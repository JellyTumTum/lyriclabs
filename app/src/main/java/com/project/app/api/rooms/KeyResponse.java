package com.project.app.api.rooms;

public class KeyResponse {

    public String roomKey;
    public String responseMessage;

    public String username;

    public KeyResponse(String roomKey, String responseMessage, String username) {
        this.roomKey = roomKey;
        this.responseMessage = responseMessage;
        this.username = username;
    }

    public KeyResponse (String responseMessage) {
        this.roomKey = "";
        this.responseMessage = responseMessage;
        this.username = "";
    }
}
