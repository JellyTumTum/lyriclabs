package com.project.app.repository.music;

import java.util.List;
import java.util.Optional;

import com.project.app.model.music.Artist;
import com.project.app.model.music.Song;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.app.model.music.SongArtist;

@Repository
public interface SongArtistRepository extends JpaRepository<SongArtist, Long>{
    List<SongArtist> findBySong_songId(String songId);

    List<SongArtist> findByArtist_artistId(String artistId);

    @Query("SELECT a FROM SongArtist s JOIN s.artist a WHERE s.song = :song")
    List<Artist> findArtistsBySong(Song song);

    @Transactional
    @Query("SELECT s.song FROM SongArtist s WHERE s.artist = :artist AND s.song.lyricID > 0")
    List<Song> findSongsByArtistWithLyrics(Artist artist);

    boolean existsBySongAndArtist(Song song, Artist artist);

    @Transactional
    Optional<SongArtist> findBySongAndArtist(Song song, Artist artist);


    // use of findBy"tableName"_"Pkey" is transversing the relationship, so it means "find by the artist/songId of the associated artist/song entity.

}