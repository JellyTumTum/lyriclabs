package com.project.app.model.music;

import com.project.app.api.spotify.ArtistAlbumResponse;
import jakarta.persistence.*;


@Entity
@Table(name = "artist_album")
public class ArtistAlbum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    private Boolean is_main_artist;

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

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Boolean getIs_main_artist() {
        return is_main_artist;
    }

    public void setIs_main_artist(Boolean is_main_artist) {
        this.is_main_artist = is_main_artist;
    }

    public ArtistAlbum(Artist artist, Album album) {
        this.artist = artist;
        this.album = album;
    }

    public ArtistAlbum() {}
}

