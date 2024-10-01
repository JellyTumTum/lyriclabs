package com.project.app.api.rooms;

public class JoinRoomRequest {
    private String roomID;
    private String gameType;


    public JoinRoomRequest(String roomID) {
        this.roomID = roomID;
    }

    public JoinRoomRequest() {
        super();
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
}
