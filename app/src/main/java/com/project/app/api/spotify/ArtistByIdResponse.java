package com.project.app.api.spotify;

public class ArtistByIdResponse {
    private String artistId;
    private String name;
    private String artURL;
    private Long followerCount;
    private Integer popularity;

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

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }
}