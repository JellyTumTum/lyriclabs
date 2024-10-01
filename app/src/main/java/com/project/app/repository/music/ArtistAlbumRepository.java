package com.project.app.repository.music;

import java.util.List;
import java.util.Optional;

import com.project.app.model.music.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.app.model.music.ArtistAlbum;

@Repository
public interface ArtistAlbumRepository extends JpaRepository<ArtistAlbum, Integer>{
    Optional<ArtistAlbum> findByArtist_artistId(String ArtistId);

    Optional<ArtistAlbum> findByAlbum_albumId(String SongId);

    @Query("SELECT aa.album FROM ArtistAlbum aa WHERE aa.artist.artistId = :artistId")
    List<Album> findAlbumsByArtistId(@Param("artistId") String artistId);


}