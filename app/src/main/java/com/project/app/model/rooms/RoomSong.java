package com.project.app.model.rooms;

import com.project.app.model.music.Song;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "room_song")
public class RoomSong {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomConfig room;

    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;

}
