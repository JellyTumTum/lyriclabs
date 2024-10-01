package com.project.app.model.music;

import com.project.app.api.musixmatch.MatcherLyricsResponse;
import com.project.app.api.spotify.ReducedSongInfo;
import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "song")
public class Song {

    @Id
    private String songId;
    private String name;
    private Boolean isExplicit;
    @Lob
    @Column(columnDefinition="TEXT")
    private String lyrics; // Using @Lob for large objects, ideal for large text

    @Column(name="lyric_id")
    private Integer lyricID;
    @Lob
    @Column(columnDefinition="TEXT")
    private String scriptTrackingUrl;
    @Lob
    @Column(columnDefinition="TEXT")
    private String pixelTrackingUrl;
    @Lob
    @Column(columnDefinition="TEXT")
    private String htmlTrackingUrl;
    private String lyrics_copyright;
    private String updated_time;

    private Integer popularity; // -1 indicates not fetched.

    @Column(name="last_accessed")
    private LocalDateTime lastAccessed;

    @ManyToOne
    @JoinColumn(name = "main_album_id")
    private Album mainAlbum;

    @OneToMany(mappedBy = "song", fetch = FetchType.EAGER)
    private Set<SongArtist> songArtists;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getExplicit() {
        return isExplicit;
    }

    public void setExplicit(Boolean explicit) {
        isExplicit = explicit;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public Album getMainAlbum() {
        return mainAlbum;
    }

    public void setMainAlbum(Album mainAlbum) {
        this.mainAlbum = mainAlbum;
    }

    public Set<SongArtist> getSongArtists() {
        try {
            Set<SongArtist> temp = this.songArtists;
            return temp;
        } catch (Exception e) {
            System.err.println("An error occurred in getSongArtists: " + e.getMessage());
            e.printStackTrace();
            // Optionally, you can return a default value like an empty set or handle the exception as needed
            return new HashSet<>();
        }
    }

    public void setSongArtists(Set<SongArtist> songArtists) {
        this.songArtists = songArtists;
    }

    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(LocalDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public String getScriptTrackingUrl() {
        return scriptTrackingUrl;
    }

    public void setScriptTrackingUrl(String script_tracking_url) {
        this.scriptTrackingUrl = script_tracking_url;
    }

    public void setPixelTrackingUrl(String pixel_tracking_url) {
        this.pixelTrackingUrl = pixel_tracking_url;
    }

    public void setHtmlTrackingUrl(String html_tracking_url) {
        this.htmlTrackingUrl = html_tracking_url;
    }

    public String getLyricsCopyright() {
        return lyrics_copyright;
    }

    public void setLyrics_copyright(String lyrics_copyright) {
        this.lyrics_copyright = lyrics_copyright;
    }

    public String getUpdatedTime() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public void addLyricInformation(MatcherLyricsResponse lyricInfo) {
        String lyricBody = lyricInfo.getMessage().getBody().getLyrics().getLyricsBody();

//        int tripleDotIndex = lyricBody.indexOf("..."); // RIP first method, turns out songs use ... just in their lyrics. (22/01/24) shout out 'Send My Love to your new Lover' by adele for letting me notice this.
        int endIndex = lyricBody.indexOf("\n...\n\n******* This Lyrics is NOT for Commercial use *******");
        // doubt this bad boy will turn up in someone's bars. hopefully. (22/01/24)

        if (endIndex != -1) {
            this.lyrics = lyricBody.substring(0, endIndex);
        } else if (!lyricBody.isEmpty()) {
            this.lyrics = lyricBody;
        } else {
            this.lyrics = "Error Fetching lyrics: see Song.java";
        }
        this.lyricID = lyricInfo.getMessage().getBody().getLyrics().getLyricsID();
        this.scriptTrackingUrl = lyricInfo.getMessage().getBody().getLyrics().getScriptTrackingUrl();
        this.pixelTrackingUrl = lyricInfo.getMessage().getBody().getLyrics().getPixelTrackingUrl();
        this.htmlTrackingUrl = lyricInfo.getMessage().getBody().getLyrics().getHtmlTrackingUrl();
        this.lyrics_copyright = lyricInfo.getMessage().getBody().getLyrics().getLyricsCopyright();
        this.lastAccessed = LocalDateTime.now();
    }

    public void removeLyricInformation() {
        this.lyricID = -1;
        this.lyrics = "";
        this.scriptTrackingUrl = "";
        this.pixelTrackingUrl = "";
        this.htmlTrackingUrl = "";
        this.lyrics_copyright = "";
        this.lastAccessed = LocalDateTime.now();
    }

    public Song(ReducedSongInfo songItem, Album mainAlbum) {
        this.songId = songItem.getId();
        this.name = songItem.getName();
        this.isExplicit = songItem.isExplicit();
        this.lyrics = "";
        this.mainAlbum = mainAlbum;
        this.lyricID = -1;
        if (songItem.getPopularity() != null) {
            this.popularity = songItem.getPopularity();
        } else {
            this.popularity = -1;
        }
        this.lastAccessed = LocalDateTime.now();
    }

    public void addSongArtist(SongArtist songArtist) {
        if (this.songArtists == null) {
            this.songArtists = new HashSet<>();
            this.songArtists.add(songArtist);
        } else {
            this.songArtists.add(songArtist);
        }

    }

    public int getLyricID() {
        return lyricID;
    }

    public void setLyricID(int lyricID) {
        this.lyricID = lyricID;
    }

    public String getPixelTrackingUrl() {
        return pixelTrackingUrl;
    }

    public String getHtmlTrackingUrl() {
        return htmlTrackingUrl;
    }

    public String getLyrics_copyright() {
        return lyrics_copyright;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public Song() {}

    @Override
    public String toString() {
        return " {songName : " + this.name + ", spotifyID : " + this.songId + ", lyricID = " + this.lyricID + "} ";
    }




}

