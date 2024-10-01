package com.project.app.api.rooms;

import java.util.List;

public class DisconnectionPublic {

    String responseMessage;
    String disconnectedUsername;

    Boolean missedHeartbeats;

    List<FrontendUserInfo> currentPlayers;

    public DisconnectionPublic() {
    }

    public DisconnectionPublic(String responseMessage, String disconnectedUsername, boolean missedHeartbeats, List<FrontendUserInfo> currentPlayers) {
        this.responseMessage = responseMessage;
        this.disconnectedUsername = disconnectedUsername;
        this.missedHeartbeats = missedHeartbeats;
        this.currentPlayers = currentPlayers;
    }


    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getDisconnectedUsername() {
        return disconnectedUsername;
    }

    public void setDisconnectedUsername(String disconnectedUsername) {
        this.disconnectedUsername = disconnectedUsername;
    }

    public Boolean getMissedHeartbeats() {
        return missedHeartbeats;
    }

    public void setMissedHeartbeats(Boolean missedHeartbeats) {
        this.missedHeartbeats = missedHeartbeats;
    }

    public void setCurrentPlayers(List<FrontendUserInfo> currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public List<FrontendUserInfo> getCurrentPlayers() {
        return this.currentPlayers;
    }
}


