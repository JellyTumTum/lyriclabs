package com.project.app.api;

public class RegistrationResponse {
    private String responseMessage;
    private boolean success;

    public RegistrationResponse(String responseMessage, boolean success) {
        this.responseMessage = responseMessage;
        this.success = success;
    }

    // Getters and setters
    public String getResponseMessage() { return this.responseMessage; }

    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }

    public boolean getSuccess() { return this.success; }

    public void setSuccess(boolean success) {this.success = success; }


}