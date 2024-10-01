package com.project.app.model.music;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "genre")
public class Genre {

    @Id
    private Integer genreId;

    private String genreName;
}
