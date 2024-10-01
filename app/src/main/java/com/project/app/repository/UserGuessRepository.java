package com.project.app.repository;

import java.util.List;
import java.util.Optional;

import com.project.app.model.ApplicationUser;
import com.project.app.model.UserGuess;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.app.model.UserStats;

@Repository
public interface UserGuessRepository extends JpaRepository<UserGuess, Integer>{

    @Transactional
    Optional<UserGuess> findUserGuessByGuessID(Integer guessID);

    @Transactional
    List<UserGuess> findUserGuessByRoomID(String roomID);

    @Transactional
    List<UserGuess> findUserGuessByUser(ApplicationUser user);


}