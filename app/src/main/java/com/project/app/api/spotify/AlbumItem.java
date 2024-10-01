package com.project.app.api.spotify;

import java.util.List;
import java.util.Map;

public class AlbumItem {
    private String albumType;
    private int totalTracks;
    private List<String> availableMarkets;
    private Map<String, String> externalUrls;
    private String href;
    private String id;
    private List<Image> images;
    private String name;
    private String releaseDate;
    private String releaseDatePrecision;
    private Restrictions restrictions;
    private String type;
    private String uri;
    private List<ReducedArtistInfo> artists;
    private String albumGroup;

    public String selectAlbumArt() {
        for (Image image : images) {
            if (image.getWidth() == 300) {
                return image.getUrl();
            }
        }
        //TODO: Get a defaultImageURL to provide if no image that fits makes sense.
        return images.isEmpty() ? "default" : images.get(0).getUrl();
    }

    public String getAlbumType() {
        return albumType;
    }

    public void setAlbumType(String albumType) {
        this.albumType = albumType;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
    }

    public List<String> getAvailableMarkets() {
        return availableMarkets;
    }

    public void setAvailableMarkets(List<String> availableMarkets) {
        this.availableMarkets = availableMarkets;
    }

    public Map<String, String> getExternalUrls() {
        return externalUrls;
    }

    public void setExternalUrls(Map<String, String> externalUrls) {
        this.externalUrls = externalUrls;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDatePrecision() {
        return releaseDatePrecision;
    }

    public void setReleaseDatePrecision(String releaseDatePrecision) {
        this.releaseDatePrecision = releaseDatePrecision;
    }

    public Restrictions getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Restrictions restrictions) {
        this.restrictions = restrictions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<ReducedArtistInfo> getArtists() {
        return artists;
    }

    public void setArtists(List<ReducedArtistInfo> artists) {
        this.artists = artists;
    }

    public String getAlbumGroup() {
        return albumGroup;
    }

    public void setAlbumGroup(String albumGroup) {
        this.albumGroup = albumGroup;
    }



}


