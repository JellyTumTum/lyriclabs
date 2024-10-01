package com.project.app.repository;

import com.project.app.model.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;


@Repository
public interface TokensRepository extends JpaRepository<Token, Long> {


    Optional<Token> findByUserId(long userID);

    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.token = :token")
    int deleteByToken(String token);

    @Modifying
    @Transactional
    int deleteByUserId(Long userId);

    @Modifying
    @Query(value = "INSERT INTO tokens (user_id, token, expiry_time) VALUES (:userId, :tokenValue, :expiryTime)", nativeQuery = true)
    void insertAll(@Param("userId") Long userId, @Param("tokenValue") String tokenValue, @Param("expiryTime") Instant expiryTime);


}
