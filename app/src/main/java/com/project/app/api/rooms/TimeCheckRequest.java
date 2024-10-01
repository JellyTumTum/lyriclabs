package com.project.app.api.rooms;

public class TimeCheckRequest {

    long recieveTimeMS;
    long clientGuessTimeMS;

    public TimeCheckRequest(long recieveTimeMS, long clientGuessTimeMS) {
        this.recieveTimeMS = recieveTimeMS;
        this.clientGuessTimeMS = clientGuessTimeMS;

    }
}
