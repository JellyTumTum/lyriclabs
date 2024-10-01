package com.project.app.api.GameStats;

import com.project.app.api.rooms.FrontendArtist;

public class RoundGuessStats {

    Integer totalRounds;
    Integer correctGuesses;
    Double percentCorrect;
    FrontendArtist frontendArtist;

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
    }

    public Integer getCorrectGuesses() {
        return correctGuesses;
    }

    public void setCorrectGuesses(Integer correctGuesses) {
        this.correctGuesses = correctGuesses;
    }

    public Double getPercentCorrect() {
        return percentCorrect;
    }

    public void setPercentCorrect(Double percentCorrect) {
        this.percentCorrect = percentCorrect;
    }

    public FrontendArtist getFrontendArtist() {
        return frontendArtist;
    }

    public void setFrontendArtist(FrontendArtist frontendArtist) {
        this.frontendArtist = frontendArtist;
    }

    public RoundGuessStats(Integer totalRounds, Integer correctGuesses, Double percentCorrect, FrontendArtist frontendArtist) {
        this.totalRounds = totalRounds;
        this.correctGuesses = correctGuesses;
        this.percentCorrect = percentCorrect;
        this.frontendArtist = frontendArtist;
    }
}
