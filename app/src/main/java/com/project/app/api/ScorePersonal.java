package com.project.app.api;

public class ScorePersonal {

    Integer score;
    String responseMessage;
    long guessTime;

    String currentArtistName;
    String currentSongName;
    String currentArtistPhotoURL;

    boolean isCorrect;

    public ScorePersonal(String responseMessage, Integer score, long guessTime, boolean isCorrect, String currentArtistName, String currentSongName, String currentArtistPhotoURL) {
        this.responseMessage = responseMessage;
        this.score = score;
        this.guessTime = guessTime;
        this.isCorrect = isCorrect;
        this.currentArtistName = currentArtistName;
        this.currentSongName = currentSongName;
        this.currentArtistPhotoURL = currentArtistPhotoURL;
    }

    public ScorePersonal(String responseMessage, Integer score, long guessTime, boolean isCorrect) {
        this.responseMessage = responseMessage;
        this.score = score;
        this.guessTime = guessTime;
        this.isCorrect = isCorrect;
    }



    public ScorePersonal() {}

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public long getGuessTime() {
        return guessTime;
    }

    public void setGuessTime(long guessTime) {
        this.guessTime = guessTime;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getCurrentArtistName() {
        return currentArtistName;
    }

    public void setCurrentArtistName(String currentArtistName) {
        this.currentArtistName = currentArtistName;
    }

    public String getCurrentSongName() {
        return currentSongName;
    }

    public void setCurrentSongName(String currentSongName) {
        this.currentSongName = currentSongName;
    }

    public String getCurrentArtistPhotoURL() {
        return currentArtistPhotoURL;
    }

    public void setCurrentArtistPhotoURL(String currentArtistPhotoURL) {
        this.currentArtistPhotoURL = currentArtistPhotoURL;
    }
}

