package com.project.app.api.GameStats;

import com.project.app.api.rooms.FrontendArtist;
import com.project.app.model.ApplicationUser;
import org.springframework.security.core.parameters.P;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameStatsInitialResponse {

    public static class IntStringPair {

        Integer key;
        String value;

        public IntStringPair(Integer id, String username) {
            this.key = id;
            this.value = username;
        }

        public Integer getKey() {
            return key;
        }

        public void setKey(Integer key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    String responseMessage;
    String notification;
    List<IntStringPair> userList;
    List<FrontendArtist> artistList;
    String lobbyName;

    IntStringPair currentUser;
    Boolean inGame;

    boolean isPractice;

    public GameStatsInitialResponse(String responseMessage, List<ApplicationUser> userList, String lobbyName, List<FrontendArtist> frontendArtists, ApplicationUser currentUser, boolean isPractice) {
        this.responseMessage = responseMessage;
        this.userList = userList.stream()
                .map(user -> new IntStringPair(user.getUserId(), user.getUsername()))
                .collect(Collectors.toList());
        this.lobbyName = lobbyName;
        this.artistList = frontendArtists;
        if (currentUser != null) {
            this.currentUser = new IntStringPair(currentUser.getUserId(), currentUser.getUsername());
            this.inGame = true;
        } else {
            this.currentUser = null;
            this.inGame = false;
        }
        this.isPractice = isPractice;

    }

    public GameStatsInitialResponse(String responseMessage,  String notification) {
        this.responseMessage = responseMessage;
        this.notification = notification;
    }

    public GameStatsInitialResponse(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public List<IntStringPair> getUserList() {
        return userList;
    }

    public void setUserList(List<IntStringPair> userList) {
        this.userList = userList;
    }

    public List<FrontendArtist> getArtistList() {
        return artistList;
    }

    public void setArtistList(List<FrontendArtist> artistList) {
        this.artistList = artistList;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public IntStringPair getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(IntStringPair currentUser) {
        this.currentUser = currentUser;
    }

    public Boolean getInGame() {
        return inGame;
    }

    public void setInGame(Boolean inGame) {
        this.inGame = inGame;
    }

    public boolean isPractice() {
        return isPractice;
    }

    public void setPractice(boolean practice) {
        isPractice = practice;
    }
}
