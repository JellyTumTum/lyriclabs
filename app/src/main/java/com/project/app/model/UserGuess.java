package com.project.app.model;


import com.project.app.api.rooms.FrontendGuess;
import com.project.app.model.music.Song;
import jakarta.persistence.*;

@Entity
@Table(name="user_guess")
public class UserGuess {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="guess_id")
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


    public UserGuess(FrontendGuess answerGuess, Song song, boolean isCorrect, ApplicationUser user) {
        this.roomID = answerGuess.getRoomID();
        this.guessCount = answerGuess.getGuessNumber();
        this.song = song;
        this.recentGuessTime = answerGuess.getGuessTimeMS();
        this.totalGuessTime = answerGuess.getTotalGuessTimeMS();
        this.correctGuess = isCorrect;
        this.user = user;
        this.roundNumber = answerGuess.getRoundNumber();
    }

    public UserGuess(PracticeGuess practiceGuess) {
        // ** DEPRECATED **
        // casting practiceGuess to userGuess, so it can be returned out a function.
        this.roomID = practiceGuess.getRoomID();
        this.guessCount = practiceGuess.getGuessCount();
        this.song = practiceGuess.getSong();
        this.recentGuessTime = practiceGuess.getRecentGuessTime();
        this.totalGuessTime = practiceGuess.getTotalGuessTime();
        this.correctGuess = practiceGuess.isCorrectGuess();

    }

    public UserGuess() {

    }

    public Integer getGuessID() {
        return guessID;
    }

    public void setGuessID(Integer guessID) {
        this.guessID = guessID;
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

    public Integer getAssociatedScore() {
        return associatedScore;
    }

    public void setAssociatedScore(Integer associatedScore) {
        this.associatedScore = associatedScore;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    @Override
    public String toString() {
        return "UserGuess{\n" +
                "\nguessID=" + guessID +
                "\n, guessCount=" + guessCount +
                "\n, roomID='" + roomID + '\'' +
                "\n, song=" + song +
                "\n, correctGuess=" + correctGuess +
                "\n, recentGuessTime=" + recentGuessTime +
                "\n, totalGuessTime=" + totalGuessTime +
                "\n, associatedScore=" + associatedScore +
                '}';
    }
}
