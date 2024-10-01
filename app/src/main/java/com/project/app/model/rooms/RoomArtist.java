package com.project.app.model.rooms;

import com.project.app.model.music.Artist;
import jakarta.persistence.*;

@Entity
@Table(name = "room_artist")
public class RoomArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomConfig room;


    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomConfig getRoom() {
        return room;
    }

    public void setRoom(RoomConfig room) {
        this.room = room;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public RoomArtist(RoomConfig roomConfig, Artist artist) {
        this.room = roomConfig;
        this.artist = artist;

    }


    public RoomArtist() {

    }

    @Override
    public String toString() {
        return "RoomConfigID: " + this.room.getRoom().getRoomId() + ", Artist: " + this.artist + "\n";
    }


}
