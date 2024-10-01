package com.project.app.controller;


import com.project.app.api.GameStats.GameStatsData;
import com.project.app.api.GameStats.UserSpecificGraphData;
import com.project.app.api.Graphs.ArtistSpecificGraphs;
import com.project.app.api.Graphs.GameAvgResponseBar;
import com.project.app.api.GameStats.GameChartResponse;
import com.project.app.api.GameStats.GameStatsInitialResponse;
import com.project.app.model.ApplicationUser;
import com.project.app.model.music.Artist;
import com.project.app.model.rooms.RoomArchive;
import com.project.app.repository.UserRepository;
import com.project.app.repository.music.ArtistRepository;
import com.project.app.repository.rooms.RoomArchiveRepository;
import com.project.app.service.PracticeService;
import com.project.app.service.StatsService;
import com.project.app.service.UserService;
import com.project.app.service.music.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/gameStats")
public class StatsController {

    @Autowired
    public StatsService statsService;

    @Autowired
    public RoomArchiveRepository roomArchiveRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public UserService userService;

    @Autowired
    public ArtistRepository artistRepository;

    @Autowired
    public PracticeService practiceService;

    @Autowired
    public MusicService musicService;

    @GetMapping("/load")
    public GameStatsInitialResponse loadPageDetails(@RequestParam String roomID, @AuthenticationPrincipal Jwt jwt) {

        System.out.println("\n StatsController | loadPageDetails: roomID = " + roomID + "\n");
        String username;
        if (jwt != null) {
            username = jwt.getClaimAsString("sub"); // "sub" is a standard claim representing the subject (typically the user)
        } else {
            return new GameStatsInitialResponse("[REFRESH] JWT may have expired, refresh the page so they can use their newly obtained one.");
        }


        GameStatsData gameStatsData = StatsService.gameStatsCache.getIfPresent(roomID);
        ApplicationUser currentUser = null;
        Optional<ApplicationUser> optCurrentUser = userRepository.findByUsername(username);
        System.out.println("\n StatsController | loadPageDetails: optCurrentUser.isPresent = " + optCurrentUser.isPresent() + "\n");

        if (gameStatsData == null) {
            Optional<RoomArchive> optionalRoomArchive = roomArchiveRepository.findRoomArchiveByRoomId(roomID);
            if (!optionalRoomArchive.isPresent()) {
                // not a valid room --> probably doesn't exist, send them to the home page.
                return new GameStatsInitialResponse("[INVALID_ROOM_ERROR] Error: Room not Found.", "Invalid Room|The room you are trying to view stats for doesnt exist");
            }
            RoomArchive ra = optionalRoomArchive.get();
            List<Integer> userIdList = List.of(ra.getUserIDList().split(",")).stream().map(Integer::parseInt).toList();
            List<String> artistIdList = List.of(ra.getArtistIDList().split(","));
            System.out.println("\n StatsController | loadPageDetails: userIdList = " + userIdList);
            System.out.println("\n StatsController | loadPageDetails: artistIdList = " + artistIdList + "\n");
            List<ApplicationUser> userList = new ArrayList<>();
            List<Artist> artistList = new ArrayList<>();
            for (Integer userID : userIdList) {
                Optional<ApplicationUser> optUser = userRepository.findByUserId(userID);
                if (optUser.isPresent()) {
                    userList.add(optUser.get());
                    System.out.println("\n StatsController | loadPageDetails: optUser = " + optUser.get().getUsername() + "\n");
                    System.out.println("\n StatsController | loadPageDetails: currentUser = " + optCurrentUser.get().getUsername() + "\n");
                    if (optCurrentUser.isPresent()) {
                        if (Objects.equals(optUser.get(), optCurrentUser.get())) {
                            currentUser = optUser.get();
                            System.out.printf("StatsController | loadPageDetails: currentUser = " + currentUser.getUsername() + "\n");
                        }
                    }

                }

            }
            for (String artistID : artistIdList) {
                Optional<Artist> optArtist = artistRepository.findByArtistId(artistID);
                optArtist.ifPresent(artistList::add);
            }
            System.out.println("\n StatsController | loadPageDetails: userList = " + userList);
            System.out.println("\n StatsController | loadPageDetails: artistList = " + artistList + "\n");
            gameStatsData = new GameStatsData(ra, userList, artistList);
            StatsService.gameStatsCache.put(roomID, gameStatsData);
        } else {
            // need to calculate if user was in lobby or not.  as its normally done above but this else means they were never in the if statement above obvs.
            if (optCurrentUser.isPresent()) {
                if (gameStatsData.getUserList().contains(optCurrentUser.get())) {
                    currentUser = optCurrentUser.get();
                    System.out.printf("\n StatsController | loadPageDetails: currentUser has been set (inside else clause)\n");
                }
            }
        }
        return new GameStatsInitialResponse("[LOADED_INIT] Load information obtained",
                gameStatsData.getUserList(),
                gameStatsData.getRoomArchive().getRoomName(),
                musicService.convertToFrontendArtists(gameStatsData.getArtistList()),
                currentUser, gameStatsData.getRoomArchive().isPractice());
    }

