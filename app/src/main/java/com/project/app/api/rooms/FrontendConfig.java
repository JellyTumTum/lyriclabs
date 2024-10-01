package com.project.app.api.rooms;

import com.project.app.model.rooms.RoomConfig;

import java.util.ArrayList;
import java.util.List;


public class FrontendConfig {

    private List<FrontendArtist> artistList;
    private Integer maxPlayers;
    private Integer artistCount;
    private Boolean usingSongName;
    private String stationName; // links to id inside stationTable (unimplemented)
    private Boolean usingStation;
    private String gamemode;
    private Integer maxGuessTime;
    private Integer waitTime;
    private Integer maxSongs;
    private Integer minSongs;

    public FrontendConfig() {
    }

    public FrontendConfig(RoomConfig roomConfig) {
        this.maxPlayers = roomConfig.getMaxPlayers();
        this.artistCount = roomConfig.getArtistCount();
        this.artistList = new ArrayList<>();
        this.usingSongName = roomConfig.getUsingSongName();
        this.stationName = "";
        this.usingStation = roomConfig.getUsingStation();
        this.gamemode = roomConfig.getGamemode();
        this.maxGuessTime = roomConfig.getMaxGuessTime();
        this.waitTime = roomConfig.getWaitTime();
        this.maxSongs = roomConfig.getMaxSongs();
        this.minSongs = roomConfig.getMinSongs();
    }

    public FrontendConfig(List<FrontendArtist> artistNames, RoomConfig roomConfig) {
        this.maxPlayers = roomConfig.getMaxPlayers();
        this.artistCount = roomConfig.getArtistCount();
        this.artistList = artistNames;
        this.usingSongName = roomConfig.getUsingSongName();
        this.stationName = "";
        this.usingStation = roomConfig.getUsingStation();
        this.gamemode = roomConfig.getGamemode();
        this.maxGuessTime = roomConfig.getMaxGuessTime();
        this.waitTime = roomConfig.getWaitTime();
        this.maxSongs = roomConfig.getMaxSongs();
        this.minSongs = roomConfig.getMinSongs();

    }

    public FrontendConfig(List<FrontendArtist> artistNames, RoomConfig roomConfig, String stationName) {
        this.maxPlayers = roomConfig.getMaxPlayers();
        this.artistCount = roomConfig.getArtistCount();
        this.artistList = artistNames;
        this.usingSongName = roomConfig.getUsingSongName();
        this.stationName = stationName;
        this.usingStation = roomConfig.getUsingStation();
        this.gamemode = roomConfig.getGamemode();
        this.maxGuessTime = roomConfig.getMaxGuessTime();
        this.waitTime = roomConfig.getWaitTime();
        this.maxSongs = roomConfig.getMaxSongs();
        this.minSongs = roomConfig.getMinSongs();

    }

    public List<FrontendArtist> getArtistList() {
        return artistList;
    }

    public void setArtistList(List<FrontendArtist> artistList) {
        this.artistList = artistList;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getArtistCount() {
        return artistCount;
    }

    public void setArtistCount(Integer artistCount) {
        this.artistCount = artistCount;
    }

    public Boolean getUsingSongName() {
        return usingSongName;
    }

    public void setUsingSongName(Boolean usingSongName) {
        this.usingSongName = usingSongName;
    }

    public String getStationName() {
        return this.stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Boolean getUsingStation() {
        return usingStation;
    }

    public void setUsingStation(Boolean usingStation) {
        this.usingStation = usingStation;
    }

    public String getGamemode() {
        return gamemode;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public Integer getMaxGuessTime() {
        return maxGuessTime;
    }

    public void setMaxGuessTime(Integer maxGuessTime) {
        this.maxGuessTime = maxGuessTime;
    }

    public Integer getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Integer waitTime) {
        this.waitTime = waitTime;
    }

    public Integer getMaxSongs() {
        return maxSongs;
    }

    public void setMaxSongs(Integer maxSongs) {
        this.maxSongs = maxSongs;
    }

    public Integer getMinSongs() {
        return minSongs;
    }

    public void setMinSongs(Integer minSongs) {
        this.minSongs = minSongs;
    }


}

