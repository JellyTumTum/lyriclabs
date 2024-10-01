package com.project.app.api.Graphs;

import com.project.app.model.ApplicationUser;
import com.project.app.model.PracticeGuess;
import com.project.app.model.UserGuess;
import com.project.app.model.music.Song;
import jakarta.persistence.*;

@Entity
@Table(name = "generic_guess")
public class GenericGuess {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "guess_id")
    private Integer guessID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    private Integer guessCount;
    private String roomID;

    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;

    private boolean correctGuess;
    private long recentGuessTime; // time from last guess to this one
    private long totalGuessTime; // total guess time for this guess.
    private int roundNumber;

    @Transient
    private Integer associatedScore;

    public GenericGuess() {
    }

    // Constructor for UserGuess
    public GenericGuess(UserGuess userGuess) {
        this.guessID = userGuess.getGuessID();
        this.user = userGuess.getUser();
        this.guessCount = userGuess.getGuessCount();
        this.roomID = userGuess.getRoomID();
        this.song = userGuess.getSong();
        this.correctGuess = userGuess.isCorrectGuess();
        this.recentGuessTime = userGuess.getRecentGuessTime();
        this.totalGuessTime = userGuess.getTotalGuessTime();
        this.roundNumber = userGuess.getRoundNumber();
        this.associatedScore = userGuess.getAssociatedScore();
    }

    // Constructor for PracticeGuess
    public GenericGuess(PracticeGuess practiceGuess) {
        this.guessID = practiceGuess.getGuessID();
        this.user = practiceGuess.getUser();
        this.guessCount = practiceGuess.getGuessCount();
        this.roomID = practiceGuess.getRoomID();
        this.song = practiceGuess.getSong();
        this.correctGuess = practiceGuess.isCorrectGuess();
        this.recentGuessTime = practiceGuess.getRecentGuessTime();
        this.totalGuessTime = practiceGuess.getTotalGuessTime();
        this.roundNumber = practiceGuess.getRoundNumber();
        this.associatedScore = practiceGuess.getAssociatedScore();
    }

    public Integer getGuessID() {
        return guessID;
    }

    public void setGuessID(Integer guessID) {
        this.guessID = guessID;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public Integer getGuessCount() {
        return guessCount;
    }

    public void setGuessCount(Integer guessCount) {
        this.guessCount = guessCount;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public boolean isCorrectGuess() {
        return correctGuess;
    }

    public void setCorrectGuess(boolean correctGuess) {
        this.correctGuess = correctGuess;
    }

    public long getRecentGuessTime() {
        return recentGuessTime;
    }

    public void setRecentGuessTime(long recentGuessTime) {
        this.recentGuessTime = recentGuessTime;
    }

    public long getTotalGuessTime() {
        return totalGuessTime;
    }

    public void setTotalGuessTime(long totalGuessTime) {
        this.totalGuessTime = totalGuessTime;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public Integer getAssociatedScore() {
        return associatedScore;
    }

    public void setAssociatedScore(Integer associatedScore) {
        this.associatedScore = associatedScore;
    }


//    @Override
//    public String toString() {
//        return "GenericGuess{" +
//                "guessID=" + guessID +
//                ", user=" + user +
//                ", guessCount=" + guessCount +
//                ", roomID='" + roomID +
//                ", correctGuess=" + correctGuess +
//                ", recentGuessTime=" + recentGuessTime +
//                ", totalGuessTime=" + totalGuessTime +
//                ", roundNumber=" + roundNumber +
//                ", associatedScore=" + associatedScore +
//                "}\n";
//    }

    @Override
    public String toString() {
        return guessID.toString();
    }
}
