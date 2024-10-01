package com.project.app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.app.model.ApplicationUser;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {

    @Query("SELECT u FROM ApplicationUser u WHERE LOWER(u.username) = LOWER(:username)")
    Optional<ApplicationUser> findByUsername(@Param("username") String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<ApplicationUser> findByEmail(String email);

    Optional<ApplicationUser> findByUserId(Integer userID);

}