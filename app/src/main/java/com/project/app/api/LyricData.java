package com.project.app.api;

import com.project.app.model.music.Artist;

import java.util.List;

public class LyricData {


    private int lyricID; // attained from musixmatch, but in this context is used as a key.
    private String lyrics;
    private long uniqueWords;
    private long wordCount;

    private float weight;

    private List<Artist> artistList;

    public LyricData(String lyrics, long uniqueWords, long wordCount, int lyricID) {
        this.lyrics = lyrics;
        this.uniqueWords = uniqueWords;
        this.wordCount = wordCount;
        this.lyricID = lyricID;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public long getUniqueWords() {
        return uniqueWords;
    }

    public void setUniqueWords(long uniqueWords) {
        this.uniqueWords = uniqueWords;
    }

    public long getWordCount() {
        return wordCount;
    }

    public void setWordCount(long wordCount) {
        this.wordCount = wordCount;
    }

    public int getLyricID() {
        return lyricID;
    }

    public void setLyricID(int lyricID) {
        this.lyricID = lyricID;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = (float) weight;
    }

    public List<Artist> getArtistList() {
        return artistList;
    }

    public void setArtistList(List<Artist> possibleArtists) {
        this.artistList = possibleArtists;
    }

    @Override
    public String toString() {
        return "LyricData{" +
                "lyricID=" + lyricID +
                ", lyrics= NOT PRINTING DUE TO LENGTH" +
                ", uniqueWords=" + uniqueWords +
                ", wordCount=" + wordCount +
                ", weight=" + weight +
                '}';
    }
}
