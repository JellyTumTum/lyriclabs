package com.project.app.api.rooms;

public class SelectingSongResponse {

    String responseMessage;
    Integer songNumber;
    Integer songCount;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Integer getSongNumber() {
        return songNumber;
    }

    public void setSongNumber(Integer songNumber) {
        this.songNumber = songNumber;
    }

    public Integer getSongCount() {
        return songCount;
    }

    public void setSongCount(Integer songCount) {
        this.songCount = songCount;
    }


    public SelectingSongResponse() {}

    public SelectingSongResponse(String responseMessage, Integer songNumber, Integer songCount) {
        this.responseMessage = responseMessage;
        this.songNumber = songNumber;
        this.songCount = songCount;
    }
}
