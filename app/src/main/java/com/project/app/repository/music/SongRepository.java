package com.project.app.repository.music;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.app.model.music.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, String>{

    @Transactional
    Optional<Song> findBySongId(String SongId);

    @Transactional
    @Query("SELECT s FROM Song s WHERE s.lastAccessed < :thresholdTime")
    List<Song> findSongsWithExpiredLyrics(LocalDateTime thresholdTime);

    @Transactional
    @Query("SELECT s FROM Song s WHERE s.lyricID = :lyricID")
    List<Song> findSongByLyricID(Integer lyricID);


    @Transactional
    @Modifying
    @Query("UPDATE Song s SET s.lastAccessed = :now WHERE s.lyricID = :lyricID")
    void updateLastAccessedTimeByLyricID(@Param("lyricID") Integer lyricID, @Param("now") LocalDateTime now);
}