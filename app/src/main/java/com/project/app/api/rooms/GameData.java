package com.project.app.api.rooms;

import com.project.app.api.LyricData;
import com.project.app.api.RoundInformation;
import com.project.app.model.music.Album;
import com.project.app.model.music.Artist;
import com.project.app.model.music.Song;

import java.util.*;

public class GameData {

    Set<Artist> artistsList;

    List<Artist> selectedArtists; // differs from artistsList, as this is just the ones selected by the user.

    Set<Song> songList;

    List<Song> spareSongs;
    Map<Integer, RoundInformation> roundInformationMap;

    Map<Integer, LyricData> answerMap;

    Map<String, List<Album>> albumArtistMap;

    Map<String, Integer> userConnections;

    LyricBroadcast lastBroadcast;

    Integer maxAllowedGuessTime;

    Integer roundCount;

    Integer currentRound;

    boolean isPractice;

    public GameData() {
        this.artistsList = new HashSet<>();
        this.songList = new HashSet<>();
        this.answerMap = new HashMap<>();
        this.albumArtistMap = new HashMap<>();
        this.selectedArtists = new ArrayList<>();
        this.userConnections = new HashMap<>();
        this.roundCount = -1;
        this.currentRound = 0;
        this.isPractice = false;
    }

    public GameData(boolean isPractice) {
        this.artistsList = new HashSet<>();
        this.songList = new HashSet<>();
        this.answerMap = new HashMap<>();
        this.albumArtistMap = new HashMap<>();
        this.selectedArtists = new ArrayList<>();
        this.userConnections = new HashMap<>();
        this.roundCount = -1;
        this.currentRound = 0;
        this.isPractice = isPractice;
    }

    public Map<Integer, RoundInformation> getRoundInformationMap() {
        return roundInformationMap;
    }

    public void setRoundInformationMap(Map<Integer, RoundInformation> roundInformationMap) {
        this.roundInformationMap = roundInformationMap;
    }

    public Set<Artist> getArtistsList() {
        return artistsList;
    }

    public void setArtistsList(Set<Artist> artistsList) {
        this.artistsList = artistsList;
    }

    public Set<Song> getSongList() {
        return songList;
    }

    public void setSongList(Set<Song> songList) {
        this.songList = songList;
    }

    public Map<Integer, LyricData> getAnswers() {
        return answerMap;
    }

    public void setAnswers(Map<Integer, LyricData> answerMap) {
        this.answerMap = answerMap;
    }

    public void addLyricData(LyricData data) {
        this.answerMap.put(data.getLyricID(), data);
    }

    public void addSong(Song song) {
        this.songList.add(song);
    }

    public LyricData findAnswerData(Integer lyricID) {
        return answerMap.get(lyricID);
    }
    public long getMaxGuessTime() {
        return maxAllowedGuessTime;
    }

    public void setMaxGuessTime(Integer maxGuessTime) {
        this.maxAllowedGuessTime = maxGuessTime;
    }

    public Map<Integer, LyricData> getAnswerMap() {
        return answerMap;
    }

    public void setAnswerMap(Map<Integer, LyricData> answerMap) {
        this.answerMap = answerMap;
    }

    public Map<String, List<Album>> getAlbumArtistMap() {
        return albumArtistMap;
    }

    public void setAlbumArtistMap(List<List<Album>> albumList, List<Artist> artistList) {
        // Ensure that both lists have the same size to avoid IndexOutOfBoundsException
        if (albumList.size() != artistList.size()) {
            throw new IllegalArgumentException("The size of albumList and artistList must be the same.");
        }

        for (int i = 0; i < artistList.size(); i++) {
            this.albumArtistMap.put(artistList.get(i).getArtistId(), albumList.get(i));
        }
    }

    public Integer getMaxAllowedGuessTime() {
        return maxAllowedGuessTime;
    }

    public void setMaxAllowedGuessTime(Integer maxAllowedGuessTime) {
        this.maxAllowedGuessTime = maxAllowedGuessTime;
    }

    public List<Artist> getSelectedArtists() {
        return selectedArtists;
    }

    public void setSelectedArtists(Set<Artist> selectedArtists) {
        this.selectedArtists = new ArrayList<>(selectedArtists);
    }

    public List<Song> getSpareSongs() {
        return spareSongs;
    }

    public void setSpareSongs(List<Song> spareSongs) {
        this.spareSongs = spareSongs;
    }

    public Map<String, Integer> getUserConnections() {
        return userConnections;
    }

    public void setUserConnections(Map<String, Integer> userConnections) {
        this.userConnections = userConnections;
    }

    public void setAlbumArtistMap(Map<String, List<Album>> albumArtistMap) {
        this.albumArtistMap = albumArtistMap;
    }

    public LyricBroadcast getLastBroadcast() {
        return lastBroadcast;
    }

    public void setLastBroadcast(LyricBroadcast lastBroadcast) {
        this.lastBroadcast = lastBroadcast;
    }

    public Integer getRoundCount() {
        return roundCount;
    }

    public void setRoundCount(Integer roundCount) {
        this.roundCount = roundCount;
    }

    public void setSelectedArtists(List<Artist> selectedArtists) {
        this.selectedArtists = selectedArtists;
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    public boolean isPractice() {
        return isPractice;
    }

    public void setPractice(boolean practice) {
        isPractice = practice;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GameData{\n");

        sb.append("  Artists List: ").append(artistsList).append(",\n");
        sb.append("  Song List: ").append(songList).append(",\n");
        sb.append("  Answer Map: ").append(answerMap).append(",\n");
        sb.append("  Max Allowed Guess Time: ").append(maxAllowedGuessTime).append("\n");
        sb.append("  RoundCount: ").append(roundCount).append("\n");
        sb.append("  RoundInformationMap: ").append(roundInformationMap).append("\n");

        sb.append('}');
        return sb.toString();
    }
}
