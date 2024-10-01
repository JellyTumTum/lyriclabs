package com.project.app.repository;

import java.util.List;
import java.util.Optional;

import com.project.app.model.ApplicationUser;
import com.project.app.model.PracticeGuess;
import com.project.app.model.UserGuess;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeGuessRepository extends JpaRepository<PracticeGuess, Integer>{

    @Transactional
    Optional<UserGuess> findPracticeGuessByGuessID(Integer guessID);

    @Transactional
    List<PracticeGuess> findPracticeGuessByRoomID(String roomID);

    @Transactional
    List<PracticeGuess> findPracticeGuessByUser(ApplicationUser user);

    @Query("SELECT AVG(pg.totalGuessTime) FROM PracticeGuess pg WHERE pg.roomID = ?1")
    Double findAverageGuessTimeByRoomID(String roomID);

    @Query("SELECT AVG(pg.totalGuessTime) FROM PracticeGuess pg WHERE pg.user = ?1")
    Double findAverageGuessTimeByUser(ApplicationUser user);

    @Modifying
    @Transactional
    void deleteByTotalGuessTimeGreaterThanAndRoomID(Integer timeThreshold, String roomID);

    @Modifying
    @Transactional
    void deleteByTotalGuessTimeGreaterThanAndUser(Integer timeThreshold, ApplicationUser user);
}
