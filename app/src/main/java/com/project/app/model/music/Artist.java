package com.project.app.model.music;


import com.project.app.api.spotify.AlbumItem;
import com.project.app.api.spotify.ArtistItem;
import com.project.app.api.spotify.ArtistSearchResponse;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "artist")
public class Artist {

    @Id
    private String artistId;
    private String name;
    private String mmid;
    private Integer popularity;
    private String artURL;
    private Long followerCount;

    private Integer genreId; // give each artist one genre, just in case but not really that useful cause there are over 5000 of them

    @OneToMany(mappedBy = "artist")
    private Set<ArtistAlbum> artistAlbums;

    @OneToMany(mappedBy = "artist")
    private Set<SongArtist> songArtists;

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
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

    public Long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Long followerCount) {
        this.followerCount = followerCount;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public Set<ArtistAlbum> getArtistAlbums() {
        return artistAlbums;
    }

    public void setArtistAlbums(Set<ArtistAlbum> artistAlbums) {
        this.artistAlbums = artistAlbums;
    }

    public Set<SongArtist> getSongArtists() {
        return songArtists;
    }

    public void setSongArtists(Set<SongArtist> songArtists) {
        this.songArtists = songArtists;
    }

    public Artist(ArtistItem apiResponse) {
        this.setArtistId(apiResponse.getId());
        this.setName(apiResponse.getName());
        this.setPopularity(apiResponse.getPopularity());
        this.setArtURL(apiResponse.selectProfilePicture());
        this.setFollowerCount((long) apiResponse.getFollowers().getTotal());
    }

    public Artist() {}

//    @Override
//    public String toString() {
//        return "Artist {" +
//                "artistId='" + artistId + '\'' +
//                ", name='" + name + '\'' +
//                ", mmid='" + mmid + '\'' +
//                ", popularity=" + popularity +
//                ", artURL='" + artURL + '\'' +
//                ", followerCount=" + followerCount +
//                ", genreId=" + genreId +
//                ", artistAlbums=" + (artistAlbums != null ? artistAlbums.size() : "null") +
//                ", songArtists=" + (songArtists != null ? songArtists.size() : "null") +
//                '}';
//    }

    @Override
    public String toString() {
        return this.getName();
    }


    // needed for the .retainAll() function to work.
    @Override
    public boolean equals(Object potentialArtist) {
        if (this == potentialArtist) return true;
        if (potentialArtist == null || getClass() != potentialArtist.getClass()) return false;
        Artist artist = (Artist) potentialArtist;
        return Objects.equals(artistId, artist.artistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistId);
    }

}

