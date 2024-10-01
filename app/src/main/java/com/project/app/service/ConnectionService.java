package com.project.app.service;

import com.project.app.api.rooms.*;
import com.project.app.model.ApplicationUser;
import com.project.app.model.rooms.Room;
import com.project.app.model.rooms.RoomUser;
import com.project.app.repository.rooms.RoomArtistRepository;
import com.project.app.repository.rooms.RoomConfigRepository;
import com.project.app.repository.rooms.RoomRepository;
import com.project.app.repository.rooms.RoomUserRepository;
import com.project.app.service.music.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.annotation.web.oauth2.client.OAuth2ClientSecurityMarker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ConnectionService {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RoomUserRepository roomUserRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomConfigRepository roomConfigRepository;

    @Autowired
    private RoomArtistRepository roomArtistRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private MusicService musicService;

//    @Autowired
//    private SimpUserRegistry simpUserRegistry; --> can be used to get access to users websockets using | SimpUser user = simpUserRegistry.getUser(username); | not recommended though so trying to avoid it

    // A concurrent map to track the last heartbeat of each user
    private final Map<String, Long> userHeartbeats = new ConcurrentHashMap<>();

    public void printUserHeartbeats() {
        System.out.println("------- User Heartbeats -------");
        for (Map.Entry<String, Long> entry : userHeartbeats.entrySet()) {
            System.out.println("User: " + entry.getKey() + ", Timestamp: " + entry.getValue());
        }
        System.out.println("------------------------------");
    }

    private static final int HEARTBEAT_MS = 5000;

    @Scheduled(fixedRate = HEARTBEAT_MS)  // Send every 5 seconds
    public void sendHeartbeats() {
//        System.out.printf("\nsending out serverside heartbeats");
        template.convertAndSend("/topic/heartbeatResponse", "heartbeat");
    }

    // Call this when you receive a heartbeat from a user
    public void receiveHeartbeat(String username) {
        userHeartbeats.put(username, System.currentTimeMillis());
    }

    @Scheduled(fixedRate = HEARTBEAT_MS)  // Check every 5 seconds
    public void checkHeartbeats() {
//        System.out.printf("\nchecking heartbeats\n");
//        printUserHeartbeats();
        long currentTime = System.currentTimeMillis();
        userHeartbeats.forEach((key, value) -> {
            if (currentTime - value >= HEARTBEAT_MS + 1000 && currentTime - value < (2L * HEARTBEAT_MS) + 1000) { // added a second to account for potential lined up coincidence of calls. would still catch the first missed heartbeat.
                handleLostConnection(key);

            } else if (currentTime - value > 3L * HEARTBEAT_MS) {  // No heartbeat in the last 15 seconds
                handleDisconnection(key, false);
                userHeartbeats.remove(key);
            }
        });
    }

    private void handleLostConnection(String username) {
        try {
            // updateIsConnected. needs to either just remove the user if in lobby state or set to false if in gameState
            ApplicationUser user = userService.getUserByUsername(username).get();
            System.out.printf("Handling Lost Connection for " + user.getUsername());
            roomService.updateIsConnected(user, false);
            Optional<RoomUser> potDisconnectedRoomUser = roomService.getRoomUserByUser(user);

            // FINISH FUNCTION, NEED TO FIRST .get() then get roomID then notify all users that he has "lost connection" this is different to disconnected as disconnected implies he meant to do it. (different logic)
            if (potDisconnectedRoomUser.isPresent()) {
                System.out.printf("SHOULD BE SEEING THIS (5/12/23) ----------------------------------------\n");
                RoomUser disconnectedRoomUser = potDisconnectedRoomUser.get();
                String roomID = disconnectedRoomUser.getRoom().getRoomId();
                List<FrontendUserInfo> userList = getUserListInfo(disconnectedRoomUser.getRoom()); // in this case the 'lost connection' user will still be in this list which is correct.
                String gameState = disconnectedRoomUser.getRoom().getGameState();
                if (Objects.equals(gameState, "lobby")) {
                    handleDisconnection(username, false);
                } else {
                    template.convertAndSend("/topic/roomJoined/" + roomID, new DisconnectionPublic("User " + disconnectedRoomUser.getUser().getUsername() + " has lost connection.", disconnectedRoomUser.getUser().getUsername(), true, userList));
                    // FIGURE THIS OUT CURRENTLY GIVING JSON PARSE ERROR BUT THAT WAS BEFORE ADDING THE DISCONNECTIONPUBLIC CALL, Maybe rename missedHeartbeats to purposefulDisconnection, or actually calculating a timeSinceLastHeartbeat might be appropriate.
                }
            } else {
                System.out.printf("\n User was already removed from the room. (no roomUser entry for their user)\n");
                if (userHeartbeats.containsKey(username)) {
                    removeFromUserHeartbeats(username);
                }
            } // technically if user isn't present, no need to disconnect them. realistically this thought process will probably get proven wrong with some error down the line. (12/09/23)

        } catch (Exception e) {
            // Catch all other exceptions...
            System.out.printf("Error processing heartbeat check for user: " + username, e);
        }
    }

    public void handleDisconnection(String username, boolean userDisconnect) { // userDisconnect implies the user meant to disconnect if 'true'

        Optional<ApplicationUser> potDisconnectedUser = userService.getUserByUsername(username);
        Optional<RoomUser> potRoomUser;
        RoomUser roomUser;
        ApplicationUser disconnectedUser;
        Room effectedRoom;
        if (potDisconnectedUser.isPresent()) {
            disconnectedUser = potDisconnectedUser.get();
            potRoomUser = roomService.getRoomUserByUser(disconnectedUser);
            if (potRoomUser.isPresent()) {
                roomUser = potRoomUser.get();
                effectedRoom = roomUser.getRoom();

            } else {
                roomUser = null;
                System.out.printf("HandleDisconnection : potRoomUser got no value");
                System.out.printf(username);
                printUserHeartbeats();
                return;
            }
        } else {
            roomUser = null;
            System.out.printf("\nHandleDisconnection : potDisconnectedUser got no value");
            System.out.printf(username);
            printUserHeartbeats();
            return;
        }

        List<RoomUser> users = roomService.getActiveUsers(effectedRoom.getRoomId());
        if (userDisconnect) {
            if (effectedRoom.getGameState().equals("lobby")) {
                removeUserFromRoom(disconnectedUser, effectedRoom, false);
                users.remove(roomUser);
            } else {
                // only other gamestate is currently playing (if a third is added for postgame then it would still behave the same as only time the user is removed from roomUser is through lobby as to not interfere with gameplay once started)
                roomService.updateIsConnected(disconnectedUser, false);
                users.remove(roomUser);
                List<FrontendUserInfo> userList = getUserListInfo(users);
                userList.removeIf(user -> user.id.equals(roomUser.getUsername())); // due to concurrency and such the line above probably won't of been fully completed yet.
                template.convertAndSend("/topic/roomJoined/" + effectedRoom.getRoomId(), new DisconnectionPublic(disconnectedUser.getUsername() + "has left the lobby", username, false, userList));
            }
        } else {
            // this means the user did not mean to leave, and he has missed 3 heartbeats (15s as of this comment 14/09/23 -> controlled by HEARTBEAT_MS)
            if (effectedRoom.getGameState().equals("lobby")) {
                removeUserFromRoom(disconnectedUser, effectedRoom, true);
                users.remove(roomUser);
            } else {
                // only other gamestate is currently playing (if a third is added for postgame then it would still behave the same as only time the user is removed from roomUser is through lobby as to not interfere with gameplay once started)
                roomService.updateIsConnected(disconnectedUser, false);
                List<FrontendUserInfo> userList = getUserListInfo(users);
                userList.removeIf(user -> user.id.equals(roomUser.getUsername())); // due to concurrency and such the line above probably won't of been fully completed yet.
                template.convertAndSend("/topic/roomJoined/" + effectedRoom.getRoomId(), new DisconnectionPublic(disconnectedUser.getUsername() + "has left the lobby", username, true, userList));
            }
        }
        // close room if room is empty.
        System.out.printf(users.toString());
        if (users.isEmpty()) {
            closeRoom(effectedRoom);
        } else if (Objects.equals(disconnectedUser.getUserId(), effectedRoom.getHostId())) {
            // host migration
            migrateHosts(effectedRoom, users.get(0).getUser(), disconnectedUser);

        }

    }

    public void migrateHosts(Room room, ApplicationUser newHost, ApplicationUser oldHost) {
        System.out.printf("\n MIGRATING HOSTS FROM " + oldHost.getUsername() + "--> " + newHost.getUsername());
        // need to change room over to new name and host
        room.setHostId(newHost.getUserId());
        if (room.getRoomName().contains(oldHost.getUsername())) {
            room.setRoomName(newHost.getUsername() + "'s Lobby");
        }
        System.out.printf(room.toString());
        roomRepository.updateRoomInfo(newHost.getUserId(), room.getRoomName(), room.getRoomId());
        roomUserRepository.updateIsHostByUser(newHost, true);
        List<FrontendUserInfo> userList = getUserListInfo(room);
        template.convertAndSend("/topic/roomJoined/" + room.getRoomId(), new HostMigration("New host = " + newHost.getUsername(), newHost.getUsername(), room.getRoomName(), userList));

    }

    public void handleReconnection(String username) {
        Optional<ApplicationUser> potUser = userService.getUserByUsername(username);
        if (potUser.isPresent()) {
            ApplicationUser user = potUser.get();
            roomService.updateIsConnected(user, true);

        }

    }

    public void removeFromUserHeartbeats(ApplicationUser user) {
        removeFromUserHeartbeats(user.getUsername());
    }

    public void removeFromUserHeartbeats(String username) {
        userHeartbeats.remove(username);
    }

    public void removeUserFromRoom(ApplicationUser disconnectedUser, Room effectedRoom, boolean missedHeartbeats) {
        // TODO: remove them from map as well otherwise the disconnection from lost connection runs too.
        Optional<RoomUser> potRoomUser = roomService.getRoomUserByUser(disconnectedUser);
        if (potRoomUser.isPresent()) {
            roomUserRepository.delete(potRoomUser.get());
            List<FrontendUserInfo> userList = getUserListInfo(effectedRoom);
            userList.removeIf(user -> user.id.equals(potRoomUser.get().getUsername())); // due to concurrency and such the line above probably won't of been fully completed yet.
            System.out.printf("\nuserList = " + userList + "\n");
            template.convertAndSend("/topic/roomJoined/" + effectedRoom.getRoomId(), new DisconnectionPublic(disconnectedUser.getUsername() + " has left the lobby asasdasd", disconnectedUser.getUsername(), missedHeartbeats, userList));
            removeFromUserHeartbeats(disconnectedUser);
        }
    }

    public void closeRoom(Room room) {
        String destination = "/topic/roomJoined/" + room.getRoomId();

        System.out.printf("\nClearing room " + room.getRoomName() + " (" + room.getRoomId() + ") \n");
        List<RoomUser> userList = roomService.getRoomUsersByRoomID(room.getRoomId());
        for (RoomUser user : userList) {
            removeFromUserHeartbeats(user.getUser());
        }
        GameData gd = GameService.roomDataMap.get(room.getRoomId());
        System.out.println("\n gd = " + gd + "\n");
        if (gd != null) {
            gameService.endGame(room.getRoomId());
        } else {
            roomUserRepository.deleteByRoomId(room.getRoomId());
            System.out.printf("Cleared roomUsers (id = " + room.getRoomId() + ")\n");
            musicService.clearRoomArtistsByRoom(room.getRoomId());
            System.out.printf("Cleared roomArtist (id = " + room.getRoomId() + ")\n");
            roomConfigRepository.deleteByRoomId(room.getRoomId());
            System.out.printf("Cleared roomConfig (id = " + room.getRoomId() + ")\n");

            roomRepository.delete(room);
            System.out.printf("Cleared room (id = " + room.getRoomId() + ")\n");

        }

        template.convertAndSend(destination, new PlainResponse("Room has been force-closed")); // catch in-case somehow someone is still in room, currently this is only ran if room is empty so something has changed if you have ended up here (21/09/23)

    }

    public List<FrontendUserInfo> getUserListInfo(Room room) {
        return getUserListInfo(roomService.getRoomUsersByRoomID(room.getRoomId()));
    }

    public List<FrontendUserInfo> getUserListInfo(List<RoomUser> roomUsers) {
        return roomUsers.stream()
                .map(roomUser -> {
                    return new FrontendUserInfo(roomUser.getUser().getUsername(), roomUser.getIsHost(), roomUser.getScore(), roomUser.getIsConnected());
                })
                .collect(Collectors.toList());
    }

}
