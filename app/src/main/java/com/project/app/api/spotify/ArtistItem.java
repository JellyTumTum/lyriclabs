package com.project.app.api.spotify;

import java.util.List;

public class ArtistItem {
    private String id;
    private String name;
    private List<String> genres;
    private int popularity;
    private List<Image> images;
    private ArtistSearchResponse.Followers followers;
    private String href;

    public String selectProfilePicture() {
        for (Image image : images) {
            if (image.getWidth() == 300) {
                return image.getUrl();
            }
        }
        //TODO: Get a defaultImageURL to provide if no image that fits makes sense.
        return images.isEmpty() ? "default" : images.get(0).getUrl();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public ArtistSearchResponse.Followers getFollowers() {
        return followers;
    }

    public void setFollowers(ArtistSearchResponse.Followers followers) {
        this.followers = followers;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

        @Override
        public String toString() {
            return "ArtistItem{\n" +
//                    "  id='" + id + '\'' + "\n" +
                    "  name='" + name + '\'' + "\n" +
//                    "  genres=" + genres + "\n" +
//                    "  popularity=" + popularity + "\n" +
//                    "  images=" + images + "\n" +
//                    "  followers=" + (followers == null ? "null" : followers.toString()) + "\n" +
//                    "  href='" + href + '\'' + "\n" +
                    '}';
        }

//    @Override
//    public String toString() {
//        return "ArtistItem{\n" +
//                "  name='" + name + '\'' + "\n" +
//                '}';
//    }

    public ArtistItem() {
    }
}
