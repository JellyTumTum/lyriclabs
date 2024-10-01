package com.project.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_stats")
public class UserStats {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer statsId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private ApplicationUser user;

    @Column
    private Integer gamesPlayed;

    @Column
    private Integer gamesWon;

    @Column
    private String favouriteArtist;

    @Column
    private Integer favouriteArtistWins;

    @Column
    private Integer favouriteArtistOccurances;


    public UserStats() {
        super();
    }

    public UserStats(ApplicationUser user, Integer gamesPlayed, Integer gamesWon,
                     String favouriteArtist, Integer favouriteArtistWins, Integer favouriteArtistOccurances) {
        this.user = user;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.favouriteArtist = favouriteArtist;
        this.favouriteArtistWins = favouriteArtistWins;
        this.favouriteArtistOccurances = favouriteArtistOccurances;
    }

    // Getter and setters

    public Integer getStatsId() {
        return statsId;
    }

    public void setStatsId(Integer statsId) {
        this.statsId = statsId;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public Integer getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(Integer gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public Integer getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(Integer gamesWon) {
        this.gamesWon = gamesWon;
    }

    public String getFavouriteArtist() {
        return favouriteArtist;
    }

    public void setFavouriteArtist(String favouriteArtist) {
        this.favouriteArtist = favouriteArtist;
    }

    public Integer getFavouriteArtistWins() {
        return favouriteArtistWins;
    }

    public void setFavouriteArtistWins(Integer favouriteArtistWins) {
        this.favouriteArtistWins = favouriteArtistWins;
    }

    public Integer getFavouriteArtistOccurances() {
        return favouriteArtistOccurances;
    }

    public void setFavouriteArtistOccurances(Integer favouriteArtistOccurances) {
        this.favouriteArtistOccurances = favouriteArtistOccurances;
    }
}
