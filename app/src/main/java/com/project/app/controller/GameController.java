package com.project.app.controller;

import com.project.app.api.HistoryPublic;
import com.project.app.api.rooms.*;
import com.project.app.model.ApplicationUser;
import com.project.app.model.UserGuess;
import com.project.app.model.music.Artist;
import com.project.app.model.rooms.Room;
import com.project.app.model.rooms.RoomConfig;
import com.project.app.model.rooms.RoomUser;
import com.project.app.repository.rooms.RoomUserRepository;
import com.project.app.service.*;
import com.project.app.service.music.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;

@RestController
public class GameController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private MusicService musicService;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private RoomUserRepository roomUserRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private PracticeService practiceService;


    @MessageMapping("/joinGame") // Endpoint the client sends message to when joining the game.
    // replies sent to "/topic/gameJoined/{roomID}"
    public void joinGame(@Payload JoinRoomRequest request, Principal principal) {

        String destination = "/topic/gameJoined/" + request.getRoomID();
        String username = principal.getName();
        boolean isReconnect = false;
        LyricBroadcast lyricBroadcast = null;
        GameData gameData = GameService.roomDataMap.get(request.getRoomID());
        if (gameData == null) {
            template.convertAndSend(destination, new JoinRoomPublic("[Close Window] Error : Room does not exist.", "Room N/A|The room you tried to join does not exist"));
            return;
        }
        Map<String, Integer> userConnections = gameData.getUserConnections();

        System.out.printf("USER : " + username + "has joined the game");
        if (userConnections.get(username) == null) {
            userConnections.put(username, 1);
        } else {
            userConnections.put(username, userConnections.get(username) + 1);
            lyricBroadcast = gameService.fetchReconnectionInfo(request.getRoomID());
            isReconnect = true;
        }

        // Auth user
        Optional<ApplicationUser> potentialUser = userService.getUserByUsername(username);
        if (!potentialUser.isPresent()) {
            template.convertAndSendToUser(username, "/queue/privateMessage", new JoinRoomPersonal("[Close Window] Not Joined: User does not exist"));
            // realistically shouldn't ever occur because they wouldn't get past verification if their user doesn't exist.
            return;

        }
        ApplicationUser user = potentialUser.get();

        // Check if room exists
        Optional<Room> potentialRoom = roomService.findByRoomId(request.getRoomID());
        if (potentialRoom.isEmpty()) {
            template.convertAndSend(destination, new JoinRoomPublic("[Close Window] Error : Room does not exist.", "Room N/A|The room you tried to join does not exist"));
            return;
        }
        Room room = potentialRoom.get();


        boolean isHost = false;
        List<RoomUser> listOfUsers = roomService.getRoomUsersByRoomID(request.getRoomID());
        if (room.getHostId().equals(user.getUserId())) {
            // user is hosting
//            roomService.updateIsConnected(user, true); // --> potentially not needed, keeping commented for now incase something breaks 06/12/23
            isHost = true;
        }
        if (roomService.getRoomUserByUser(user).isPresent()) {
            // user is already in room_users, probably just disconnected or refreshed page.
            roomService.updateIsConnected(user, true);
            // TODO: handleReconnection() ??

            // Inform the new user
            // TODO: Add an extra list to frontendArtistList for extra artists (ones in features etc that weren't originally added).
            Set<Artist> artistSet = GameService.roomDataMap.get(room.getRoomId()).getArtistsList();
            List<FrontendUserInfo> userList = connectionService.getUserListInfo(listOfUsers);
            List<FrontendArtist> frontendArtists = musicService.convertToFrontendArtists(artistSet.stream().toList());
            System.out.println("\n frontendArtists = " + frontendArtists + "\n");
            template.convertAndSendToUser(username, "/queue/privateMessage",
                    new JoinRoomPersonal(request.getRoomID(), room.getRoomName(),
                            room.getGameState(), null, "Joined Game.",
                            isHost, userList, username, frontendArtists));

            // Notify all users currently in lobby

            boolean allUsersConnected = userList.stream().allMatch(FrontendUserInfo::isConnected);
            String startGameString = allUsersConnected ? "[ALL_CONNECTED]" : "";
            // /\ used to signify the game can start, just appended to end of the response from here so that there is no extra calls. seems like a bonus efficiency wise.
            JoinRoomPublic response;
            String isHostString = isHost ? "[H] " : "";

            if (isReconnect) {
                response = new JoinRoomPublic(username,
                        isHostString + username + " has reconnected [RECONNECTION] ",
                        userList);
            } else {
                response = new JoinRoomPublic(username,
                        isHostString + username + " has connected [CONNECTION] " + startGameString,
                        userList);
            }
            template.convertAndSend(destination, response);
            if (isReconnect) {
                lyricBroadcast.setResponseMessage("[RECONNECTION_ROUND_INFO] lyrics for current round ");
                template.convertAndSendToUser(username, "/queue/privateMessage", lyricBroadcast);
            }
            if (!startGameString.isEmpty()) {
                RoomConfig roomConfig = roomService.getConfigSettings(room.getRoomId()).get();
//                gameService.startGameDelay(room, roomConfig);
            }
        } else {
            template.convertAndSendToUser(username, "/queue/privateMessage", new JoinRoomPersonal("[Close Window] Not Joined: User is not apart of this lobby.", "Game In Progress|You could not join the room due to the game being in progress")); // maybe add spectating later, very low priority. 06/12/23
        }
    }


    @MessageMapping("/sendGuess")
    public void validateGuess(@Payload FrontendGuess answerRequest, Principal principal) {

        Optional<Room> optCurrentRoom = roomService.findByRoomId(answerRequest.getRoomID());
        Room currentRoom = optCurrentRoom.get();
        boolean isPractice = currentRoom.isPractice();
        System.out.printf("\n GameController:ValidateGuess answerRequest = " + answerRequest + "\n");

        System.out.printf("\n [guessTime: " + answerRequest.getGuessTimeMS() + "] " + answerRequest.getArtistID() + " in " + answerRequest.getTotalGuessTimeMS() + " ms \n");
        String destination = "/topic/gameLoop/" + answerRequest.getRoomID();
        String username = principal.getName();

        // Auth user
        Optional<ApplicationUser> potentialUser = userService.getUserByUsername(username);
        ApplicationUser user;
        if (potentialUser.isPresent()) {
            user = potentialUser.get();
            if (isPractice) {
                practiceService.handlePracticeGuess(answerRequest, user, currentRoom);
            } else {
                UserGuess userGuess = gameService.checkUserGuess(answerRequest, user, currentRoom);
                Integer newScoreTotal = roomUserRepository.findScoreByUser(user);
                if (userGuess.isCorrectGuess()) {
                    List<RoomUser> listOfUsers = roomService.getRoomUsersByRoomID(answerRequest.getRoomID());
                    List<FrontendUserInfo> userList = connectionService.getUserListInfo(listOfUsers);
                    HistoryPublic newHistory = new HistoryPublic("[NEW_HISTORY]", user.getUsername(), userGuess.getAssociatedScore(), newScoreTotal, userGuess.getTotalGuessTime(), userList);
                    template.convertAndSend(destination, newHistory);
                }

            }
        }
    }
}