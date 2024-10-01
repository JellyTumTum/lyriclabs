package com.project.app.api;

import com.project.app.model.music.Album;
import com.project.app.model.music.Artist;
import com.project.app.model.music.Song;

import java.util.List;

public class WrappedSongChoice {

    // List<Album> artistAlbumChoice;
    int artistAlbumIndex;
    // Album albumChoice;
    int albumIndex;
    Song chosenSong;

    public WrappedSongChoice(int artistAlbumIndex, int albumIndex, Song chosenSong) {
        this.artistAlbumIndex = artistAlbumIndex;
        this.albumIndex = albumIndex;
        this.chosenSong = chosenSong;
    }

    public int getArtistAlbumIndex() {
        return artistAlbumIndex;
    }

    public void setArtistAlbumIndex(int artistAlbumIndex) {
        this.artistAlbumIndex = artistAlbumIndex;
    }

    public int getAlbumIndex() {
        return albumIndex;
    }

    public void setAlbumIndex(int albumIndex) {
        this.albumIndex = albumIndex;
    }

    public Song getChosenSong() {
        return chosenSong;
    }

    public void setChosenSong(Song chosenSong) {
        this.chosenSong = chosenSong;
    }
}
