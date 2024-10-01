package com.project.app.model.rooms;

import com.project.app.model.ApplicationUser;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "room_users")
public class RoomUser {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @ManyToOne // CANNOT ADD CASCADE HERE TO FIX ERROR, as it breaks people joining lobbies that arent the host.
    @JoinColumn(name = "room_id")
    private Room room;

    private Boolean isHost;

    private Boolean isConnected;

    private Integer score;

    public RoomUser() {}

    public RoomUser(ApplicationUser user, Room room, Boolean isHost, boolean isConnected) {
        this.user = user;
        this.room = room;
        this.isHost = isHost;
        this.isConnected = isConnected;
        this.score = 0;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Boolean getIsHost() {
        return isHost;
    }

    public void setIsHost(Boolean isHost) {
        this.isHost = isHost;
    }

    public String getUsername() {
        if (this.isHost) {
            return "[H] " + this.user.getUsername();
        } else {return this.user.getUsername(); }

    }

    public Boolean getHost() {
        return isHost;
    }

    public void setHost(Boolean host) {
        isHost = host;
    }

    public Boolean getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(Boolean isConnected) {
        this.isConnected = isConnected;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "{ user(name) : " + this.user.getUsername() +
                ", room(name): " + this.room.getRoomName() +
                ", isHost: " + this.getIsHost() +
                ", isConnected: " + this.getIsConnected() +
                ", score : " + this.getScore() +
                " }\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        return Objects.equals(this.getUser().getUsername(), ((RoomUser) o).getUser().getUsername()); // simplest way to compare and should only be the case if they have the same details for everything else.
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, room, isHost, isConnected, score);
    }

}
