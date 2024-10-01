package com.project.app.api;

public class StatsResponse {

    private String username;
    private Integer gamesPlayed;
    private Integer gamesWon;
    private String favouriteArtist;
    private Integer favouriteArtistWins;
    private Integer favouriteArtistOccurances;
    private String responseMessage;

    public StatsResponse() {
    }

    public StatsResponse(String username, Integer gamesPlayed, Integer gamesWon, String favouriteArtist, Integer favouriteArtistWins, Integer favouriteArtistOccurances, String responseMessage) {

        this.username = username;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.favouriteArtist = favouriteArtist;
        this.favouriteArtistWins = favouriteArtistWins;
        this.favouriteArtistOccurances = favouriteArtistOccurances;
        this.responseMessage = responseMessage;
    }

    public StatsResponse(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(Integer gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public Integer getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(Integer gamesWon) {
        this.gamesWon = gamesWon;
    }

    public String getFavouriteArtist() {
        return favouriteArtist;
    }

    public void setFavouriteArtist(String favouriteArtist) {
        this.favouriteArtist = favouriteArtist;
    }

    public Integer getFavouriteArtistWins() {
        return favouriteArtistWins;
    }

    public void setFavouriteArtistWins(Integer favouriteArtistWins) {
        this.favouriteArtistWins = favouriteArtistWins;
    }

    public Integer getFavouriteArtistOccurances() {
        return favouriteArtistOccurances;
    }

    public void setFavouriteArtistOccurances(Integer favouriteArtistOccurances) {
        this.favouriteArtistOccurances = favouriteArtistOccurances;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
