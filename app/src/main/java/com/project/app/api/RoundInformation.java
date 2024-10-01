package com.project.app.api;

import com.project.app.model.music.Song;

public class RoundInformation {


    int lyricID;
    String lyrics;
    Float lyricWeight;

    long broadcastTimeMS;

    private boolean hasScriptTracking;
    private boolean hasPixelTracking;
    private boolean hasHtmlTracking;

    private String scriptTrackingUrl;
    private String pixelTrackingUrl;
    private String htmlTrackingUrl;
    private String lyricsCopyright;
    private String updatedTime;

    public RoundInformation( Song song, LyricData chosenLyrics) {
        this.lyrics = chosenLyrics.getLyrics();
        if (song.getPixelTrackingUrl() != null) {
            this.hasPixelTracking = true;
            this.pixelTrackingUrl = song.getPixelTrackingUrl();
        }
        if (song.getHtmlTrackingUrl() != null) {
            this.hasHtmlTracking = true;
            this.htmlTrackingUrl = song.getHtmlTrackingUrl();
        }
        if (song.getScriptTrackingUrl() != null) {
            this.hasScriptTracking = true;
            this.scriptTrackingUrl = song.getScriptTrackingUrl();
        }
        this.lyricsCopyright = song.getLyricsCopyright();
        this.updatedTime = song.getUpdatedTime();
        this.lyricID = chosenLyrics.getLyricID();
    }




    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public Float getLyricWeight() {
        return lyricWeight;
    }

    public void setLyricWeight(Float lyricWeight) {
        this.lyricWeight = lyricWeight;
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

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public int getLyricID() {
        return lyricID;
    }

    public void setLyricID(int lyricID) {
        this.lyricID = lyricID;
    }

    public boolean getHasScriptTracking() {
        return hasScriptTracking;
    }

    public void setHasScriptTracking(boolean hasScriptTracking) {
        this.hasScriptTracking = hasScriptTracking;
    }

    public boolean getHasPixelTracking() {
        return hasPixelTracking;
    }

    public void setHasPixelTracking(boolean hasPixelTracking) {
        this.hasPixelTracking = hasPixelTracking;
    }

    public boolean getHasHtmlTracking() {
        return hasHtmlTracking;
    }

    public void setHasHtmlTracking(boolean hasHtmlTracking) {
        this.hasHtmlTracking = hasHtmlTracking;
    }

    public long getBroadcastTime() {
        return broadcastTimeMS;
    }

    public void setBroadcastTime(long broadcastTimeMS) {
        this.broadcastTimeMS = broadcastTimeMS;
    }

    public long getBroadcastTimeMS() {
        return broadcastTimeMS;
    }

    public void setBroadcastTimeMS(long broadcastTimeMS) {
        this.broadcastTimeMS = broadcastTimeMS;
    }

    @Override
    public String toString() {
        return "RoundInformation {" +
                "lyricID=" + lyricID +
                ", lyrics='" + lyrics + '\'' +
                ", lyricWeight=" + lyricWeight +
                ", scriptTrackingUrl='" + scriptTrackingUrl + '\'' +
                ", pixelTrackingUrl='" + pixelTrackingUrl + '\'' +
                ", htmlTrackingUrl='" + htmlTrackingUrl + '\'' +
                ", lyricsCopyright='" + lyricsCopyright + '\'' +
                ", updatedTime='" + updatedTime + '\'' +
                '}';
    }


}
