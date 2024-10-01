package com.project.app.api.rooms;

public class FrontendGuess {

    String roomID;
    Integer lyricID;
    String artistID;
    Integer guessNumber;
    long guessTimeMS;
    long totalGuessTimeMS;

    Integer roundNumber;

//    TODO: ADD ROUND NUMBER

    public String getArtistID() {
        return artistID;
    }

    public void setArtistID(String artistID) {
        this.artistID = artistID;
    }

    public long getGuessTimeMS() {
        return guessTimeMS;
    }

    public void setGuessTimeMS(long unadjustedGuessTimeMS) {
        this.guessTimeMS = unadjustedGuessTimeMS;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public Integer getLyricID() {
        return lyricID;
    }

    public void setLyricID(Integer lyricID) {
        this.lyricID = lyricID;
    }

    public long getTotalGuessTimeMS() {
        return totalGuessTimeMS;
    }

    public void setTotalGuessTimeMS(long totalGuessTimeMS) {
        this.totalGuessTimeMS = totalGuessTimeMS;
    }

    public Integer getGuessNumber() {
        return guessNumber;
    }

    public void setGuessNumber(Integer guessNumber) {
        this.guessNumber = guessNumber;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    @Override
    public String toString() {
        return "FrontendGuess{" +
                "roomID='" + roomID + '\'' +
                ", id=" + lyricID +
                ", artistID='" + artistID + '\'' +
                ", guessNumber=" + guessNumber +
                ", guessTimeMS=" + guessTimeMS +
                ", totalGuessTimeMS=" + totalGuessTimeMS +
                '}';
    }
}
