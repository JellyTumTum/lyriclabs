package com.project.app.repository.music;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.app.model.music.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, String>{
    Optional<Album> findByAlbumId(String SongId);
}