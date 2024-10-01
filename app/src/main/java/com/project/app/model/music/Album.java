package com.project.app.model.music;

import com.project.app.api.spotify.AlbumItem;
import com.project.app.api.spotify.ArtistAlbumResponse;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "album")
public class Album {

    @Id
    private String albumId;
    private String name;
    private String mmid;
    private Integer popularity;
    private String artURL;

    private String releaseDate;
    private int trackCount;

    @OneToMany(mappedBy = "album", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<ArtistAlbum> artistAlbums;
    // allows for easy access of all related artists to this album.

    public Album() {}

    public Album(AlbumItem albumItem) {
        this.albumId = albumItem.getId();
        this.name = albumItem.getName();
        // set popularity through an average of all the songs popularities.
        this.artURL = albumItem.selectAlbumArt();
        this.releaseDate = albumItem.getReleaseDate();
        this.trackCount = albumItem.getTotalTracks();
        this.artistAlbums = new HashSet<>();

    }

    public void addArtistAlbum(ArtistAlbum artistAlbum) {
        if (this.artistAlbums == null) {
            this.artistAlbums = new HashSet<>();
        }
        this.artistAlbums.add(artistAlbum);
    }

    public void setArtistAlbums(Set<ArtistAlbum> artistAlbums) {
        this.artistAlbums = artistAlbums;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMmid() {
        return mmid;
    }

    public void setMmid(String mmid) {
        this.mmid = mmid;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public String getArtURL() {
        return artURL;
    }

    public void setArtURL(String artURL) {
        this.artURL = artURL;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public Set<ArtistAlbum> getArtistAlbums() {
        return artistAlbums;
    }
}
