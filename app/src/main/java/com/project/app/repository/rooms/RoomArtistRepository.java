package com.project.app.repository.rooms;

import java.util.List;
import java.util.Optional;

import com.project.app.model.music.Artist;
import com.project.app.model.rooms.Room;
import com.project.app.model.rooms.RoomArtist;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.app.model.rooms.RoomConfig;

@Repository
public interface RoomArtistRepository extends JpaRepository<RoomArtist, String>{

    @Query("SELECT ra.artist FROM RoomArtist ra WHERE ra.room.room.roomId = :roomId")
    List<Artist> findArtistsByRoomId(@Param("roomId") String roomId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomArtist ra WHERE ra.room = :roomConfig AND ra.artist.artistId = :artistId")
    void deleteByRoomIdAndArtistId(@Param("roomConfig") RoomConfig roomConfig, @Param("artistId") String artistId);

}

//    @Modifying
//    @Transactional
//    @Query("DELETE FROM RoomArtist ra WHERE ra.room.room.roomId = :roomId")
//    void deleteByRoomId(@Param("roomId") String roomId);
//     --> Use musicService.clearRoomArtistByRoom(), due to relationship with roomConfig having the Set<Artist> in, this doesnt really work.

