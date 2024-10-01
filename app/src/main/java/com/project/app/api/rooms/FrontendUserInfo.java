package com.project.app.api.rooms;

public class FrontendUserInfo {

    public String id;
    public boolean isHost;
    public boolean isConnected;
    public Integer score;

    public FrontendUserInfo(String username, boolean isHost, Integer score, Boolean isConnected) {
        this.id = username;
        this.isHost = isHost;
        this.score = score;
        this.isConnected = isConnected;
    }

    @Override
    public String toString() {
        return "{id: " + this.id + ", isHost: " + this.isHost + ", score: " + this.score + ", isConnected: " + this.isConnected + "}";
    }

    public boolean isConnected() {
        return this.isConnected;
    }
}
