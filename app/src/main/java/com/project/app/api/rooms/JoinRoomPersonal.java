package com.project.app.api.rooms;

import com.project.app.model.rooms.RoomConfig;

import java.util.List;

public class JoinRoomPersonal {

    public String roomId;
    public String roomName;
    public String gameState;
    public FrontendConfig config;
    public String responseMessage;
    public String notification;


    public boolean isHost;

    public List<FrontendUserInfo> userList;
    public List<FrontendArtist> artistList;
    String username;

    public List<FrontendUserInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<FrontendUserInfo> userList) {
        this.userList = userList;
    }

    public JoinRoomPersonal(String roomId, String roomName, String gameState, FrontendConfig config, String responseMessage, boolean isHost, List<FrontendUserInfo> userList, String username, List<FrontendArtist> artistList) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.gameState = gameState;
        this.config = config;
        this.responseMessage = responseMessage;
        this.isHost = isHost;
        this.userList = userList;
        this.username = username;
        this.artistList = artistList;
    }

    public JoinRoomPersonal(String responseMessage) {
        this.roomId = "";
        this.roomName = "";
        this.gameState = "";
        this.config = null;
        this.responseMessage = responseMessage;
        this.isHost = false;
        this.userList = null;
        this.username = "";
        this.artistList = null;

    }

    public JoinRoomPersonal(String responseMessage, String notification) {
        this.roomId = "";
        this.roomName = "";
        this.gameState = "";
        this.config = null;
        this.responseMessage = responseMessage;
        this.isHost = false;
        this.userList = null;
        this.username = "";
        this.artistList = null;
        this.notification = notification;

    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public FrontendConfig getConfig() {
        return config;
    }

    public void setConfig(FrontendConfig config) {
        this.config = config;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<FrontendArtist> getArtistList() {
        return artistList;
    }

    public void setArtistList(List<FrontendArtist> artistList) {
        this.artistList = artistList;
    }
}
