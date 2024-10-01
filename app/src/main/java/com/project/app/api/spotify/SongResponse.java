package com.project.app.api.spotify;

import java.util.List;

public class SongResponse {

    public AlbumItem album;
    public List<ReducedArtistInfo> artists;
    public ReducedSongInfo songInfo;

    public AlbumItem getAlbum() {
        return album;
    }

    public void setAlbum(AlbumItem album) {
        this.album = album;
    }

    public List<ReducedArtistInfo> getArtists() {
        return artists;
    }

    public void setArtists(List<ReducedArtistInfo> artists) {
        this.artists = artists;
    }

    public ReducedSongInfo getSongInfo() {
        return songInfo;
    }

    public void setSongInfo(ReducedSongInfo songInfo) {
        this.songInfo = songInfo;
    }


}
