package com.project.app.repository.rooms;

import com.project.app.model.rooms.Room;
import com.project.app.model.rooms.RoomArchive;
import com.project.app.model.rooms.RoomArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomArchiveRepository extends JpaRepository<RoomArchive, String> {

    Optional<RoomArchive> findRoomArchiveByRoomId(String RoomId);




}