    @GetMapping("/loadGameGraphs")
    public GameChartResponse loadGameCharts(@RequestParam String roomID, @RequestParam Optional<String> frontendArtist,  @AuthenticationPrincipal Jwt jwt) {
        System.out.println("\n StatsController | loadPageDetails: roomID = " + roomID + "\n");
        String username;
        if (jwt != null) {
            username = jwt.getClaimAsString("sub");
        } else {
            return new GameChartResponse("[REFRESH] User not authenticated.");
        }
        if (StatsService.gameStatsCache.getIfPresent(roomID) == null) {
            loadPageDetails(roomID, jwt); // adds the required gameStatsData to the cache. return value can just be thrown here
        }

        GameChartResponse response = new GameChartResponse();
        Optional<RoomArchive> optRoomArchive = roomArchiveRepository.findRoomArchiveByRoomId(roomID);
        RoomArchive roomArchive;
        if (optRoomArchive.isPresent()) {
            roomArchive = optRoomArchive.get();
        } else {
            response.setResponseMessage("[UNEXPECTED_ERROR] Somehow room_archive isnt present");
            response.setNotification("Rare Error|Congratulations you found a case I know about but didn't think could happen. sorry about that one");
            return response;
        }
        response.setAvgResponseBarChartData(statsService.generateAverageResponseBars(roomArchive, frontendArtist));
        response.setCorrectGuessLineGraphData(statsService.generateCorrectGuessLineGraph(roomArchive));
        response.setCorrectGuessPieChartData(statsService.generatePieChart(roomArchive, frontendArtist));


        // TODO: rest of graphs.
        return response;

    }

    @GetMapping("/updateAvgResponseArtistGraph")
    // DEPRECATED (05/02/24) --> doing both at once see updateArtistSpecificGraphs
    public GameAvgResponseBar updateAvgResponseGraph(@RequestParam String roomID, @RequestParam Optional<String> frontendArtist, @AuthenticationPrincipal Jwt jwt) {
        Optional<RoomArchive> optRoomArchive = roomArchiveRepository.findRoomArchiveByRoomId(roomID);
        RoomArchive roomArchive;
        if (frontendArtist.isPresent()) {
            if (frontendArtist.get().equals("")) {
                frontendArtist = Optional.empty();
            }
        }
        if (optRoomArchive.isPresent()) {
            roomArchive = optRoomArchive.get();
            return statsService.generateAverageResponseBars(roomArchive, frontendArtist);
        } else {
            return null;
        }
    }

