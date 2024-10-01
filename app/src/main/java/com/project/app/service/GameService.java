package com.project.app.service;

import com.nimbusds.jose.jwk.source.RefreshAheadCachingJWKSetSource;
import com.project.app.api.*;
import com.project.app.api.musixmatch.MatcherLyricsResponse;
import com.project.app.api.rooms.*;
import com.project.app.model.ApplicationUser;
import com.project.app.model.PracticeGuess;
import com.project.app.model.UserGuess;
import com.project.app.model.music.Album;
import com.project.app.model.music.Artist;
import com.project.app.model.music.Song;
import com.project.app.model.rooms.RoomArchive;
import com.project.app.model.rooms.RoomConfig;
import com.project.app.model.rooms.Room;
import com.project.app.model.rooms.RoomUser;
import com.project.app.repository.PracticeGuessRepository;
import com.project.app.repository.UserGuessRepository;
import com.project.app.repository.music.ArtistRepository;
import com.project.app.repository.music.SongArtistRepository;
import com.project.app.repository.music.SongRepository;
import com.project.app.repository.rooms.*;
import com.project.app.service.music.MusicService;
import com.project.app.service.music.MusixmatchService;
import com.project.app.service.music.SpotifyService;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class GameService {

    public static Map<String, GameData> roomDataMap = new HashMap<>();

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private MusicService musicService;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private RoomArtistRepository roomArtistRepository;

    @Autowired
    private SongArtistRepository songArtistRepository;

    @Autowired
    private UserGuessRepository userGuessRepository;

    @Autowired
    private PracticeGuessRepository practiceGuessRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomConfigRepository roomConfigRepository;

    @Autowired
    private RoomUserRepository roomUserRepository;

    @Autowired
    private RoomArchiveRepository roomArchiveRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private MusixmatchService musixmatchService;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, ScheduledFuture<?>> gameSchedulers = new ConcurrentHashMap<>();

    @Async
    public void startGameDelay(Room room, RoomConfig roomConfig) {
        template.convertAndSend("/topic/gameJoined/" + room.getRoomId(), new PlainResponse("[START_GAME_TIMER]"));
        scheduler.schedule(() -> {
            gameLoop(room, roomConfig); // begins game loop after .getWaitTime() seconds.
        }, roomConfig.getWaitTime(), TimeUnit.SECONDS);

    }

    public void gameLoop(Room room, RoomConfig roomConfig) {
        System.out.printf("\n --- 1 STARTING GAME LOOP --- \n");
        System.out.printf("\n --- 2 STARTING GAME LOOP --- \n");
        System.out.printf("\n --- 3 STARTING GAME LOOP --- \n");

        int rounds = roomService.getRoundSongCountByRoom(room.getRoomId());
        String destination = "/topic/gameLoop/" + room.getRoomId();
        AtomicInteger currentRound = new AtomicInteger(1);

        final ScheduledFuture<?>[] gameTask = new ScheduledFuture<?>[1]; // "Variable gameTask may not be initalised" has caused this monstrosity of a workaround.

        gameTask[0] = scheduler.scheduleAtFixedRate(() -> {
            System.out.printf("\n Inside scheduler loop \n");
            int round = currentRound.getAndIncrement();
            System.out.println("\n GameService|GameLoop: round = " + round + "\n");
            if (round > rounds) {
                System.out.printf("\n END OF GAME \n");
                gameTask[0].cancel(false); // cancels task -->  a workaround for this looks messy, only a minor error
                endGame(room.getRoomId()); // Ends the game.
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
                    roomConfig.getMaxGuessTime(),
                    System.currentTimeMillis()
            );
            roundInformation.setBroadcastTime(broadcast.getCurrentTimeMS());
            GameService.roomDataMap.get(room.getRoomId()).setLastBroadcast(broadcast);
            System.out.printf("\n gameLoop: LyricBroadcast = " + broadcast + "\n");
            try {
                template.convertAndSend(destination, broadcast);
            } catch (Exception e) {
                System.err.println("Failed to send message: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.printf(" sent |" + broadcast.getLyrics() + "| to [" + destination + "] Round " + broadcast.getRoundNumber() + "/" + broadcast.getTotalRounds() + "\n");

        }, 0, Math.round(roomConfig.getMaxGuessTime()), TimeUnit.SECONDS);

        gameSchedulers.put(room.getRoomId(), gameTask[0]);
    }

//    public RoundInformation getLyrics(Room room, int round) throws UnsupportedEncodingException {
//        /*
//         * Fetch a song from the rooms associated List<Song>
//         * Some sort of algorithm to rank the lines in the song and take a suitable line along with a value indicating a multiplier for how rare it is.
//         * return both to here
//         * return lyrics
//         */
//        round -= 1; // accounting for array access.
//        System.out.printf("\n INSIDE GET LYRICS \n");
//        Random random = new Random();
//        GameData gameData = GameService.roomDataMap.get(room.getRoomId());
//        List<Song> songList = new ArrayList<>(gameData.getSongList());
//        System.out.println("\n getLyrics:songListSize = " + songList.size() + "\n");
//        System.out.printf("\n getLyrics:songList = " + songList + "\n");
//        Map<String, List<Album>> albumArtistMap = gameData.getAlbumArtistMap();
//        System.out.println("\n getLyrics:albumArtistMap = " + albumArtistMap + "\n");
//        MatcherLyricsResponse songLyricInformation = null;
//        Song chosenSong = null;
//        List<LyricData> suitableLyricList = new ArrayList<>();
//
//        while (suitableLyricList.size() < 1) {
//            System.out.printf("\n getLyrics: getting lyrics for round " + round + " \n");
//            chosenSong = songList.get(round);
//            System.out.println("\n getLyrics: song for round = " + chosenSong + "\n");
//
//            if (chosenSong.getLyrics().isEmpty()) {
//                System.out.printf("\n getLyrics:chosenSong = " + chosenSong.getName());
//
//                songLyricInformation = musixmatchService.fetchLyrics(chosenSong);
//                System.out.println("\n getLyrics: songLyricInformation : " + songLyricInformation + "\n");
//
//                if (songLyricInformation != null) {
//                    System.out.println("getLyrics:songLyricInformation = " + songLyricInformation.getMessage().getBody().getLyrics().toString() + "\n");
//                    chosenSong.addLyricInformation(songLyricInformation);
//                    System.out.println("\n getLyrics:ChosenSong with lyric information (~L178)= " + chosenSong + "\n");
//                    songRepository.save(chosenSong);
//                } else {
//                    System.out.println("\n getLyrics: songLyricInformation detected as null \n");
//                    List<Song> spareSongs = gameData.getSpareSongs();
//                    if (!spareSongs.isEmpty()) {
//                        songList.set(round, spareSongs.get(random.nextInt(spareSongs.size())));
//                    } else {
//                        List<Artist> selectedArtists = gameData.getSelectedArtists();
//                        System.out.println("\n Selected Artists: " + selectedArtists);
//                        int randomIndex = random.nextInt(selectedArtists.size());
//                        System.out.println("\n Random Index: " + randomIndex);
//                        Artist randomArtist = selectedArtists.get(randomIndex);
//                        System.out.println("\n Random Artist: " + randomArtist);
//                        songList.set(round, spotifyService.chooseSongFromAlbumList(albumArtistMap.get(randomArtist.getArtistId()), new ArrayList<>(GameService.roomDataMap.get(room.getRoomId()).getArtistsList())));
//                        System.out.println("\n getLyrics: song | " + chosenSong + " | changed to | " + songList.get(round) + "| \n");
//                    }
//                }
//            } else {
//                System.out.println("\n getLyrics:ChosenSong with lyric information (outside while loop) (~L197) = " + chosenSong + "\n");
//                List<String> lyricList = Arrays.stream(chosenSong.getLyrics().split("\n"))
//                        .filter(s -> Pattern.matches(".*[a-zA-Z0-9]+.*", s))
//                        .toList();
//                songRepository.updateLastAccessedTimeByLyricID(chosenSong.getLyricID(), LocalDateTime.now());
//
//                suitableLyricList = musicService.findSuitableLyrics(lyricList, chosenSong.getLyricID());
//            }
//
//
//        }
//
//        LyricData chosenLyrics = suitableLyricList.get(random.nextInt(suitableLyricList.size()));
//        double lyricWeight = musicService.calculateLyricWeight(chosenLyrics);
//        chosenLyrics.setWeight(lyricWeight);
//        System.out.printf("\n getLyrics:lyricData = " + chosenLyrics + "\n ");
//        chosenLyrics.setArtistList(musicService.getArtistListFromSongArtists(chosenSong.getSongArtists()));
//        GameService.roomDataMap.get(room.getRoomId()).addLyricData(chosenLyrics);
//        System.out.println("getLyrics:roomDataMap(" + room.getRoomId() + ") = " + GameService.roomDataMap.get(room.getRoomId()) + "\n");
//
//        return new RoundInformation(chosenSong, chosenLyrics);
//    }

    public LyricBroadcast fetchReconnectionInfo(String roomID) {

        return GameService.roomDataMap.get(roomID).getLastBroadcast();
    }

    public UserGuess checkUserGuess(FrontendGuess frontendGuess, ApplicationUser user, Room room) {

        // fetch correct artist from the lyricData.
        System.out.println("\n checkUserGuess:frontendGuess = " + frontendGuess + "\n");
        String roomID = room.getRoomId();
        System.out.println("checkUserGuess:roomDataMap(" + roomID + ") = " + GameService.roomDataMap.get(roomID) + "\n");
        LyricData lyricData = GameService.roomDataMap.get(roomID).findAnswerData(frontendGuess.getLyricID());
        System.out.printf("\n checkUserGuess: lyricData = " + lyricData + "\n");
        Song song = songRepository.findSongByLyricID(lyricData.getLyricID()).get(0);
        UserGuess userGuess = saveUserGuess(frontendGuess, song, user); // automatically places guess in database.
        // 1. If correct
        // - calculate points based on time taken, weight, guessCount;
        // - send information back to whole room. points gained and by who.
        // 2. Incorrect - send info back to user.
        if (userGuess.isCorrectGuess()) {
            int points = calculatePointValue(userGuess);
            System.out.println("\n checkUserGuess: points = " + points + "\n");
            userGuess.setAssociatedScore(points);
            roomUserRepository.addScoreByUser(user, points);
            Artist chosenArtist = artistRepository.findByArtistId(frontendGuess.getArtistID()).get();
            template.convertAndSendToUser(user.getUsername(), "/queue/privateMessage", new ScorePersonal("[SCORE_PERSONAL]", points, userGuess.getTotalGuessTime(), true, chosenArtist.getName(), song.getName(), chosenArtist.getArtURL()));
        } else {
            template.convertAndSendToUser(user.getUsername(), "/queue/privateMessage", new ScorePersonal("[SCORE_PERSONAL]", -1, userGuess.getTotalGuessTime(), false));
        }
        return userGuess;

    }


    public UserGuess saveUserGuess(FrontendGuess answerGuess, Song song, ApplicationUser user) {

        List<Artist> potentialArtists = songArtistRepository.findArtistsBySong(song);
        UserGuess userGuess;
        if (potentialArtists.stream().anyMatch(artist -> Objects.equals(artist.getArtistId(), answerGuess.getArtistID()))) {
            userGuess = new UserGuess(answerGuess, song, true, user);
        } else {
            userGuess = new UserGuess(answerGuess, song, false, user);
        }
        userGuessRepository.save(userGuess);
        return userGuess;
    }


    public int calculatePointValue(UserGuess userGuess) {
        /*
        Allocates points between 50 and 200.
        calculates a base from a decay function, dont know specifics had to mess around to get numbers that seemed right for score.

        */
        System.out.println("\n calculatePointValue : userGuess = " + userGuess + "\n");
        final int maxPoints = 200;
        final int minPoints = 50;
        final float maxFullPointTime; // Maximum time (in ms) for full points

        long timeTaken = userGuess.getTotalGuessTime(); // Time taken for the guess in seconds (x1000 later on)
        int guessCount = userGuess.getGuessCount(); // Number of guesses made

        GameData gameData = GameService.roomDataMap.get(userGuess.getRoomID());
        float weight = gameData
                .getAnswers()
                .get(userGuess.getSong().getLyricID())
                .getWeight();
        System.out.println("\n calculatePointValue : maxGuessTime = " + (gameData.getMaxGuessTime() * 1000) + "\n");
        maxFullPointTime = gameData.getMaxGuessTime() / 3;
        System.out.println("\n calculatePointValue : maxFullPointTime = " + maxFullPointTime + "\n");

        final double k = -Math.log(minPoints / (double) maxPoints) / (0.8 * (gameData.getMaxGuessTime() * 1000));
        System.out.println("\n calculatePointValue : k = " + k + "\n");


        // Using exponential decay function for time-based points
        double timeFactor = Math.exp(-k * timeTaken);
        System.out.println("\n calculatePointValue : timeFactor = " + timeFactor + "\n");
        int timePoints = (int) (maxPoints * timeFactor);
        System.out.println("\n calculatePointValue: raw timePoints = " + timePoints + "\n");

        // applies penalty (5% per guess) off the total
        int totalPoints = Math.min(Math.max(timePoints, minPoints), maxPoints);
        totalPoints = (int) ((weight * totalPoints) * ((1 - (0.05 * guessCount - 1))));
        // adjusted so penalty is taken after the bounds are checked (29/02/24)

        // Ensure points are within the specified range
        return Math.min(Math.max(totalPoints, minPoints), maxPoints);
    }

    public void endGame(String roomID) {
        // clear out room, roomConfig, roomArtist, roomSong, roomUser tables.
        Optional<Room> optRoom = roomService.findByRoomId(roomID);
        System.out.println("\n " + optRoom.isPresent() + "\n");
        if (optRoom.isPresent()) {
            System.out.println("\n GameService|EndGame: inside optRoom.isPresent() if statement + \n");
            Room room = optRoom.get();
            List<RoomUser> ruList = roomService.getRoomUsersByRoomID(roomID);
            RoomConfig rc = roomService.getConfigSettings(roomID).get(); // shameless .get() without isPresent(), surely it wouldnt error here if room exists so does roomConfig.
            List<ApplicationUser> userList = ruList.stream()
                    .map(RoomUser::getUser)
                    .collect(Collectors.toList());
            GameData gd = GameService.roomDataMap.get(roomID);
            System.out.println("\n gd = " + gd + "\n");
            if (gd != null) {
                System.out.println("\n GameService|endGame : if statement reached gd != null");
                RoomArchive roomArchive = new RoomArchive(room, rc, userList, gd);
                roomArchiveRepository.save(roomArchive);
                roomUserRepository.deleteByRoomId(roomID);
                musicService.clearRoomArtistsByRoom(roomID);
                roomConfigRepository.deleteByRoomId(roomID);
                roomRepository.deleteByRoomId(roomID);
                GameService.roomDataMap.remove(roomID);
            } else {
                System.out.println("\n GameService|endGame : else statement reached gd == null");
            }

        } else {
            System.out.println("\n GameService|endGame: ERROR : Room not Present... \n");
        }

        template.convertAndSend("/topic/gameLoop/" + roomID, new PlainResponse("[GAME_END] Implementation not added yet. 13/12/23"));
        System.out.println("\n GameService|endGame :");
        gameSchedulers.remove(roomID);
    }

    @PreDestroy
    public void endEverything() {
        // ONLY USED ON APPLICATION SHUTDOWN -> not for specific games.
        scheduler.shutdown();
    }

    public void loadSongMap(String roomID) throws UnsupportedEncodingException {
        String destination = "/topic/gameJoined/" + roomID;
        List<Artist> artists = roomArtistRepository.findArtistsByRoomId(roomID);
        template.convertAndSend(destination, new PlainResponse("[LOADING_ALBUMS]"));
        List<List<Album>> artistAlbums = spotifyService.loadAlbums(roomID, artists);
        List<List<Song>> artistSongs = null;
        System.out.printf("loadSongMap: artistSongs = " + artistSongs + "\n");
        Set<Song> songs = new HashSet<>();
        int songCount = roomService.getRoundSongCountByRoom(roomID);
        System.out.println("\n loadSongMap: songCount = " + songCount + "\n");
        Song chosenSong = null;
        Room room = roomRepository.findByRoomId(roomID).get(); // uh-oh no isPresent, luckily this function will have broke so much if the room doesnt exist by this point it should be fine. (01/03/24)
        Map<Integer, RoundInformation> roundInformationMap = new HashMap<>();
        while (songs.size() < songCount) {
            template.convertAndSend(destination, new SelectingSongResponse("[SELECTING_SONGS]", songs.size() + 1, songCount));
            if (!artistAlbums.isEmpty()) {
                if (spotifyService.getAvailableCallCount() > SpotifyService.RESERVE_CALLS || artistSongs.isEmpty()) { // if api usage isnt near cap, then gets from spotify to ensure it can collect uncached songs.
                    WrappedSongChoice songChoice = spotifyService.chooseSongFromRoomAlbums(artistAlbums, artists, roomID);
                    if (songChoice.getChosenSong() != null) {
                        chosenSong = songChoice.getChosenSong();
                    } else {
                        // case 1: artistAlbums is empty
                        if (songChoice.getArtistAlbumIndex() == -1) {
                            // no more albums left. uh oh.
                            if (songs.size() > 1) {
                                // game can play, just gonna be smaller. need to update round in database to account for this.
                                GameService.roomDataMap.get(roomID).setRoundCount(songs.size());
                                songCount = songs.size();
                            } else {
                                template.convertAndSend(destination, new PlainResponse("[NO_ALBUMS_ERROR]", "No Valid Songs|The list of artists provided couldnt result in a playabler experience. This is either a issue collecting information or more / different artists are required."));
                            }
                        }
                        // case 2: ran out of albums for a specific artist
                        if (songChoice.getAlbumIndex() == -1) {
                            artistAlbums.get(songChoice.getArtistAlbumIndex()).remove(songChoice.getAlbumIndex());
                        }
                    }
                } else {
                    // grabs from spares to avoid more spotify calls
                    artistSongs = musicService.loadSongsFromDatabase(artists); // moved to here to prevent calling it unnessecarily (might break stuff idk yet 01/03/24)
                    chosenSong = musicService.selectRandomSong(artistSongs);
                }
                if (chosenSong != null && chosenSong.getName().toLowerCase().contains("instrumental")) {
                    // instrumentals have no lyrics. might as well get a new song.
                    chosenSong = null;
                }
                if (!songs.contains(chosenSong) && chosenSong != null) {

                    RoundInformation potentialRoundInformation = getLyricsBySong(chosenSong, room, songs.size() + 1);
                    if (potentialRoundInformation != null) {
                        System.out.println("\n loadSongMap: song added to the list with valid lyrics = " + chosenSong.getName() + "\n");
                        songs.add(chosenSong);
                        GameService.roomDataMap.get(roomID).addSong(chosenSong);
                        roundInformationMap.put(songs.size(), potentialRoundInformation);
                    }
                    // else if the roundInformation is returned as null the song isn't acceptable and needs to be rechosen
                }
            } else {
                template.convertAndSend(destination, new PlainResponse("[NO_ALBUMS_ERROR]", "No Albums Found|The list of artists provide failed to generate albums. This is probably a connection to spotify error, sorry about this one."));
                // no albums could be recieved from spotify. multiple causes for this

            }
        }
        System.out.printf("\n loadSongMap:songs = " + songs + "\n");
//        GameService.roomDataMap.get(roomID).setSongList(songs); TODO: add song one by one
        System.out.println("\n loadSongMap: added songs to dataMap \n");
        GameService.roomDataMap.get(roomID).setAlbumArtistMap(artistAlbums, artists);
        System.out.println("\n loadSongMap: added albumArtistMap to dataMap \n");
        List<Song> spareSongs = new ArrayList<>();
        if (artistSongs != null && !artistSongs.isEmpty()) {
            for (List<Song> songList : artistSongs) {
                for (Song song : songList) {
                    if (!songs.contains(song) && !spareSongs.contains(song)) {
                        spareSongs.add(song);
                    }
                }
            }
        }
        GameService.roomDataMap.get(roomID).setSpareSongs(spareSongs);
        GameService.roomDataMap.get(roomID).setRoundInformationMap(roundInformationMap);
        System.out.println("\n loadSongMap: added spareSongs to dataMap \n");
    }



    public RoundInformation getLyricsBySong(Song song, Room room, int round) throws UnsupportedEncodingException {
        Random random = new Random();
        GameData gameData = GameService.roomDataMap.get(room.getRoomId());
        Map<String, List<Album>> albumArtistMap = gameData.getAlbumArtistMap();
        System.out.println("\n getLyrics:albumArtistMap = " + albumArtistMap + "\n");
        MatcherLyricsResponse songLyricInformation;
        List<LyricData> suitableLyricList;
        if (song.getLyrics().isEmpty()) {
            System.out.printf("\n getLyricsBySong: read song lyrics as empty in database so searching for them in musixmatch:  " + song.getName());

            songLyricInformation = musixmatchService.fetchLyrics(song);
            System.out.println("\n getLyricsBySong: songLyricInformation : " + songLyricInformation + "\n");

            if (songLyricInformation != null) {
                System.out.println("getLyricsBySong:songLyricInformation = " + songLyricInformation.getMessage().getBody().getLyrics().toString() + "\n");
                song.addLyricInformation(songLyricInformation);
                System.out.println("\n getLyricsBySong:ChosenSong with lyric information = " + song.getName() + "\n");
                song.setLastAccessed(LocalDateTime.now());
                songRepository.save(song);
                List<String> lyricList = Arrays.stream(song.getLyrics().split("\n"))
                        .filter(s -> Pattern.matches(".*[a-zA-Z0-9]+.*", s))
                        .toList();
                suitableLyricList = musicService.findSuitableLyrics(lyricList, song.getLyricID());
            } else {
                return null;
            }
        } else {
            System.out.println("\n getLyrics:ChosenSong had lyric information in database = " + song + "\n");
            List<String> lyricList = Arrays.stream(song.getLyrics().split("\n"))
                    .filter(s -> Pattern.matches(".*[a-zA-Z0-9]+.*", s))
                    .toList();
            songRepository.updateLastAccessedTimeByLyricID(song.getLyricID(), LocalDateTime.now());
            suitableLyricList = musicService.findSuitableLyrics(lyricList, song.getLyricID());
        }
        if (suitableLyricList.size() < 1) {
            // no suitable lyrics, so the rest of this function wouldn't work anyway.
            return null;
        }
        LyricData chosenLyrics = suitableLyricList.get(random.nextInt(suitableLyricList.size()));
        double lyricWeight = musicService.calculateLyricWeight(chosenLyrics);
        chosenLyrics.setWeight(lyricWeight);
        System.out.printf("\n getLyricsBySong:lyricData = " + chosenLyrics + "\n ");
        chosenLyrics.setArtistList(musicService.getArtistListFromSongArtists(song.getSongArtists()));
        GameService.roomDataMap.get(room.getRoomId()).addLyricData(chosenLyrics);
        System.out.println("getLyricsBySong:roomDataMap(" + room.getRoomId() + ") = " + GameService.roomDataMap.get(room.getRoomId()) + "\n");

        return new RoundInformation(song, chosenLyrics);
    }
}

/*
 * TODO:
 *
 * NEXT THING TO IMPLEMENT : history tab shows both correct and incorrect guesses.
 *  RELATED BUGS :
 *      Always shows incorrect for some reason --> even though score being sent through is correct.
 *      ArtistList and artistArt are empty, even before being sent to frontend. see PracticeService.java CTRLF "RFSAF54"
 *
 *
 * ONCE Practice mode is working enough, move on to Graph analysis page. should be a personal stats section for each user,
 *
 * BUG: Refreshing pages causes alot of issues, resetting text and for some reason breaks timer forever --> userConnections is never assigned to stuff from gameController:joinGame. may be useful info for whenever I fix it.
 * BUG: Once Post-Game, room doesnt seem to be wiped so anyone involved open a new room.
 *
 * Stop user from starting game without artists, pretty sure it will start it fine then break. (untested), needed in both practice and game
 * 404 error that occurs through musixmatch needs to be tracked down, it halts the loop somewhere.
 * (A) Figure out why songs are not only being saved if they are the original. should be sorted out in ...songByHref() but clearly not. Song entries are identical except spotifyID, hopefully due to them being the 'non original'.
 * Currently a bug where a song can be picked more than once. need to look into that.
 * Seems to be some errors when searching that includes a space and some backspaces.
 *
 *
 * */

