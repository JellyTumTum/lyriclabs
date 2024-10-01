package com.project.app.model.rooms;

import com.project.app.model.music.ArtistAlbum;
import com.project.app.service.RoomService;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "room_config")
public class RoomConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "room_id", unique = true)
    private Room room;

    private Integer maxPlayers;
    private Integer artistCount;

    private Boolean usingSongName; // (will not be implemented)

    private Integer stationId; // links to id inside stationTable (will not be implemented)
    private Boolean usingStation; // (will not be implemented)
    private String gamemode; // unimplemented
    private Integer maxGuessTime;
    private Integer waitTime;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER) // Fixes "failed to lazily initialize a collection of role: com.project.app.model.rooms.RoomConfig.roomArtist: could not initialize proxy - no Session". For whenever I get this error again.
    private Set<RoomArtist> roomArtist;

    @OneToMany(mappedBy = "room")
    private Set<RoomSong> roomSong;

    private Integer maxSongs;
    private Integer minSongs;
    private Integer songCount;

    // Empty constructor
    public RoomConfig() {}

    // Full constructor
    public RoomConfig(Room room, Integer maxPlayers, Integer artistCount,
                      Boolean usingSongName, Integer stationId, Boolean usingGenre, String gamemode,
                      Integer maxGuessTime, Integer waitTime, Integer maxSongs,
                      Integer minSongs, Integer songCount) {
        this.room = room;
        this.maxPlayers = maxPlayers;
        this.artistCount = artistCount;
        this.usingSongName = usingSongName;
        this.stationId = 0;
        this.usingStation = usingGenre;
        this.gamemode = gamemode;
        this.maxGuessTime = maxGuessTime;
        this.waitTime = waitTime;
        this.maxSongs = maxSongs;
        this.minSongs = minSongs;
        this.songCount = songCount;
    }

    //default constructor, just needs the room to set the default settings
    public RoomConfig(Room room) {
        this.room = room;
        this.maxPlayers = 10;
        this.artistCount = 0;
        this.usingSongName = false;
        this.stationId = 0;
        this.usingStation = false;
        this.gamemode = "classic";
        this.maxGuessTime = 10;
        this.waitTime = 5;
        this.maxSongs = 20;
        this.minSongs = 10;
        this.songCount = 0;
    }

    public RoomConfig(Room room, boolean isPractice) {
        this.room = room;
        this.maxPlayers = 2;
        this.artistCount = 0;
        this.usingSongName = false;
        this.stationId = 0;
        this.usingStation = false;
        this.gamemode = "practice";
        this.maxGuessTime = -1;
        this.waitTime = 5;
        this.maxSongs = 20;
        this.minSongs = 10;
        this.songCount = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
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

    public Set<RoomArtist> getRoomArtist() {
        return roomArtist;
    }

    public void setRoomArtist(Set<RoomArtist> roomArtist) {
        this.roomArtist = roomArtist;
    }

    public Set<RoomSong> getRoomSong() {
        return roomSong;
    }

    public void setRoomSong(Set<RoomSong> roomSong) {
        this.roomSong = roomSong;
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

    public Integer getSongCount() {
        return songCount;
    }

    public void setSongCount(Integer songCount) {
        this.songCount = songCount;
    }
}