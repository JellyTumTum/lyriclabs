package com.project.app.repository.rooms;

import java.util.Optional;

import com.project.app.model.ApplicationUser;
import com.project.app.model.rooms.RoomConfig;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.app.model.rooms.Room;

@Repository
@Transactional
public interface RoomRepository extends JpaRepository<Room, String>{
    Optional<Room> findByRoomId(String RoomId);

    Optional<Room> findByHostId(Integer HostId);

    @Modifying
    @Query("UPDATE Room r SET r.hostId = ?1, r.roomName = ?2 WHERE r.roomId = ?3")
    void updateRoomInfo(Integer newHostId, String newLobbyName, String roomId);

    @Modifying
    @Query("UPDATE Room r SET r.gameState = ?1 WHERE r.roomId = ?2")
    void updateGameState(String newGameState, String roomId);

    void deleteByRoomId(String roomID);
}
