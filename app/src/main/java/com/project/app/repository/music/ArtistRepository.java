package com.project.app.repository.music;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.app.model.music.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, String>{

    Optional<Artist> findByArtistId(String ArtistId);

}