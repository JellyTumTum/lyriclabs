package com.project.app.api.rooms;

public class FrontendArtist {


    public String spotifyID;
    public String artistName;
    public String artURL;

    public FrontendArtist() {

    }
    public FrontendArtist(String spotifyID, String artistName, String artURL) {
        this.spotifyID = spotifyID;
        this.artistName = artistName;
        this.artURL = artURL;
    }

    public String getName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtURL() {
        return artURL;
    }

    public void setArtURL(String artURL) {
        this.artURL = artURL;
    }

    public String getSpotifyID() {
        return spotifyID;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    @Override
    public String toString() {
        return this.artistName;
    }
}
