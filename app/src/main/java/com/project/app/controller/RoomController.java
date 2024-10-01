package com.project.app.controller;

import com.project.app.api.rooms.*;
import com.project.app.model.ApplicationUser;
import com.project.app.model.music.Artist;
import com.project.app.model.rooms.Room;
import com.project.app.model.rooms.RoomConfig;
import com.project.app.model.rooms.RoomUser;
import com.project.app.repository.rooms.RoomConfigRepository;
import com.project.app.repository.rooms.RoomRepository;
import com.project.app.repository.rooms.RoomUserRepository;
import com.project.app.service.ConnectionService;
import com.project.app.service.GameService;
import com.project.app.service.RoomService;
import com.project.app.service.UserService;
import com.project.app.service.music.MusicService;
import com.project.app.service.music.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.*;

@RestController
public class RoomController {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomUserRepository roomUserRepository;

    @Autowired
    private RoomConfigRepository roomConfigRepository;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private MusicService musicService;

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private GameService gameService;

    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @GetMapping("/room/create")
    public CreateRoomResponse createRoom() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication == null) {
            return new CreateRoomResponse(null, "Error creating Room: user not authenticated");
        }

        String username = authentication.getName();
        Optional<ApplicationUser> potentialUser = userService.getUserByUsername(username);
        if (potentialUser.isEmpty()) {
            return new CreateRoomResponse(null, "Error creating Room: User does not exist anymore, shouldnt really happen");
        }
        // check if user already hosts a room.
        ApplicationUser user = potentialUser.get();
        Integer hostID = user.getUserId();
        Optional<Room> hostRoom = roomService.findByHostID(hostID);
        System.out.println("\n RoomController|CreateRoom : hostRoomPresent = " + hostRoom.isPresent() + "\n");
        // TODO: This could cause some crazy issues, but shouldn't as if the user is hosting a room he should be in the lobby.
        hostRoom.ifPresent(room -> connectionService.closeRoom(room)); // somehow hostRoom can be referenced as room cause of the arrow or something, Intellij kinda wilding on this refactor.
        if (hostRoom.isPresent()) {
            return new CreateRoomResponse(null, "[OLD_ROOM_DELETED]", "Previous Room Closed|You were listed as the current host of a room. This was probably due to incorrect closure, and has been sorted. you can create a new room now.");

        }
        String roomID = generateRandomString(8);
        System.out.println("\n RoomController|CreateRoom : attempting to create room as no old room existed. id = " + roomID + "\n");
        String roomName = username + "'s Lobby";
        String gameState = "lobby";
        Boolean isPrivate = false;

        Room newRoom = new Room(roomID, roomName, gameState, hostID);
        Room createdRoom = roomService.createRoom(newRoom);
        System.out.println("\n RoomController|CreateRoom : createdRoomID = " + createdRoom.getRoomId() + "\n");
        RoomConfig defaultConfig = new RoomConfig(createdRoom);
        defaultConfig = roomService.saveConfig(defaultConfig);
        Optional<Room> savedRoom = roomRepository.findByRoomId(roomID);
        System.out.println("\n RoomController|CreateRoom : RoomPresentInDatabase = " + savedRoom.isPresent() + "\n");


        RoomUser entry = new RoomUser(user, newRoom, true, false);
        roomUserRepository.save(entry);

        return new CreateRoomResponse(roomID, roomName + ": Room Created Successfully");
    }

    @GetMapping("/room/practice")
    public CreateRoomResponse createPractice() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return new CreateRoomResponse(null, "Error creating Room: user not authenticated");
        }

        String username = authentication.getName();
        Optional<ApplicationUser> potentialUser = userService.getUserByUsername(username);
        if (potentialUser.isEmpty()) {
            return new CreateRoomResponse(null, "Error creating Room: User does not exist anymore, shouldnt really happen");
        }
        // check if user already hosts a room.
        ApplicationUser user = potentialUser.get();
        Integer hostID = user.getUserId();
        Optional<Room> hostRoom = roomService.findByHostID(hostID);
        // TODO: This could cause some crazy issues, but shouldn't as if the user is hosting a room he should be in the lobby.
        hostRoom.ifPresent(room -> connectionService.closeRoom(room));

        String roomID = generateRandomString(8);
        String roomName = username + "'s Practice";
        String gameState = "lobby";

        Room newRoom = new Room(roomID, roomName, gameState, hostID, true);
        Room createdRoom = roomService.createRoom(newRoom);
        RoomConfig practiceConfig = new RoomConfig(createdRoom, true);
        practiceConfig = roomService.saveConfig(practiceConfig);

        RoomUser entry = new RoomUser(user, createdRoom, true, false);
        roomUserRepository.save(entry);

        return new CreateRoomResponse(roomID, roomName + ": Room Created Successfully");
    }


    @GetMapping("/room/searchArtists")
    public List<FrontendArtist> searchArtists(String artistName) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isHost = verifyHost(username);
        if (!isHost) {
            return null;
        }
        System.out.println("\n searchArtists: artistName = " + artistName + "\n");
        List<Artist> artistList = spotifyService.findArtist(artistName, 5);
        return musicService.convertToFrontendArtists(artistList);
    }

    @MessageMapping("/joinRoom") // Endpoint the client sends messages to
    // replies sent to "/topic/roomJoined/{roomID}"
    public void joinRoom(@Payload JoinRoomRequest request, Principal principal) {
        System.out.printf("\nroomID = " + request.getRoomID());

        String destination = "/topic/roomJoined/" + request.getRoomID();
        String username = principal.getName();

        // 1. Auth user
        Optional<ApplicationUser> potentialUser = userService.getUserByUsername(username);
        if (!potentialUser.isPresent()) {
            template.convertAndSendToUser(username, "/queue/privateMessage", new JoinRoomPersonal("[Close Window] Not Joined: User does not exist", "Well Done|This error really shouldnt happen, re log in I guess should fix it."));
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

        // Check roomType is correct
        System.out.println("\n RoomController|JoinRoom: gameType = " + request.getGameType() + ", room.isPractice = " + room.isPractice() + "\n");
        if (Objects.equals(request.getGameType(), "practice") && !room.isPractice()) {
            template.convertAndSendToUser(username, "/queue/privateMessage", new JoinRoomPersonal("[SWITCH_MODE] This room is for a multiplayer game. not practice.", "Wrong Room Type|You have been redirected to a lobby as you tried to join a multiplayer room via the practice page."));
            return;
        }
        if (Objects.equals(request.getGameType(), "game") && room.isPractice()) {
            if (room.getHostId().equals(user.getUserId())) {
                template.convertAndSendToUser(username, "/queue/privateMessage", new JoinRoomPersonal("[SWITCH_MODE] This room is meant for practice, and you are in the game directory", "Wrong Room Type|You have been redirected to a practice lobby as you tried to join a practice lobby via the multiplayer lobby, and as its your practice you have been directed.."));
                return;
            }
            template.convertAndSendToUser(username, "/queue/privateMessage", new JoinRoomPersonal("[CLOSE_WINDOW] This room is made for practice, and not you", "Attempted to join anothers practice|You attempted to join someones practice lobby. If you want to make your own its right on the home page."));
            return;
        }
        else if (Objects.equals(request.getGameType(), "practice") && room.isPractice()) {
            if (!room.getHostId().equals(user.getUserId())) {
                template.convertAndSendToUser(username, "/queue/privateMessage", new JoinRoomPersonal("[CLOSE_WINDOW] This room is made for practice, and not you", "Attempted to join anothers practice|You attempted to join someones practice lobby. If you want to make your own its right on the home page."));
                return;
            }
        }

        // Check room has space
        Integer maxPlayers = roomService.getMaxPlayers(request.getRoomID());
        Long playerCount = roomService.getPlayerCount(request.getRoomID());
        if (maxPlayers <= playerCount) {
            template.convertAndSendToUser(username, "/queue/privateMessage", new JoinRoomPersonal("[Close Window] Not Joined: Lobby is already full. (max Players = " + maxPlayers + ")", "Max Players Reached|The room you tried to join is currently full"));
            return;
        }

        // Check if user is host (will already be in the roomUser table, just need to set him to connected)
        boolean isHost = false;
        List<RoomUser> listOfUsers = roomService.getRoomUsersByRoomID(request.getRoomID());
        if (room.getHostId().equals(user.getUserId())) {
            // user is hosting
            roomService.updateIsConnected(user, true);
            isHost = true;
        }
        if (roomService.getRoomUserByUser(user).isPresent()) {
            // user is already in room_users, probably just disconnected or refreshed page.
            roomService.updateIsConnected(user, true);
            // TODO: handleReconnection() ??

        } else {
            // Add user to room_users table
            RoomUser entry = new RoomUser(user, room, false, true);
            roomUserRepository.save(entry);
            listOfUsers.add(entry);
        }

        // Inform the new user
        Optional<RoomConfig> optConfig = roomService.getConfigSettings(request.getRoomID());
        RoomConfig configSettings = optConfig.get();
        List<FrontendUserInfo> userList = connectionService.getUserListInfo(listOfUsers);
        List<FrontendArtist> artistList = roomService.getArtistsForRoom(room.getRoomId());
        System.out.println("\n joinRoom:artistList =" + artistList + "\n");
        FrontendConfig config = new FrontendConfig(artistList, configSettings);

        template.convertAndSendToUser(username, "/queue/privateMessage",
                new JoinRoomPersonal(request.getRoomID(), room.getRoomName(),
                        room.getGameState(), config, "Joined Room.",
                        isHost, userList, username, artistList));

        // Notify all users currently in lobby
        JoinRoomPublic response;
        if (isHost) {
            response = new JoinRoomPublic(user.getUsername(),
                    "[H] " + user.getUsername() + " has joined the Room",
                    userList);
        } else {
            response = new JoinRoomPublic(user.getUsername(),
                    user.getUsername() + " has joined the Room",
                    userList);

        }
        template.convertAndSend(destination, response);

    }

    @MessageMapping("/heartbeat")
    public void readHeartbeat(@Payload Heartbeat hb, Principal principal) {
        String username = principal.getName();
        connectionService.receiveHeartbeat(username);
//        System.out.printf("recieved heartbeat");
    }

    @MessageMapping("/disconnect")
    // replies sent to "/topic/roomJoined/{roomID}" through the handleDisconnection function.
    public void handleDisconnect(Principal principal) {
        connectionService.handleDisconnection(principal.getName(), true);

    }

    @MessageMapping("/reconnect")
    public void handleReconnect(Principal principal) {
        connectionService.handleReconnection(principal.getName());
    }


    @MessageMapping("/changeSetting")
    public void handleSettingChange(Principal principal, @Payload SettingChangeRequest request) {
        String roomID = request.getRoomID();
        String settingName = request.getSettingName();
        Object newValue = request.getSettingValue();
        System.out.println("Room ID: " + roomID + "\nSetting Name: " + settingName + "\nNew Value: " + newValue + "\nValueClass: " + newValue.getClass());

        Optional<ApplicationUser> user = userService.getUserByUsername(principal.getName());
        Integer hostID;
        Optional<Room> optionalRoom = roomService.findByRoomId(roomID);
        if (optionalRoom.isPresent()) {
            hostID = optionalRoom.get().getHostId();
            if (user.isPresent()) {
                if (user.get().getUserId().equals(hostID)) {
                    // user is validated as host. is allowed to change settings (this is just an error catch as frontend should prevent this if implemented successfully).
                    String returnString = roomService.updateConfigSetting(optionalRoom.get().getRoomId(), settingName, newValue);
                    template.convertAndSendToUser(principal.getName(), "/queue/privateMessage", new SettingResponse(returnString, settingName));
                }
            }
        } else {
            template.convertAndSendToUser(principal.getName(), "/queue/privateMessage", new SettingResponse("[Config] Error : invalid room", settingName, "Invalid Room|The room you are currently in appears to of been removed due to an error, probably best to make a new one to fix any issues that could arise."));
        }

    }

    @MessageMapping("/addArtist")
    public void addArtist(Principal principal, @Payload ArtistRequest request) {

        if (verifyHost(principal)) {
            String destination = "/topic/roomJoined/" + request.getRoomID();
            List<Artist> currentArtists = musicService.getRoomArtists(request.getRoomID());
            if (currentArtists.contains(request.getArtist())) {
                return;
            }
            musicService.addRoomArtist(request.getRoomID(), spotifyService.getFullArtistDetails(request.getArtist().getSpotifyID()));
            List<Artist> artistList = musicService.getRoomArtists(request.getRoomID());
            List<FrontendArtist> frontendArtists = musicService.convertToFrontendArtists(artistList);
            template.convertAndSend(destination, new AddArtistResponse("[ADD_ARTIST] " + request.getArtist().getName() + " has been added", frontendArtists));
        }

    }

    @MessageMapping("/removeArtist")
    public void removeArtist(Principal principal, @Payload ArtistRequest request) {
        if (verifyHost(principal)) {
            // TODO: (22/12/23) adjust to account for frontendArtist in artistRequest.
            String destination = "/topic/roomJoined/" + request.getRoomID();
            musicService.removeRoomArtist(request.getRoomID(), spotifyService.getFullArtistDetails(request.getArtist().getSpotifyID()));
            List<Artist> artistList = musicService.getRoomArtists(request.getRoomID());
            List<FrontendArtist> frontendArtists = musicService.convertToFrontendArtists(artistList);
            template.convertAndSend(destination, new AddArtistResponse("[REMOVE_ARTIST] " + request.getArtist().getName() + " has been removed", frontendArtists));

        }
    }

    public boolean verifyHost(String username) {
        Optional<ApplicationUser> optionalUser = userService.getUserByUsername(username);
        if (optionalUser.isPresent()) {
            ApplicationUser user = optionalUser.get();
            Optional<RoomUser> optionalRoomUser = roomService.getRoomUserByUser(user);
            if (optionalRoomUser.isPresent()) {
                if (optionalRoomUser.get().getIsHost()) return true;
            }
        }
        template.convertAndSendToUser(username, "/queue/privateMessage", new PlainResponse("You are not the host"));
        return false;
    }

    public boolean verifyHost(Principal principal) {
        // prevent duplicating code in the use case where the things acquired during this code being ran isn't required for the rest of the function. (ApplicationUser and RoomUser).
        String username = principal.getName();
        return verifyHost(username);
    }

    @MessageMapping("/broadcastGameStart")
    public void broadcastGameStart(Principal principal) throws UnsupportedEncodingException {

        // 1. check user is host just incase.
        String username = principal.getName();
        Optional<ApplicationUser> optionalUser = userService.getUserByUsername(username);
        if (optionalUser.isPresent()) {
            ApplicationUser user = optionalUser.get();
            Optional<RoomUser> optionalRoomUser = roomService.getRoomUserByUser(user);
            if (optionalRoomUser.isPresent()) {
                boolean isHost = optionalRoomUser.get().getIsHost();
                if (!isHost) {
                    template.convertAndSendToUser(username, "/queue/privateMessage", new PlainResponse("You are not the host"));
                }
                RoomUser roomUser = optionalRoomUser.get();
                Room currentRoom = roomUser.getRoom();
                String roomID = currentRoom.getRoomId();
                String destination = "/topic/roomJoined/" + roomID;
                template.convertAndSend(destination, new BroadcastStartGameRequest("[Game Starting] started by the Host", roomID));
                List<RoomUser> roomUserList = roomService.getRoomUsersByRoomID(roomID);
                for (RoomUser ru : roomUserList) {
                    ApplicationUser u = ru.getUser();
                    roomService.updateIsConnected(u, false); // sets all the users to not connected, so they can be read as connected when joining through the game URL.
                }
                roomService.updateGameState(currentRoom.getRoomId(), "game");
                template.convertAndSend(destination, new PlainResponse("[LOADING_RESOURCES]"));
                GameService.roomDataMap.put(roomID, new GameData());
                gameService.loadSongMap(roomID);
                Optional<RoomConfig> roomConfig = roomConfigRepository.findByRoom_roomId(roomID);
                if (roomConfig.isPresent()) {
                    GameService.roomDataMap.get(roomID).setMaxGuessTime(roomConfig.get().getMaxGuessTime());
                    gameService.startGameDelay(currentRoom, roomConfig.get());
                }
            }
        } else {
            System.out.printf("Error : User not found?");
        }
    }

    @MessageMapping("/broadcastPracticeStart")
    public void broadcastPracticeStart(Principal principal) throws UnsupportedEncodingException {
        String username = principal.getName();
        Optional<ApplicationUser> optionalUser = userService.getUserByUsername(username);
        if (optionalUser.isPresent()) {
            ApplicationUser user = optionalUser.get();
            Optional<RoomUser> optionalRoomUser = roomService.getRoomUserByUser(user);
            if (optionalRoomUser.isPresent()) {
                RoomUser roomUser = optionalRoomUser.get();
                Room currentRoom = roomUser.getRoom();
                String roomID = currentRoom.getRoomId();
                String destination = "/topic/roomJoined/" + roomID;
                template.convertAndSend(destination, new BroadcastStartGameRequest("[Game Starting] started by the Host", roomID));
                List<RoomUser> roomUserList = roomService.getRoomUsersByRoomID(roomID);
                for (RoomUser ru : roomUserList) {
                    ApplicationUser u = ru.getUser();
                    roomService.updateIsConnected(u, false); // sets all the users to not connected, so they can be read as connected when joining through the game URL.
                }
                roomService.updateGameState(currentRoom.getRoomId(), "game");
                template.convertAndSend(destination, new PlainResponse("[LOADING_RESOURCES]"));
                GameService.roomDataMap.put(roomID, new GameData());
                gameService.loadSongMap(roomID);
                Optional<RoomConfig> roomConfig = roomConfigRepository.findByRoom_roomId(roomID);
                if (roomConfig.isPresent()) {
                    GameService.roomDataMap.get(roomID).setMaxGuessTime(roomConfig.get().getMaxGuessTime());
//                    gameService.startGameDelay(currentRoom, roomConfig.get()); // handling another way.
                }
            }
        } else {
            System.out.printf("Error : User not found?");
        }
    }



    @MessageMapping("/joinedGame")
    public void joinedGame(@Payload JoinRoomRequest request, Principal principal) {

        String username = principal.getName();
        Optional<ApplicationUser> optionalUser = userService.getUserByUsername(username);
        if (optionalUser.isPresent()) {
            ApplicationUser user = optionalUser.get();
            roomService.updateIsConnected(user, true);
            String destination = "/topic/game/" + request.getRoomID();
            template.convertAndSend(destination, new PlainResponse(username + "has joined the game"));


        }
    }


}
