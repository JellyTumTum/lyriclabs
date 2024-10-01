package com.project.app.service;

import com.project.app.api.rooms.FrontendArtist;
import com.project.app.api.rooms.GameData;
import com.project.app.api.rooms.SettingResponse;
import com.project.app.model.ApplicationUser;
import com.project.app.model.music.Artist;
import com.project.app.model.rooms.Room;
import com.project.app.model.rooms.RoomConfig;
import com.project.app.model.rooms.RoomUser;
import com.project.app.repository.UserRepository;
import com.project.app.repository.rooms.RoomArtistRepository;
import com.project.app.repository.rooms.RoomConfigRepository;
import com.project.app.repository.rooms.RoomRepository;
import com.project.app.repository.rooms.RoomUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomConfigRepository roomConfigRepository;

    @Autowired
    private RoomUserRepository roomUserRepository;

    @Autowired
    private RoomArtistRepository roomArtistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate template;

    public Optional<Room> findByRoomId(String roomID) {
        return roomRepository.findByRoomId(roomID);
    }

    public Optional<Room> findByHostID(Integer hostID)  {
        return roomRepository.findByHostId(hostID);
    }

    public Room createRoom(Room room) {

        return roomRepository.save(room);
//         switched to saveAndFlush to prevent error about persisting changes. (04/03/24)
    }

    public void removeRoom(Room room) {
        roomRepository.delete(room);
    }

    public RoomConfig saveConfig(RoomConfig roomConfig) {
        return roomConfigRepository.save(roomConfig);
    }
    // String roomId, Integer maxPlayers, Integer artistCount, Boolean usingSongName, String genre, Boolean usingGenre, String gamemode, Integer maxGuessTime, Integer waitTime, Integer maxSongs, Integer minSongs, Integer songCount

    public Long getPlayerCount(String roomID) {
        return roomUserRepository.countByRoomID(roomID);
    }

    public Integer getMaxPlayers(String roomID) {
        return roomConfigRepository.findMaxPlayersByRoom_roomId(roomID);
    }

    @Transactional
    public void updateIsConnected(ApplicationUser user, boolean value) {
        roomUserRepository.updateIsConnectedByUser(user, true);
    }

    public Optional<RoomConfig> getConfigSettings(String roomID) {
        return roomConfigRepository.findByRoom_roomId(roomID);
    }

    public List<RoomUser> getRoomUsersByRoomID(String roomID) {
        return roomUserRepository.findRoomUsersByRoomID(roomID);
    }

    public Optional<RoomUser> getRoomUserByUser(ApplicationUser user) {
        return roomUserRepository.findByUser(user);
    }


    public void handleRoomClosure() {
        /* theoretical function at this point.
        - would need to handle if a user makes a new room while they still have one active - as there is no way to close from outside the room should probably attempt to pass host over to another user.
        - if this cannot happen as no other users are in the room, can just close the room + the webSocket somehow, would need to look into that.
        - easier one is if a host just closes the room by clicking the close room button.
        */

    }

    public List<RoomUser> getActiveUsers(String roomID) {
        return getActiveUsers(getRoomUsersByRoomID(roomID));
    }

    public List<RoomUser> getActiveUsers(List<RoomUser> userList) {
        return userList.stream().filter(RoomUser::getIsConnected).collect(Collectors.toList());
    }

    public List<FrontendArtist> getArtistsForRoom(String roomID) {
        List<Artist> artistList = roomArtistRepository.findArtistsByRoomId(roomID);
        return artistList.stream().map(artist -> {
            return new FrontendArtist(artist.getArtistId(), artist.getName(), artist.getArtURL());
        }).toList();
    }

    private Object forceInt(Object input) {
        if (input instanceof String str) {
            try {
                return Integer.parseInt(str);  // Convert String to Integer if possible
            } catch (NumberFormatException e) {
                // Not a number, return as-is
                return str;
            }
        }
        return input;  // If it's not a String, return as-is
    }

    public String updateConfigSetting(String roomID, String settingName, Object settingValue) {

        settingValue = forceInt(settingValue); // will turn string numbers to Integer numbers for the case checks later on. (Slider sends over Integer directly but TextFields sent String numbers)
        try {
            switch (settingName) {
                case "usingSongName":
                case "usingStation":
                    if (settingValue instanceof Boolean) {
                        if (settingName.equals("usingSongName")) {
                            roomConfigRepository.updateUsingSongName(roomID, (Boolean) settingValue);
                        } else roomConfigRepository.updateUsingStation(roomID, (Boolean) settingValue);
                    } else {
                        throw new IllegalArgumentException("Invalid type for " + settingName);
                    }
                    break;

                case "gamemode":
                    if (settingValue instanceof String) {
                        roomConfigRepository.updateGamemode(roomID, (String) settingValue);
                    } else {
                        throw new IllegalArgumentException("Invalid type for gamemode");
                    }
                    break;

                case "maxPlayers":
                case "maxGuessTime":
                case "waitTime":
                case "maxSongs":
                case "minSongs":
                    if (settingValue instanceof Integer) {
                        if (settingName.equals("maxGuessTime")) {
                            roomConfigRepository.updateMaxGuessTime(roomID, (Integer) settingValue);
                        } else if (settingName.equals("waitTime")) {
                            roomConfigRepository.updateWaitTime(roomID, (Integer) settingValue);
                        } else if (settingName.equals("maxSongs")) {
                            roomConfigRepository.updateMaxSongs(roomID, (Integer) settingValue);
                        } else if (settingName.equals("minSongs")) {
                            roomConfigRepository.updateMinSongs(roomID, (Integer) settingValue);
                        } else {
                            roomConfigRepository.updateMaxPlayers(roomID, (Integer) settingValue);
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid type for " + settingName);
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Invalid setting name: " + settingName);
            }
        } catch (IllegalArgumentException e) {
            return "[Config] Error : " + e;
        }

        template.convertAndSend("/topic/roomJoined/" + roomID, new SettingResponse("Setting Change - " + settingName  + ": " + settingValue, settingName, settingValue));
        return "[Config] Success : setting " + settingName + "adjusted to " + settingValue; // potentially could be returning some wierd obj string not the value as a string (03/10/23);

    }


    public void updateGameState(String roomID, String newGameState) {
        roomRepository.updateGameState(newGameState, roomID);
    }

    public int getRoundSongCountByRoom(String roomID) {
        GameData gameData = GameService.roomDataMap.get(roomID);
        System.out.println("RoomService:getRoundSongCountByRoom - GameData(" + roomID + ") = " + gameData + "\n");
        if (gameData.getRoundCount() != -1 ) {
            return gameData.getRoundCount();
        } else {
            Optional<RoomConfig> optConfig = roomConfigRepository.findByRoom_roomId(roomID);
            RoomConfig config;
            if (optConfig.isPresent()) {
                config = optConfig.get();
                Random random = new Random();
                int minSongs = config.getMinSongs();
                int maxSongs = config.getMaxSongs();

                // Generate a random number between minSongs (inclusive) and maxSongs (inclusive)
                int roundCount = minSongs + random.nextInt(maxSongs - minSongs + 1);
                gameData.setRoundCount(roundCount);
                return roundCount;
            }
            return -1;
        }


    }
}
