package com.project.app.model.rooms;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; // sick of writing out my own jsons

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @Column(unique = true)
    private String roomId;

    private String roomName;

    private String gameState;
    private Integer hostId;

    private boolean isPractice;

    public Room() {
    }

    public Room(String roomId, String roomName, String gamestate, Integer hostId) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.gameState = gamestate;
        this.hostId = hostId;
        this.isPractice = false;
    }

    public Room(String roomId, String roomName, String gamestate, Integer hostId, boolean isPractice) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.gameState = gamestate;
        this.hostId = hostId;
        this.isPractice = isPractice;
    }

    // getters and setters

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gamestate) {
        this.gameState = gamestate;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public boolean isPractice() {
        return isPractice;
    }

    public void setPractice(boolean practice) {
        isPractice = practice;
    }



    @Override
    public String toString() {
        try {
            // Create instance to convert to string. (Using Jackson Library)
            ObjectMapper mapper = new ObjectMapper();

            // Convert the current object to a JSON string
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return super.toString();
        }
    }
}
