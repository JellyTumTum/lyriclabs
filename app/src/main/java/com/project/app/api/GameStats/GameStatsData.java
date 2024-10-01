package com.project.app.api.GameStats;

import com.project.app.api.Graphs.CorrectGuessLineGraph;
import com.project.app.api.Graphs.GameAvgResponseBar;
import com.project.app.api.Graphs.GenericGuess;
import com.project.app.api.Graphs.SimplePieChart;
import com.project.app.api.rooms.FrontendArtist;
import com.project.app.model.ApplicationUser;
import com.project.app.model.music.Artist;
import com.project.app.model.rooms.RoomArchive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;

public class GameStatsData {

    // TODO: Fill with GraphData etc.
    RoomArchive roomArchive;

    // both are inside roomArchive, however are concat strings so best to place them in their own lists for access.
    List<ApplicationUser> userList;
    List<Artist> artistList;

    List<GenericGuess> genericGuesses;

    GameAvgResponseBar gameAvgResponseBar;

    Map<String,  GameAvgResponseBar> gameAvgResponseArtistBar = new HashMap<>();

    Map<String, SimplePieChart> pieChartMap = new HashMap<>();

    CorrectGuessLineGraph correctGuessLineGraph;

    public GameStatsData() {}

    public GameStatsData(RoomArchive ra, List<ApplicationUser> ul, List<Artist> al) {
        this.roomArchive = ra;
        this.userList = ul;
        this.artistList = al;

    }

    public RoomArchive getRoomArchive() {
        return roomArchive;
    }

    public void setRoomArchive(RoomArchive roomArchive) {
        this.roomArchive = roomArchive;
    }

    public List<ApplicationUser> getUserList() {
        return userList;
    }

    public void setUserList(List<ApplicationUser> userList) {
        this.userList = userList;
    }

    public List<Artist> getArtistList() {
        return artistList;
    }

    public void setArtistList(List<Artist> artistList) {
        this.artistList = artistList;
    }

    public List<GenericGuess> getGenericGuesses() {
        return genericGuesses;
    }

    public void setGenericGuesses(List<GenericGuess> genericGuesses) {
        this.genericGuesses = genericGuesses;
    }

    public GameAvgResponseBar getGameAvgResponseBar() {
        return gameAvgResponseBar;
    }

    public void setGameAvgResponseBar(GameAvgResponseBar gameAvgResponseBar) {
        this.gameAvgResponseBar = gameAvgResponseBar;
    }

    public CorrectGuessLineGraph getCorrectGuessLineGraph() {
        return correctGuessLineGraph;
    }

    public void setCorrectGuessLineGraph(CorrectGuessLineGraph correctGuessLineGraph) {
        this.correctGuessLineGraph = correctGuessLineGraph;
    }

    @Override
    public String toString() {
        return "GameStatsData(" + getRoomArchive().getRoomId() + "){\n userIDList = " + getUserList() + "\n artistList = " + getArtistList() + "}";
    }

    public Map<String, GameAvgResponseBar> getGameAvgResponseArtistBar() {
        return gameAvgResponseArtistBar;
    }

    public void setGameAvgResponseArtistBar(Map<String, GameAvgResponseBar> gameAvgResponseArtistBar) {
        this.gameAvgResponseArtistBar = gameAvgResponseArtistBar;
    }

    public Map<String, SimplePieChart> getPieChartMap() {
        return pieChartMap;
    }

    public void setPieChartMap(Map<String, SimplePieChart> pieChartMap) {
        this.pieChartMap = pieChartMap;
    }
}
