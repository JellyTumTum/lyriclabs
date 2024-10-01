package com.project.app.api.rooms;

public class SettingResponse {

    public String responseMessage;
    public String settingName;
    public Object settingValue;

    public SettingResponse(String responseMessage, String settingName, Object settingValue) {
        this.responseMessage = responseMessage;
        this.settingName = settingName;
        this.settingValue = settingValue;

    }

    public SettingResponse(String responseMessage, String settingName) {
        this.responseMessage = responseMessage;
        this.settingName = settingName;
        this.settingValue = "";
    }
}
