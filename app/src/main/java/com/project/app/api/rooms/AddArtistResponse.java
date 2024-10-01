package com.project.app.api.rooms;

import com.project.app.model.music.Artist;

import java.util.List;

public class AddArtistResponse {

    String responseMessage;
    List<FrontendArtist> artistList;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public List<FrontendArtist> getArtistList() {
        return artistList;
    }

    public void setArtistList(List<FrontendArtist> artistList) {
        this.artistList = artistList;
    }

    public AddArtistResponse(String responseMessage, List<FrontendArtist> artistList) {
        this.responseMessage = responseMessage;
        this.artistList = artistList;
    }

    public AddArtistResponse() {}

}
