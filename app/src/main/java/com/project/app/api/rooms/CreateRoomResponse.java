package com.project.app.api.rooms;

public class CreateRoomResponse {

    private String roomId;
    private String responseMessage;

    private String notification;

    public CreateRoomResponse(String roomId, String message) {
        this.roomId = roomId;
        this.responseMessage = message;
    }

    public CreateRoomResponse(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public CreateRoomResponse(String roomId, String message, String notification) {
        this.roomId = roomId;
        this.responseMessage = message;
        this.notification = notification;
    }


    // Getters and setters

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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
}
