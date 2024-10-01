package com.project.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.app.model.UserStats;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Integer>{

    Optional<UserStats> findByUserId(Long userID);


}
