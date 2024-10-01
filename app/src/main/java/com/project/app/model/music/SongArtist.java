package com.project.app.model.music;

import jakarta.persistence.*;

@Entity
@Table(name = "song_artist", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"song_id", "artist_id"})
})
public class SongArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;

    public SongArtist() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public SongArtist(Artist artist, Song song) {
        this.artist = artist;
        this.song = song;
    }

    @Override
    public String toString() {
        return "{ artistName: " + artist.getName() + ", songName: " + song.getName() + " }";
    }
}
