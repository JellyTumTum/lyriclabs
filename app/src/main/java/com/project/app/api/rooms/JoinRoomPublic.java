package com.project.app.api.rooms;

import java.util.List;

public class JoinRoomPublic {

    public String username;
    public String responseMessage;
    public String notification;
    public List<FrontendUserInfo> userList;
    public Integer playerCount;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public List<FrontendUserInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<FrontendUserInfo> userList) {
        this.userList = userList;
    }

    public JoinRoomPublic(String username, String responseMessage, List<FrontendUserInfo> userList) {
        this.username = username;
        this.responseMessage = responseMessage;
        this.userList = userList;
        this.playerCount = userList.size();
        System.out.printf("playerCount: " + this.playerCount);
    }

    public JoinRoomPublic (String responseMessage, String notification) {
        this.responseMessage = responseMessage;
        this.notification = notification;

    }

}
