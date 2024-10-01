package com.project.app.api.rooms;

import com.project.app.api.RoundInformation;

public class LyricBroadcast {

    String responseMessage;
    String lyrics;

    Integer lyricID;

    long backupSendTimeMS;

    int roundNumber;
    int totalRounds;
    int maxGuessTime;

    private String scriptTrackingUrl;
    private String pixelTrackingUrl;
    private String htmlTrackingUrl;
    private String lyricsCopyright;

    private boolean hasScriptTracking;
    private boolean hasPixelTracking;
    private boolean hasHtmlTracking;


    long currentTimeMS = System.currentTimeMillis();

    public LyricBroadcast(String responseMessage, RoundInformation lyricData, int roundNumber, int totalRounds, int maxGuessTime, long currentTimeMS) {

        this.responseMessage = responseMessage;
        this.lyrics = lyricData.getLyrics();
        this.scriptTrackingUrl = lyricData.getScriptTrackingUrl();
        this.hasScriptTracking = lyricData.getHasScriptTracking();
        this.pixelTrackingUrl = lyricData.getPixelTrackingUrl();
        this.hasPixelTracking = lyricData.getHasPixelTracking();
        this.htmlTrackingUrl = lyricData.getHtmlTrackingUrl();
        this.hasHtmlTracking = lyricData.getHasHtmlTracking();
        this.lyricsCopyright = lyricData.getLyricsCopyright();
        this.roundNumber = roundNumber;
        this.totalRounds = totalRounds;
        this.maxGuessTime = maxGuessTime;
        this.currentTimeMS = currentTimeMS;
        this.lyricID = lyricData.getLyricID();
        this.backupSendTimeMS = System.currentTimeMillis();
    }

    public LyricBroadcast() {
        this.responseMessage = "";
        this.lyrics = "";
        this.roundNumber = 0;
        this.totalRounds = 0;
        this.maxGuessTime = 10;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public int getMaxGuessTime() {
        return maxGuessTime;
    }

    public void setMaxGuessTime(int maxGuessTime) {
        this.maxGuessTime = maxGuessTime;
    }

    public String getScriptTrackingUrl() {
        return scriptTrackingUrl;
    }

    public void setScriptTrackingUrl(String scriptTrackingUrl) {
        this.scriptTrackingUrl = scriptTrackingUrl;
    }

    public String getPixelTrackingUrl() {
        return pixelTrackingUrl;
    }

    public void setPixelTrackingUrl(String pixelTrackingUrl) {
        this.pixelTrackingUrl = pixelTrackingUrl;
    }

    public String getHtmlTrackingUrl() {
        return htmlTrackingUrl;
    }

    public void setHtmlTrackingUrl(String htmlTrackingUrl) {
        this.htmlTrackingUrl = htmlTrackingUrl;
    }

    public String getLyricsCopyright() {
        return lyricsCopyright;
    }

    public void setLyricsCopyright(String lyricsCopyright) {
        this.lyricsCopyright = lyricsCopyright;
    }

    public long getCurrentTimeMS() {
        return currentTimeMS;
    }

    public void setCurrentTimeMS(long currentTimeMS) {
        this.currentTimeMS = currentTimeMS;
    }

    public boolean isHasScriptTracking() {
        return hasScriptTracking;
    }

    public void setHasScriptTracking(boolean hasScriptTracking) {
        this.hasScriptTracking = hasScriptTracking;
    }

    public boolean isHasPixelTracking() {
        return hasPixelTracking;
    }

    public void setHasPixelTracking(boolean hasPixelTracking) {
        this.hasPixelTracking = hasPixelTracking;
    }

    public boolean isHasHtmlTracking() {
        return hasHtmlTracking;
    }

    public void setHasHtmlTracking(boolean hasHtmlTracking) {
        this.hasHtmlTracking = hasHtmlTracking;
    }

    public Integer getLyricID() {
        return lyricID;
    }

    public void setLyricID(Integer lyricID) {
        this.lyricID = lyricID;
    }

    @Override
    public String toString() {
        return "LyricBroadcast{" +
                "responseMessage='" + responseMessage + '\'' +
                ", lyrics='" + lyrics + '\'' +
                ", roundNumber=" + roundNumber +
                ", totalRounds=" + totalRounds +
                ", maxGuessTime=" + maxGuessTime +
                ", lyricID=" + lyricID +
                '}';
    }


}
