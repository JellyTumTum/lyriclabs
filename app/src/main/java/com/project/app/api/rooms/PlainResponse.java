package com.project.app.api.rooms;

public class PlainResponse {

    public String responseMessage;
    public String notification;

    public PlainResponse() {
        this.responseMessage = "";
    }

    public PlainResponse(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public PlainResponse(String responseMessage, String notification) {
        this.responseMessage = responseMessage;
        this.notification = notification;
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
