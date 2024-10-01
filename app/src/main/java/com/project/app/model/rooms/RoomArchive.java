package com.project.app.model.rooms;

import com.project.app.api.rooms.GameData;
import com.project.app.model.ApplicationUser;
import com.project.app.model.music.Artist;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.List;
import java.util.stream.Collectors;


@Entity
public class RoomArchive {

    @Id
    @Column(unique = true)
    private String roomId;

    private String roomName;

    // config stuff required.

    private Integer playerCount; // will be taken from the end of the game so not 100% accurate, but probably is.

    private Integer artistCount;

    private Integer maxGuessTime;

    private Integer roundCount;

    private boolean isPractice;


    // combined lists.
    @Column(name="user_id_list")
    private String userIDList;

    @Column(name="artist_id_list")
    private String artistIDList;

    // TODO: Add as needed while testing.

    public RoomArchive() {}

    public RoomArchive(Room room, RoomConfig roomConfig, List<ApplicationUser> userList, GameData gameData) {
        this.roomId = room.getRoomId();
        this.roomName = room.getRoomName();
        this.playerCount = userList.size();
        this.artistCount = gameData.getArtistsList().size();
        this.maxGuessTime = roomConfig.getMaxGuessTime();
        this.roundCount = gameData.getRoundCount();
        this.isPractice = room.isPractice();
        this.userIDList = userList.stream()
                .map(ApplicationUser::getUserId) // This produces a Stream<Integer>
                .map(Object::toString)           // Convert each Integer to String
                .collect(Collectors.joining(",")); // Join all Strings with a comma

        this.artistIDList =gameData.getArtistsList().stream().map(Artist::getArtistId).collect(Collectors.joining(","));
    }


    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public Integer getArtistCount() {
        return artistCount;
    }

    public void setArtistCount(Integer artistCount) {
        this.artistCount = artistCount;
    }

    public Integer getMaxGuessTime() {
        return maxGuessTime;
    }

    public void setMaxGuessTime(Integer maxGuessTime) {
        this.maxGuessTime = maxGuessTime;
    }

    public Integer getRoundCount() {
        return roundCount;
    }

    public void setRoundCount(Integer roundCount) {
        this.roundCount = roundCount;
    }

    public boolean isPractice() {
        return isPractice;
    }

    public void setPractice(boolean practice) {
        isPractice = practice;
    }

    public String getUserIDList() {
        return userIDList;
    }

    public void setUserIDList(String userIDList) {
        this.userIDList = userIDList;
    }

    public String getArtistIDList() {
        return artistIDList;
    }

    public void setArtistIDList(String artistIDList) {
        this.artistIDList = artistIDList;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
