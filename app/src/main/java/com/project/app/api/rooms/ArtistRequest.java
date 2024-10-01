package com.project.app.api.rooms;

import com.project.app.model.music.Artist;

public class ArtistRequest {

    String roomID;

    FrontendArtist artist;
    boolean isAdd; // denotes if its adding or removing, technically not required but seen as its ambigious otherwise due to using the same class for add and remove, makes sense to note it down.

    public ArtistRequest(FrontendArtist artist, String roomID, boolean isAdd) {
        this.artist = artist;
        this.isAdd = isAdd;
        this.roomID = roomID;
    }

    public FrontendArtist getArtist() {
        return artist;
    }

    public void setArtist(FrontendArtist artist) {
        this.artist = artist;
    }

    public boolean getIsAdd() {
        return isAdd;
    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