    @GetMapping("/updateArtistSpecificGraphs")
    public ArtistSpecificGraphs updateArtistSpecificGraphs(@RequestParam String roomID, @RequestParam Optional<String> frontendArtist, @AuthenticationPrincipal Jwt jwt) {
        Optional<RoomArchive> optRoomArchive = roomArchiveRepository.findRoomArchiveByRoomId(roomID);
        RoomArchive roomArchive;
        if (frontendArtist.isPresent()) {
            if (frontendArtist.get().equals("")) {
                frontendArtist = Optional.empty();
            }
        }
        if (optRoomArchive.isPresent()) {
            roomArchive = optRoomArchive.get();
            return new ArtistSpecificGraphs(statsService.generatePieChart(roomArchive, frontendArtist), statsService.generateAverageResponseBars(roomArchive, frontendArtist));
        } else {
            return null;
        }
    }

    @GetMapping("/getUserSpecificGraphs")
    public UserSpecificGraphData getUserSpecificGraphData(@RequestParam String roomID, @RequestParam Optional<String> frontendArtist, @RequestParam Optional<String> chosenUserID, @AuthenticationPrincipal Jwt jwt) {

        System.out.println("\n StatsController | getUserSpecificGraphData : inside function  userID -> " + chosenUserID.get() + " frontendArtist.isPresent -> " + frontendArtist.isPresent() + "\n");
        String username;
        if (jwt != null) {
            username = jwt.getClaimAsString("sub"); // "sub" is a standard claim representing the subject (typically the user)
        } else {
            return new UserSpecificGraphData("[NO_AUTH_ERROR] User not authenticated.");
        }
        if (StatsService.gameStatsCache.getIfPresent(roomID) == null) {
            loadPageDetails(roomID, jwt); // adds the required gameStatsData to the cache. return value can just be thrown here
        }

        UserSpecificGraphData response = new UserSpecificGraphData();
        Optional<RoomArchive> optRoomArchive = roomArchiveRepository.findRoomArchiveByRoomId(roomID);
        RoomArchive roomArchive;
        if (optRoomArchive.isPresent()) {
            roomArchive = optRoomArchive.get();
        } else {
            roomArchive = null;
        }

        if (!chosenUserID.isPresent()) {
            response.setResponseMessage("[NO_USER_GIVEN] no userID was passed in");
            return response;
        }
        Optional<ApplicationUser> optUser = userRepository.findByUserId(Integer.valueOf(chosenUserID.get()));
        ApplicationUser user;
        if (optUser.isPresent()) {
            user = optUser.get();
        } else {
            return new UserSpecificGraphData("[NO_USER_ERROR] User does not exist");
        }
        boolean roomSpecific = roomArchive != null;
        if (roomArchive.isPractice()) {
            roomSpecific = false;
        }
        response.setGuessTimeHistogram(statsService.generateHistogram(roomArchive, user, frontendArtist, roomSpecific));
        response.setUserArtistBarChart(statsService.generateUserArtistBarChart(roomArchive, user, roomSpecific));
        response.setArtistGuessStats(statsService.generateArtistGuessStats(roomArchive, user));
        System.out.println("\n StatsController | getUserSpecificGraphData : histogram data added " + response.getGuessTimeHistogram() + "\n");
        return response;

    }

    @GetMapping("/prunePracticeData")
    public UserSpecificGraphData prunePracticeData(@RequestParam String roomID, @RequestParam Optional<String> frontendArtist, @RequestParam Optional<String> chosenUserID, @AuthenticationPrincipal Jwt jwt) {

        //
        String username;
        if (jwt != null) {
            username = jwt.getClaimAsString("sub"); // "sub" is a standard claim representing the subject (typically the user)
        } else {
            return new UserSpecificGraphData("[NO_AUTH_ERROR] User not authenticated.");
        }
        if (StatsService.gameStatsCache.getIfPresent(roomID) == null) {
            loadPageDetails(roomID, jwt); // adds the required gameStatsData to the cache. return value can just be thrown here
        }

        Optional<ApplicationUser> optUser = userRepository.findByUsername(username);
        if (optUser.isPresent()) {
            ApplicationUser user = optUser.get();
            practiceService.userSpecificPruning(user);
        }
        else {
            return new UserSpecificGraphData("[NO_USER_ERROR] User does not exist???");
        }

        return getUserSpecificGraphData(roomID, frontendArtist, chosenUserID, jwt);


    }

}
