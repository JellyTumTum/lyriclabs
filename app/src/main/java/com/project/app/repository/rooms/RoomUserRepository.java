package com.project.app.repository.rooms;

import java.util.List;
import java.util.Optional;

import com.project.app.model.ApplicationUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.project.app.model.rooms.RoomUser;

@Repository
public interface RoomUserRepository extends JpaRepository<RoomUser, Long> {
    Optional<RoomUser> findByRoom_roomId(String roomId);

    Optional<RoomUser> findByUser(ApplicationUser user);

    @Query("SELECT r.score FROM RoomUser r WHERE r.user = ?1")
    Integer findScoreByUser(ApplicationUser user);

    @Query("SELECT COUNT(r) FROM RoomUser r WHERE r.room.roomId = ?1")
    Long countByRoomID(String roomID);

    @Transactional
    @Modifying
    @Query("UPDATE RoomUser r SET r.isConnected = ?2 WHERE r.user = ?1")
    void updateIsConnectedByUser(ApplicationUser user, boolean value);

    @Query("SELECT r FROM RoomUser r WHERE r.room.roomId = ?1")
    List<RoomUser> findRoomUsersByRoomID(String roomId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomUser ru WHERE ru.room.roomId = ?1")
    void deleteByRoomId(String roomId);


    @Transactional
    @Modifying
    @Query("UPDATE RoomUser r SET r.isHost = ?2 WHERE r.user = ?1")
    void updateIsHostByUser(ApplicationUser user, boolean value);

    @Transactional
    @Modifying
    @Query("UPDATE RoomUser r SET r.score = r.score + ?2 WHERE r.user = ?1")
    void addScoreByUser(ApplicationUser user, int scoreToAdd);


}
