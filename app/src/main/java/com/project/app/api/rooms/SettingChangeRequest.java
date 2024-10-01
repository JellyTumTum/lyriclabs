package com.project.app.api.rooms;

public class SettingChangeRequest {

    public String roomID;
    public String settingName;
    public Object settingValue;

    public SettingChangeRequest() {

    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public Object getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(Object settingValue) {
        this.settingValue = settingValue;
    }
}
