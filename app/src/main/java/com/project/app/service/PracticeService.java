package com.project.app.service;

import com.project.app.api.LyricData;
import com.project.app.api.RoundInformation;
import com.project.app.api.ScorePersonal;
import com.project.app.api.rooms.*;
import com.project.app.model.ApplicationUser;
import com.project.app.model.PracticeGuess;
import com.project.app.model.music.Artist;
import com.project.app.model.music.Song;
import com.project.app.model.rooms.Room;
import com.project.app.model.rooms.RoomUser;
import com.project.app.repository.PracticeGuessRepository;
import com.project.app.repository.music.ArtistRepository;
import com.project.app.repository.music.SongArtistRepository;
import com.project.app.repository.music.SongRepository;
import com.project.app.repository.rooms.RoomArtistRepository;
import com.project.app.repository.rooms.RoomUserRepository;
import com.project.app.service.music.MusicService;
import com.project.app.service.music.MusixmatchService;
import com.project.app.service.music.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class PracticeService {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private MusicService musicService;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private GameService gameService;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private RoomArtistRepository roomArtistRepository;

    @Autowired
    private SongArtistRepository songArtistRepository;

    @Autowired
    private PracticeGuessRepository practiceGuessRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private RoomUserRepository roomUserRepository;

    @Autowired
    private MusixmatchService musixmatchService;


    public void executeRound(Room room) {
        System.out.printf("\n --- EXECUTING ROUND --- \n");

        int rounds = roomService.getRoundSongCountByRoom(room.getRoomId());
        String destination = "/topic/gameLoop/" + room.getRoomId();
        int round = GameService.roomDataMap.get(room.getRoomId()).getCurrentRound();
        if (round == 0) {
            round = 1; // easiest way to fix it
        }
        GameService.roomDataMap.get(room.getRoomId()).setCurrentRound(round + 1); // increment it for next time.
        if (round > rounds) {
            System.out.printf("\n END OF GAME \n");
            endPractice(room); // Ends the game.
            return;
        }

        // stupid encoding issue causes the chance of a UnsupportedEncodingException
        RoundInformation roundInformation = GameService.roomDataMap.get(room.getRoomId()).getRoundInformationMap().get(round);
        System.out.println("\n GameService|GameLoop: RoundInformation for round (" + round + ") = " + roundInformation + "\n");

        LyricBroadcast broadcast = new LyricBroadcast(
                "[NEW_ROUND] Lyrics received",
                roundInformation,
                round,
                rounds,
                1023,
                System.currentTimeMillis()
        );
        roundInformation.setBroadcastTime(broadcast.getCurrentTimeMS());
        GameService.roomDataMap.get(room.getRoomId()).setLastBroadcast(broadcast);
        System.out.printf("\n executeRound: LyricBroadcast = " + broadcast + "\n");

        try {
            template.convertAndSend(destination, broadcast);
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.printf(" sent |" + broadcast.getLyrics() + "| to [" + destination + "] Round " + broadcast.getRoundNumber() + "/" + broadcast.getTotalRounds() + "\n");
    }

    public void endPractice(Room room) {
        // esssentially a wrapper for endGame that adds the pruning of guesses.

        postGamePruning(room.getRoomId());
        gameService.endGame(room.getRoomId());
    }


    public void handlePracticeGuess(FrontendGuess frontendGuess, ApplicationUser user, Room currentRoom) {

        // GameService: checkUserGuess code
        // fetch correct artist from the lyricData.
        System.out.println("\n handlePracticeGuess:frontendGuess = " + frontendGuess + "\n");
        String roomID = currentRoom.getRoomId();
        System.out.println("handlePracticeGuess:roomDataMap(" + roomID + ") = " + GameService.roomDataMap.get(roomID) + "\n");
        LyricData lyricData = GameService.roomDataMap.get(roomID).findAnswerData(frontendGuess.getLyricID());
        System.out.printf("\n handlePracticeGuess: lyricData = " + lyricData + "\n");
        Song song = songRepository.findSongByLyricID(lyricData.getLyricID()).get(0);
        PracticeGuess practiceGuess = savePracticeGuess(frontendGuess, song, user); // automatically places guess in database.
        int points;
        if (practiceGuess.isCorrectGuess()) {
            points = 1;
            System.out.println("\n handlePracticeGuess: points = " + points + "\n");
            roomUserRepository.addScoreByUser(user, points);
            template.convertAndSendToUser(user.getUsername(), "/queue/privateMessage", new ScorePersonal("[SCORE_PERSONAL]", points, practiceGuess.getTotalGuessTime(), true));
        } else {
            points = -1;
            template.convertAndSendToUser(user.getUsername(), "/queue/privateMessage", new ScorePersonal("[SCORE_PERSONAL]", points, practiceGuess.getTotalGuessTime(), false));
        }

        // GameController: validateGuess code.
        String destination = "/topic/gameLoop/" + currentRoom.getRoomId();
        Integer newScoreTotal = roomUserRepository.findScoreByUser(user);

        // ---- would be inside if correct, but practice only allows for one guess each.

        List<RoomUser> listOfUsers = roomService.getRoomUsersByRoomID(currentRoom.getRoomId());
        List<FrontendUserInfo> userList = connectionService.getUserListInfo(listOfUsers);
        Set<Artist> roomArtists = GameService.roomDataMap.get(roomID).getArtistsList();
        System.out.println("handlePracticeGuess: roomArtists = " + roomArtists + "\n");
        List<Artist> songArtists = musicService.getArtistListFromSongArtists(song.getSongArtists());
        System.out.println("handlePracticeGuess: songArtists = " + songArtists + "\n");
        // Create a copy of the Set to avoid modifying the original, mistake I will not make again.
        Set<Artist> copy = new HashSet<>(roomArtists);
        copy.retainAll(songArtists); // creates a list of roomArtists that are in the song, so that the response to the user makes sense.
        // .retainAll appears to leave the list empty. no clue why as of yet. RFSAF54
        System.out.println("handlePracticeGuess: copy = " + copy + "\n");
        PracticeHistory newHistory = new PracticeHistory("[NEW_HISTORY]", user.getUsername(), points, newScoreTotal, practiceGuess.getTotalGuessTime(), userList, copy);
        template.convertAndSend(destination, newHistory);
        System.out.println("\n handlePracticeGuess: newHistory = " + newHistory + "\n");
        executeRound(currentRoom);

        // ----

    }

    public PracticeGuess savePracticeGuess(FrontendGuess frontendGuess, Song song, ApplicationUser user) {
        List<Artist> potentialArtists = songArtistRepository.findArtistsBySong(song);
        System.out.printf("\n savePracticeGuess: potentialArtists = " + potentialArtists + "\n");

        PracticeGuess practiceGuess;
        if (potentialArtists.stream().anyMatch(artist -> Objects.equals(artist.getArtistId(), frontendGuess.getArtistID()))) {
            practiceGuess = new PracticeGuess(frontendGuess, song, true, user);
        } else {
            practiceGuess = new PracticeGuess(frontendGuess, song, false, user);
        }
        System.out.println("\n savePracticeGuess: practiceGuess = " + practiceGuess + "\n");
        practiceGuessRepository.save(practiceGuess);
        return practiceGuess;
    }

    public void postGamePruning(String roomID) {
        Double averageTime = practiceGuessRepository.findAverageGuessTimeByRoomID(roomID);
        if (averageTime != null) {
            Integer threshold = (int) (averageTime * 2);
            practiceGuessRepository.deleteByTotalGuessTimeGreaterThanAndRoomID(threshold, roomID);
        }
    }

    public void userSpecificPruning(ApplicationUser user) {

        practiceGuessRepository.deleteByTotalGuessTimeGreaterThanAndUser(30 * 1000, user);
        Double averageTime = practiceGuessRepository.findAverageGuessTimeByUser(user);
        if (averageTime != null && averageTime > 10) {
            Integer threshold = (int) (averageTime * 2);
            System.out.println("\n asdwdas " + threshold + "\n");
            practiceGuessRepository.deleteByTotalGuessTimeGreaterThanAndUser(threshold, user);
        }
    }
}



