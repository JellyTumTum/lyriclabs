package com.project.app.repository.rooms;

import java.util.Optional;

import com.project.app.model.rooms.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.app.model.rooms.RoomConfig;

@Repository
public interface RoomConfigRepository extends JpaRepository<RoomConfig, Long>{
    Optional<RoomConfig> findByRoom_roomId(String roomId);
    // use of findBy"tableName"_"Pkey" is transversing the relationship.

    @Query("SELECT r.maxPlayers FROM RoomConfig r WHERE r.room.roomId = :roomId")
    Integer findMaxPlayersByRoom_roomId(@Param("roomId") String roomId);


    @Modifying
    @Transactional
    @Query("DELETE FROM RoomConfig rc WHERE rc.room.roomId = ?1")
    void deleteByRoomId(String roomId);

    @Modifying
    @Transactional
    @Query("UPDATE RoomConfig rc SET rc.maxPlayers = :maxPlayers WHERE rc.room.roomId = :roomID")
    int updateMaxPlayers(@Param("roomID") String roomID, @Param("maxPlayers") Integer maxPlayers);

    @Modifying
    @Transactional
    @Query("UPDATE RoomConfig rc SET rc.artistCount = :artistCount WHERE rc.room.roomId = :roomID")
    int updateArtistCount(@Param("roomID") String roomID, @Param("artistCount") Integer artistCount);

    @Modifying
    @Transactional
    @Query("UPDATE RoomConfig rc SET rc.usingSongName = :usingSongName WHERE rc.room.roomId = :roomID")
    int updateUsingSongName(@Param("roomID") String roomID, @Param("usingSongName") Boolean usingSongName);

    @Modifying
    @Transactional
    @Query("UPDATE RoomConfig rc SET rc.stationId = :stationId WHERE rc.room.roomId = :roomID")
    int updateStationId(@Param("roomID") String roomID, @Param("stationId") Integer stationId);

    @Modifying
    @Transactional
    @Query("UPDATE RoomConfig rc SET rc.usingStation = :usingStation WHERE rc.room.roomId = :roomID")
    int updateUsingStation(@Param("roomID") String roomID, @Param("usingStation") Boolean usingStation);

    @Modifying
    @Transactional
    @Query("UPDATE RoomConfig rc SET rc.gamemode = :gamemode WHERE rc.room.roomId = :roomID")
    int updateGamemode(@Param("roomID") String roomID, @Param("gamemode") String gamemode);

    @Modifying
    @Transactional
    @Query("UPDATE RoomConfig rc SET rc.maxGuessTime = :maxGuessTime WHERE rc.room.roomId = :roomID")
    int updateMaxGuessTime(@Param("roomID") String roomID, @Param("maxGuessTime") Integer maxGuessTime);

    @Modifying
    @Transactional
    @Query("UPDATE RoomConfig rc SET rc.waitTime = :waitTime WHERE rc.room.roomId = :roomID")
    int updateWaitTime(@Param("roomID") String roomID, @Param("waitTime") Integer waitTime);

    @Modifying
    @Transactional
    @Query("UPDATE RoomConfig rc SET rc.maxSongs = :maxSongs WHERE rc.room.roomId = :roomID")
    int updateMaxSongs(@Param("roomID") String roomID, @Param("maxSongs") Integer maxSongs);

    @Modifying
    @Transactional
    @Query("UPDATE RoomConfig rc SET rc.minSongs = :minSongs WHERE rc.room.roomId = :roomID")
    int updateMinSongs(@Param("roomID") String roomID, @Param("minSongs") Integer minSongs);

    @Modifying
    @Transactional
    @Query("UPDATE RoomConfig rc SET rc.songCount = :songCount WHERE rc.room.roomId = :roomID")
    int updateSongCount(@Param("roomID") String roomID, @Param("songCount") Integer songCount);


}
