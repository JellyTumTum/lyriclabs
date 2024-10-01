package com.project.app.repository.music;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.app.model.music.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer>{
    Optional<Genre> findByGenreId(Integer genreId);

    Optional<Genre> findByGenreName(String genreName);
}