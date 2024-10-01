package com.project.app.controller;


import com.project.app.api.StatsRequest;
import com.project.app.api.StatsResponse;
import com.project.app.model.ApplicationUser;
import com.project.app.model.UserStats;
import com.project.app.service.UserService;
import com.project.app.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/deprecatedStats")
public class UserStatsController {

    @Autowired
    UserService userService;

    @Autowired
    UserStatsService userStatsService;

    @PostMapping("/getstats")
    public ResponseEntity<StatsResponse> getStats(@RequestBody StatsRequest statsRequest) {

        // StatRequest = {username : ""}
        System.out.printf("Inside getStats");
        Optional<ApplicationUser> potentialUser = userService.getUserByUsername(statsRequest.username);
        ApplicationUser user;
        StatsResponse response;
        Optional<UserStats> potentialStats;
        UserStats stats;
        if (potentialUser.isPresent()) {
            user = potentialUser.get();
            potentialStats = userStatsService.getByUserID(Integer.toUnsignedLong(user.getUserId()));
            if (potentialStats.isPresent()) {
                stats = potentialStats.get();
                response = new StatsResponse(user.getUsername(), stats.getGamesPlayed(), stats.getGamesWon(), stats.getFavouriteArtist(), stats.getFavouriteArtistWins(), stats.getFavouriteArtistOccurances(), "Stats Recieved");
            } else {
                response = new StatsResponse("Error: No Data found for " + statsRequest.username + "--> Not really sure why this shouldnt happen");
            }

        } else {
            response = new StatsResponse("Error: No User found for " + statsRequest.username);
        }


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON) // Set content type header
                .body(response);
    }



    // TODO :
    // - Deal with getStats on the frontend and setup profile routing and stuff
}
