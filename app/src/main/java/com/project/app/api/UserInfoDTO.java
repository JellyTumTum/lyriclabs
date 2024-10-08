package com.project.app.api;

public class UserInfoDTO {
    private String username;
    private String password;
    private String email;

    public UserInfoDTO() {
        super();
    }

    public UserInfoDTO(String username, String password, String email) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters and setters + other functions
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return "\nRegistration info: \nusername: " + this.username + " \npassword: " + this.password + " \nemail: " + this.email;
    }
}
