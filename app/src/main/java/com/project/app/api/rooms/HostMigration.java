package com.project.app.api.rooms;

import java.util.List;

public class HostMigration {

    public String responseMessage;
    public String username;
    public String roomName;
    public List<FrontendUserInfo> userList;

    public HostMigration() {}

    public HostMigration(String responseMessage, String username, String lobbyName, List<FrontendUserInfo> userList) {
        this.responseMessage = responseMessage;
        this.username = username;
        this.roomName = lobbyName;
        this.userList = userList;

    }

}
