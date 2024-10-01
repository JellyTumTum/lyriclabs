package com.project.app.controller;


import com.project.app.api.Graphs.Results.ResultsResponse;
import com.project.app.repository.UserRepository;
import com.project.app.repository.music.ArtistRepository;
import com.project.app.repository.rooms.RoomArchiveRepository;
import com.project.app.service.AnalysisService;
import com.project.app.service.StatsService;
import com.project.app.service.UserService;
import com.project.app.service.music.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.project.app.service.AnalysisService.CUT_OFF_TIME_MS;
import static com.project.app.service.AnalysisService.lastRefreshTimeMS;

@RestController
@RequestMapping("/analysis")
public class ResultsController {

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
    public MusicService musicService;

    @Autowired
    public AnalysisService analysisService;

    @GetMapping("/load")
    public ResultsResponse loadResultsPageDetails(@RequestParam(required = false) Boolean forcedLoad, @AuthenticationPrincipal Jwt jwt) {

        String username = null;
        if (jwt != null) {
            username = jwt.getClaimAsString("sub");
        }
        if (username == null) {
            forcedLoad = false;
        }
        long currentTimeMS = System.currentTimeMillis();
        analysisService.fetchData(forcedLoad);
        System.out.println("\n ResultsController|LoadResultsPageDetails: \nlastRefreshTimeMS = " + lastRefreshTimeMS + "\ncurrentTimeMS = " + currentTimeMS + "\n CUT_OFF_TIME_MS = " + CUT_OFF_TIME_MS + "\n forcedLoad = " + forcedLoad + "\n");
        if (lastRefreshTimeMS == 0L || currentTimeMS - lastRefreshTimeMS > CUT_OFF_TIME_MS || forcedLoad) {
            System.out.println("\n ResultsController|LoadResultsPageDetails: if hit, returning loading signal\n");
            return new ResultsResponse("[LOADING] Data is being analysed, reattempting in 5 seconds");
        }
        else {
            System.out.println("\n ResultsController|LoadResultsPageDetails: else hit, returning 'cached' data \n");
            return AnalysisService.currentResultsData;
        }

    }


}
