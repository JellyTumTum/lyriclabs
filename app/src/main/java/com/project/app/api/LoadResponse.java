package com.project.app.api;

public class LoadResponse {

    public String token;
    public String username;
    public String responseMessage;
    public String newJWT;

    public LoadResponse() {
    }

    public LoadResponse(String token, String username, String responseMessage) {
        this.token = token;
        this.username = username;
        this.responseMessage = responseMessage;
        this.newJWT = "";
    }
    public LoadResponse(String token, String username, String responseMessage, String jwt) {
        this.token = token;
        this.username = username;
        this.responseMessage = responseMessage;
        this.newJWT = jwt;
    }


}
