package com.project.app.api.rooms;

public class BroadcastStartGameRequest {

    public String responseMessage;
    public String roomID;

    public BroadcastStartGameRequest() {
        this.responseMessage = "";
    }

    public BroadcastStartGameRequest(String responseMessage, String roomID) {

        this.responseMessage = responseMessage;
        this.roomID = roomID;
    }
}
