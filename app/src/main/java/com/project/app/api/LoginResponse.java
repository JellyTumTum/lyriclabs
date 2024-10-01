package com.project.app.api;

import com.project.app.model.ApplicationUser;

public class LoginResponse {


    private Integer userId;
    private String username;
    private String email;
    private String jwt;
    private String responseMessage;
    private String sessionKey;

    public LoginResponse(String responseMessage) {
        this.userId = null;
        this.username = null;
        this.email = null;
        this.jwt = null;
        this.responseMessage = responseMessage;
    }

    public LoginResponse(ApplicationUser user, String jwt, String sessionKey) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.jwt = jwt;
        this.sessionKey = sessionKey;
        this.responseMessage = "login successful";
    }

    // Getters and setters

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}

