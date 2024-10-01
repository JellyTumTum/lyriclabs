package com.project.app.api.rooms;

import com.project.app.api.rooms.FrontendUserInfo;
import com.project.app.model.music.Artist;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class PracticeHistory {

    private static int nextId = 1;
    String responseMessage;
    Integer totalScore;
    Integer recentScore;
    String username;

    List<String> artistNames;

    List<String> artistArt;
    long totalGuessTime;
    Integer id;
    List<FrontendUserInfo> userList;

    public PracticeHistory(String responseMessage, String username, Integer recentScore, Integer totalScore, long totalGuessTime, List<FrontendUserInfo> userList, Set<Artist> artistSet) {
        System.out.println("\n PracticeHistory.java: artistSet being passed in = " + artistSet + "\n");
        this.id = nextId++;
        this.responseMessage = responseMessage;
        this.username = username;
        this.recentScore = recentScore;
        this.totalScore = totalScore;
        this.totalGuessTime = totalGuessTime;
        this.userList = userList;
        this.artistNames = artistSet.stream()
                .map(Artist::getName)
                .collect(Collectors.toList());
        this.artistArt = artistSet.stream().map(Artist::getArtURL).collect(Collectors.toList());
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
        PracticeHistory.nextId = nextId;
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

    public List<String> getArtistNames() {
        return artistNames;
    }

    public void setArtistNames(List<String> artistNames) {
        this.artistNames = artistNames;
    }

    public List<String> getArtistArt() {
        return artistArt;
    }

    public void setArtistArt(List<String> artistArt) {
        this.artistArt = artistArt;
    }



    @Override
    public String toString() {
        return "PracticeHistory{" +
                "id=" + id +
                ", responseMessage='" + responseMessage + '\'' +
                ", totalScore=" + totalScore +
                ", recentScore=" + recentScore +
                ", username='" + username + '\'' +
                ", artistNames=" + artistNames +
                ", artistArt=" + artistArt +
                ", totalGuessTime=" + totalGuessTime +
                ", userList=" + userList +
                '}';
    }



}
