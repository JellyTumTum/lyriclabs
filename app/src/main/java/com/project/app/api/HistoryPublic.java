package com.project.app.api;

import com.project.app.api.rooms.FrontendUserInfo;

import java.util.List;


public class HistoryPublic {

    private static int nextId = 1;
    String responseMessage;
    Integer totalScore;
    Integer recentScore;
    String username;
    long totalGuessTime;
    Integer id;
    List<FrontendUserInfo> userList;

    public HistoryPublic(String responseMessage, String username, Integer recentScore, Integer totalScore, long totalGuessTime, List<FrontendUserInfo> userList) {
        this.id = nextId++;
        this.responseMessage = responseMessage;
        this.username = username;
        this.recentScore = recentScore;
        this.totalScore = totalScore;
        this.totalGuessTime = totalGuessTime;
        this.userList = userList;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getRecentScore() {
        return recentScore;
    }

    public void setRecentScore(Integer recentScore) {
        this.recentScore = recentScore;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTotalGuessTime() {
        return totalGuessTime;
    }

    public void setTotalGuessTime(long totalGuessTime) {
        this.totalGuessTime = totalGuessTime;
    }

    public static int getNextId() {
        return nextId;
    }

    public static void setNextId(int nextId) {
        HistoryPublic.nextId = nextId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<FrontendUserInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<FrontendUserInfo> userList) {
        this.userList = userList;
    }
}
