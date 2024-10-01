package com.project.app.service;

import com.project.app.model.ApplicationUser;
import com.project.app.model.UserStats;
import com.project.app.repository.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserStatsService {

    @Autowired
    public UserStatsRepository userStatsRepository;

    public Optional<UserStats> getByUserID(Long userID) {
        return userStatsRepository.findByUserId(userID);
    }

    public UserStats createUserStats(ApplicationUser user, Integer gamesPlayed, Integer gamesWon,
                                     String favouriteArtist, Integer favouriteArtistWins, Integer favouriteArtistOccurances) {
        UserStats userStats = new UserStats(user, gamesPlayed, gamesWon, favouriteArtist, favouriteArtistWins, favouriteArtistOccurances);
        return userStatsRepository.save(userStats);
    }

}
