package com.project.app.api.GameStats;

import com.project.app.api.Graphs.GameAvgResponseBar;
import com.project.app.api.Graphs.GuessTimeHistogram;

import java.util.List;

public class UserSpecificGraphData {

    String responseMessage;
    GuessTimeHistogram guessTimeHistogram;

    List<RoundGuessStats> artistGuessStats;

    GameAvgResponseBar userArtistBarChart;

    public UserSpecificGraphData() {}

    public UserSpecificGraphData(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public GuessTimeHistogram getGuessTimeHistogram() {
        return guessTimeHistogram;
    }

    public void setGuessTimeHistogram(GuessTimeHistogram guessTimeHistogramsList) {
        this.guessTimeHistogram = guessTimeHistogramsList;
    }

    public GameAvgResponseBar getUserArtistBarChart() {
        return userArtistBarChart;
    }

    public void setUserArtistBarChart(GameAvgResponseBar userArtistBarChart) {
        this.userArtistBarChart = userArtistBarChart;
    }

    public List<RoundGuessStats> getArtistGuessStats() {
        return artistGuessStats;
    }

    public void setArtistGuessStats(List<RoundGuessStats> artistGuessStats) {
        this.artistGuessStats = artistGuessStats;
    }
}
