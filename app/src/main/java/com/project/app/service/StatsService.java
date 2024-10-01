package com.project.app.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.project.app.api.GameStats.GameStatsData;
import com.project.app.api.GameStats.RoundGuessStats;
import com.project.app.api.Graphs.*;
import com.project.app.api.rooms.FrontendArtist;
import com.project.app.model.ApplicationUser;
import com.project.app.model.PracticeGuess;
import com.project.app.model.UserGuess;
import com.project.app.model.music.Artist;
import com.project.app.model.music.SongArtist;
import com.project.app.model.rooms.Room;
import com.project.app.model.rooms.RoomArchive;
import com.project.app.repository.PracticeGuessRepository;
import com.project.app.repository.UserGuessRepository;
import com.project.app.service.music.MusicService;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Line;
import javax.xml.crypto.Data;
import java.sql.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class StatsService {

    @Autowired
    private PracticeGuessRepository practiceGuessRepository;

    @Autowired
    private UserGuessRepository userGuessRepository;

    @Autowired
    private MusicService musicService;

    public static final List<String> barColors = Arrays.asList(
            "#6DD47E",
            "#4CB3D4",
            "#9F9FEF",
            "#50D2C2",
            "#9DD3A8",
            "#73A5C6",
            "#C6B4CE",
            "#7AC5CD",
            "#8DA9C4",
            "#B5EAD7"
    );

    public static final List<String> outlineColors = Arrays.asList(
            "#59a66e",
            "#3991b0",
            "#8d8dd0",
            "#41a79e",
            "#87b78e",
            "#6287a4",
            "#a597b0",
            "#5e9da6",
            "#768a9b",
            "#9bc6b0"
    );




    public static Cache<String, GameStatsData> gameStatsCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();
    // expireTime is from the .put(), any changes made inside functions would affect the data but not change the expiryTime(), luckily most accesses to the stats are in 30 minutes after the lobby so.


    public List<GenericGuess> getGenericGuessList(RoomArchive roomArchive) {
        if (roomArchive.isPractice()) {
            List<PracticeGuess> practiceGuessList = practiceGuessRepository.findPracticeGuessByRoomID(roomArchive.getRoomId());
            return practiceGuessList.stream()
                    .map(GenericGuess::new)
                    .toList();
        } else {
            List<UserGuess> userGuessList = userGuessRepository.findUserGuessByRoomID(roomArchive.getRoomId());
            return userGuessList.stream()
                    .map(GenericGuess::new)
                    .toList();
        }
    }

    public Map<ApplicationUser, List<GenericGuess>> getUserGuessMap(List<GenericGuess> guessList) {
        return guessList.stream().collect(Collectors.groupingBy(GenericGuess::getUser));
    }

    public List<ApplicationUser> getSortedUserList(List<GenericGuess> guessList) {
        Set<ApplicationUser> userList = guessList.stream()
                .map(GenericGuess::getUser)
                .collect(Collectors.toSet());
        return userList.stream()
                .sorted(Comparator.comparing(ApplicationUser::getUserId))
                .collect(Collectors.toList());
    }

    public GameAvgResponseBar generateAverageResponseBars(RoomArchive roomArchive) {

        /*
         * 1. Fetch Guesses for the given room.
         * 2. separate into lists by user
         * 3. calculate averages for each user.
         * 4. place into GameAvgResponseBar object.
         * return BOOM.
         * */

        // ** DEPRECATED ** --> better version that includes more parameters is now used.

        if (StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getGameAvgResponseBar() != null) {
            return StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getGameAvgResponseBar();
        }
        List<GenericGuess> guessList;
        if (StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getGenericGuesses() != null) {
            guessList = StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getGenericGuesses();
        } else {
            guessList = getGenericGuessList(roomArchive);
        }

        List<ApplicationUser> userList = getSortedUserList(guessList);

        List<String> usernameLabels = userList.stream().map(ApplicationUser::getUsername).toList();
        System.out.println("\n usernameLabels = " + usernameLabels + "\n");
        Map<ApplicationUser, List<GenericGuess>> userGuessMap = getUserGuessMap(guessList);
        System.out.println("userGuessMap = " + userGuessMap + "\n");
        List<Float> averageCorrectGuesses = new ArrayList<>(); // only used in game instances, not in practice.
        List<Float> averageGuesses = new ArrayList<>();
        for (ApplicationUser user : userList) {
            float totalCorrect = 0f;
            float total = 0f;
            float correctGuesses = 0; // only in float to make division easier
            List<GenericGuess> userGuesses = userGuessMap.get(user);
            // Line above should mean that all lists are indexed properly in relation to userList.
            for (GenericGuess guess : userGuesses) {
                if (guess.isCorrectGuess()) {
                    totalCorrect += guess.getTotalGuessTime();
                    correctGuesses++;
                }
                total += guess.getRecentGuessTime();
            }
            if (total != 0) {
                total /= userGuesses.size();
                total /= 1000;
            }
            averageGuesses.add(total);
            if (correctGuesses > 0 && totalCorrect > 0) { // wouldn't want to divide by zero.
                totalCorrect /= correctGuesses;
                totalCorrect /= 1000;
            }
            averageCorrectGuesses.add(totalCorrect);

        }
        if (roomArchive.isPractice()) {
            GameAvgResponseBar.DataSet data = new GameAvgResponseBar.DataSet("Average Guess Time", averageGuesses, StatsService.barColors, StatsService.outlineColors, 1);
            return new GameAvgResponseBar(usernameLabels, List.of(data));
        } else {
            System.out.println("\naverageGuesses: " + averageGuesses + "\naverageCorrectGuesses: " + averageCorrectGuesses);
            GameAvgResponseBar.DataSet data = new GameAvgResponseBar.DataSet("Average Guess Time", averageGuesses, StatsService.barColors.subList(0, averageGuesses.size()), StatsService.outlineColors.subList(0, averageGuesses.size()), 1);
            GameAvgResponseBar.DataSet data2 = new GameAvgResponseBar.DataSet("Average Correct Guess Time", averageCorrectGuesses, StatsService.barColors.subList(0, averageCorrectGuesses.size()), StatsService.outlineColors.subList(0, averageCorrectGuesses.size()), 1);
            return new GameAvgResponseBar(usernameLabels, List.of(data, data2));
        }

    }

    public CorrectGuessLineGraph generateCorrectGuessLineGraph(RoomArchive roomArchive) {

        /*
         * 1. Fetch genericGuesses from the cache.
         * 2. Create List for each player of their correct guess times / null for non-correct guesses.
         * 3. add to DataSet objects.
         * */

        if (StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getCorrectGuessLineGraph() != null) {
            return StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getCorrectGuessLineGraph();
        }
        List<GenericGuess> guessList;
        if (StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getGenericGuesses() != null) {
            guessList = StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getGenericGuesses();
        } else {
            guessList = getGenericGuessList(roomArchive);
        }

        Map<ApplicationUser, List<GenericGuess>> userGuessMap = getUserGuessMap(guessList);
        List<ApplicationUser> userList = getSortedUserList(guessList);

        List<CorrectGuessLineGraph.LineDataSet> dataSetList = new ArrayList<>();
        int loopIndex = 0;
        for (ApplicationUser user : userList) {
            List<GenericGuess> userGuesses = userGuessMap.get(user);
            List<Float> correctGuessTimes = new ArrayList<>();
            for (int roundNo = 1; roundNo <= roomArchive.getRoundCount(); roundNo++) {
                int finalRoundNo = roundNo; // needs to be final for lambda.
                Optional<GenericGuess> associatedCorrectGuess = userGuesses.stream()
                        .filter(guess -> guess.isCorrectGuess() && guess.getRoundNumber() == finalRoundNo)
                        .findFirst();
                if (associatedCorrectGuess.isPresent()) {
                    correctGuessTimes.add((float) associatedCorrectGuess.get().getTotalGuessTime() / 1000f);
                } else {
                    correctGuessTimes.add(null);
                }
            }
            dataSetList.add(new CorrectGuessLineGraph.LineDataSet(user.getUsername(), correctGuessTimes, StatsService.barColors.get(loopIndex++), 1));
        }

        List<String> labels = new ArrayList<>();
        for (int i = 0; i < roomArchive.getRoundCount(); i++) {
            labels.add(String.valueOf(i));

        }
        return new CorrectGuessLineGraph(labels, dataSetList);
    }

    @Transactional
    public GameAvgResponseBar generateAverageResponseBars(RoomArchive roomArchive, Optional<String> optionalChosenArtist) {
        if (optionalChosenArtist.isEmpty()) {
            if (StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).getGameAvgResponseBar() != null) {
                return StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).getGameAvgResponseBar();
            }
        } else {
            if (StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).getGameAvgResponseArtistBar().get(optionalChosenArtist.get()) != null) {
                return StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).getGameAvgResponseArtistBar().get(optionalChosenArtist.get());
            }
        }
        List<GenericGuess> guessList = StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getGenericGuesses() != null ?
                StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getGenericGuesses() :
                getGenericGuessList(roomArchive);

        Map<ApplicationUser, List<GenericGuess>> userGuessMap = getUserGuessMap(guessList);
        List<ApplicationUser> userList = getSortedUserList(guessList);
        List<Float> averageCorrectGuesses = new ArrayList<>();
        List<Float> averageGuesses = new ArrayList<>();
        List<String> usernameLabels = userList.stream().map(ApplicationUser::getUsername).collect(Collectors.toList());

        for (ApplicationUser user : userList) {
            float totalCorrect = 0f;
            float total = 0f;
            float correctGuesses = 0;
            List<GenericGuess> userGuesses = userGuessMap.get(user);

            for (GenericGuess guess : userGuesses) {
                boolean checkGuess = true;
                if (optionalChosenArtist.isPresent()) {
                    Set<SongArtist> songArtists = guess.getSong().getSongArtists();
                    checkGuess = songArtists.stream().anyMatch(songArtist -> songArtist.getArtist().getArtistId().equals(optionalChosenArtist.get()));
                    // checks guess by default, if an artist is picked, then potentially checkGuess can be false.
                }
                if (checkGuess) {
                    if (guess.isCorrectGuess()) {
                        totalCorrect += guess.getTotalGuessTime();
                        correctGuesses++;
                    }
                    total += guess.getRecentGuessTime();
                }
            }
            if (total != 0) {
                total /= userGuesses.size();
                total /= 1000;
            }
            averageGuesses.add(total);
            if (correctGuesses > 0 && totalCorrect > 0) { // wouldn't want to divide by zero.
                totalCorrect /= correctGuesses;
                totalCorrect /= 1000;
            }
            averageCorrectGuesses.add(totalCorrect);
        }
        GameAvgResponseBar graph;
        if (roomArchive.isPractice()) {
            GameAvgResponseBar.DataSet data = new GameAvgResponseBar.DataSet("Average Guess Time", averageGuesses, StatsService.barColors, StatsService.outlineColors, 1);
            graph = new GameAvgResponseBar(usernameLabels, List.of(data));
        } else {
            GameAvgResponseBar.DataSet data = new GameAvgResponseBar.DataSet("Average Guess Time", averageGuesses, StatsService.barColors.subList(0, averageGuesses.size()), StatsService.outlineColors.subList(0, averageGuesses.size()), 1);
            GameAvgResponseBar.DataSet data2 = new GameAvgResponseBar.DataSet("Average Correct Guess Time", averageCorrectGuesses, StatsService.barColors.subList(0, averageCorrectGuesses.size()), StatsService.outlineColors.subList(0, averageCorrectGuesses.size()), 1);
            graph = new GameAvgResponseBar(usernameLabels, List.of(data, data2));
        }
        if (optionalChosenArtist.isPresent()) {
            StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).getGameAvgResponseArtistBar().put(optionalChosenArtist.get(), graph);
        } else {
            StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).setGameAvgResponseBar(graph);
        }
        return graph;
    }


    @Transactional
    public SimplePieChart generatePieChart(RoomArchive roomArchive, Optional<String> chosenArtist) {
        // NOT SUITABLE FOR PRACTICE MODE
        /*
         * 1. split the guesses into correct guesses only.
         * 2. split into rounds.
         * 3. order by totalGuessTime, and take the shortest.
         * 4. add 1 to that users tally.
         * 5. create SimplePieChart DTO using the Map.
         * */
        if (chosenArtist.isPresent()) {
            if (StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).getPieChartMap().get(chosenArtist.get()) != null) {
                return StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).getPieChartMap().get(chosenArtist.get());
            }
        } else {
            if (StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).getPieChartMap().get("") != null) {
                return StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).getPieChartMap().get("");
            }
        }

        List<GenericGuess> guessList;
        if (StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getGenericGuesses() != null) {
            guessList = StatsService.gameStatsCache.asMap().get(roomArchive.getRoomId()).getGenericGuesses();
        } else {
            guessList = getGenericGuessList(roomArchive);
        }

        List<ApplicationUser> userList = getSortedUserList(guessList);
        Map<ApplicationUser, Integer> correctGuessCountMap = new HashMap<>();
        for (ApplicationUser user : userList) {
            correctGuessCountMap.put(user, 0);
        }
        List<List<GenericGuess>> roundGuesses = new ArrayList<>();
        for (int i = 0; i <= roomArchive.getRoundCount(); i++) {
            roundGuesses.add(new ArrayList<>());
        }
        for (GenericGuess guess : guessList) {
            boolean checkGuess = true;
            if (guess.isCorrectGuess()) {
                if (!chosenArtist.isPresent()) {
                    roundGuesses.get(guess.getRoundNumber() - 1).add(guess);
                } else {
                    Set<SongArtist> songArtists = guess.getSong().getSongArtists();
                    if (songArtists.stream().anyMatch(songArtist -> songArtist.getArtist().getArtistId().equals(chosenArtist.get()))) {
                        roundGuesses.get(guess.getRoundNumber() - 1).add(guess);
                    }
                }
            }
        }
        for (List<GenericGuess> crgl : roundGuesses) {
            crgl.sort(Comparator.comparingLong(GenericGuess::getTotalGuessTime));
            if (!crgl.isEmpty()) {
                ApplicationUser winner = crgl.get(0).getUser();
                correctGuessCountMap.put(winner, correctGuessCountMap.get(winner) + 1);
            }

        }
        List<Integer> data = new ArrayList<>();
        List<String> usernameLabels = new ArrayList<>();
        for (ApplicationUser user : userList) {
            data.add(correctGuessCountMap.get(user));
            usernameLabels.add(user.getUsername());
        }
        SimplePieChart.Dataset dataset = new SimplePieChart.Dataset(data, StatsService.barColors.subList(0, data.size()), StatsService.outlineColors.subList(0, data.size()), 1);
        return new SimplePieChart(usernameLabels, dataset);

    }

    public GameAvgResponseBar generateUserArtistBarChart(RoomArchive roomArchive, ApplicationUser user, boolean roomSpecific) {
        List<UserGuess> guessData = userGuessRepository.findUserGuessByUser(user);
        List<PracticeGuess> practiceData = practiceGuessRepository.findPracticeGuessByUser(user);
        List<Artist> artistList = StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).getArtistList();

        if (roomSpecific) {
            guessData = guessData.stream()
                    .filter(guess -> guess.getRoomID().equals(roomArchive.getRoomId()))
                    .collect(Collectors.toList());
            // Practice data cannot be specified by room. its kind of the whole point
        }


        List<String> artistIds = artistList.stream()
                .map(Artist::getArtistId)
                .toList();

        guessData = guessData.stream()
                .filter(guess -> guess.getSong().getSongArtists().stream()
                        .map(SongArtist::getArtist)
                        .anyMatch(artist -> artistIds.contains(artist.getArtistId())))
                .toList();

        practiceData = practiceData.stream()
                .filter(guess -> guess.getSong().getSongArtists().stream()
                        .map(SongArtist::getArtist)
                        .anyMatch(artist -> artistIds.contains(artist.getArtistId())))
                .toList();


        Map<String, List<UserGuess>> userGuessesByArtist = new HashMap<>();
        Map<String, List<PracticeGuess>> practiceGuessesByArtist = new HashMap<>();
        for (Artist artist : artistList) {
            userGuessesByArtist.put(artist.getArtistId(), new ArrayList<>());
            practiceGuessesByArtist.put(artist.getArtistId(), new ArrayList<>());
        }

        for (UserGuess guess : guessData) {
            for (SongArtist songArtist : guess.getSong().getSongArtists()) {
                String artistId = songArtist.getArtist().getArtistId();
                if (userGuessesByArtist.get(artistId) != null) {
                    userGuessesByArtist.get(artistId).add(guess);
                }

            }
        }

        for (PracticeGuess guess : practiceData) {
            for (SongArtist songArtist : guess.getSong().getSongArtists()) {
                String artistId = songArtist.getArtist().getArtistId();
                if (practiceGuessesByArtist.get(artistId) != null) {
                    practiceGuessesByArtist.get(artistId).add(guess);
                }
            }
        }

        Map<String, List<Long>> totalCorrectGuessTimesGame = new HashMap<>();
        Map<String, List<Long>> totalCorrectGuessTimesPractice = new HashMap<>();

        for (Artist artist : artistList) {
            String artistID = artist.getArtistId();
            totalCorrectGuessTimesGame.put(artistID, new ArrayList<>());
            totalCorrectGuessTimesPractice.put(artistID, new ArrayList<>());

            for (UserGuess ug : userGuessesByArtist.get(artistID)) {
                if (ug.isCorrectGuess()) {
                    totalCorrectGuessTimesGame.get(artistID).add(ug.getTotalGuessTime());
                }
            }
            for (PracticeGuess pg : practiceGuessesByArtist.get(artistID)) {
                if (pg.isCorrectGuess()) {
                    totalCorrectGuessTimesPractice.get(artistID).add(pg.getTotalGuessTime());
                }
            }
        }


        List<String> labels = artistList.stream()
                .map(Artist::getName)
                .collect(Collectors.toList());

        List<Float> avgGameTimes = new ArrayList<>();
        List<Float> avgPracticeTimes = new ArrayList<>();

        for (Artist artist : artistList) {
            String artistID = artist.getArtistId();

            List<Long> gameTimes = totalCorrectGuessTimesGame.get(artistID);
            float avgGameTime = gameTimes.isEmpty() ? 0 :
                    (float) gameTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
            avgGameTimes.add(avgGameTime / 1000);

            List<Long> practiceTimes = totalCorrectGuessTimesPractice.get(artistID);
            float avgPracticeTime = practiceTimes.isEmpty() ? 0 :
                    (float) practiceTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
            avgPracticeTimes.add(avgPracticeTime / 1000);
        }

        GameAvgResponseBar.DataSet gameDataSet = new GameAvgResponseBar.DataSet(
                "Game Data",
                avgGameTimes,
                StatsService.barColors.subList(0, labels.size()),
                StatsService.outlineColors.subList(0, labels.size()),
                1
        );

        GameAvgResponseBar.DataSet practiceDataSet = new GameAvgResponseBar.DataSet(
                "Practice Data",
                avgPracticeTimes,
                StatsService.barColors.subList(0, labels.size()),
                StatsService.outlineColors.subList(0, labels.size()),
                1
        );

        return new GameAvgResponseBar(labels, Arrays.asList(gameDataSet, practiceDataSet));

    }

    @Transactional
    public GuessTimeHistogram generateHistogram(RoomArchive roomArchive, ApplicationUser user, Optional<String> chosenArtist, boolean roomSpecific) {

        List<UserGuess> guessData = userGuessRepository.findUserGuessByUser(user);
        List<PracticeGuess> practiceData = practiceGuessRepository.findPracticeGuessByUser(user);

        if (chosenArtist.isPresent()) {
            if (chosenArtist.get().equals("")) {
                chosenArtist = Optional.empty();
            }
        }

        if (chosenArtist.isPresent()) {
            String artistId = chosenArtist.get();
            guessData = guessData.stream()
                    .filter(guess -> guess.getSong().getSongArtists().stream().map(SongArtist::getArtist).map(Artist::getArtistId).toList().contains(artistId))
                    .collect(Collectors.toList());
            practiceData = practiceData.stream()
                    .filter(guess -> guess.getSong().getSongArtists().stream().map(SongArtist::getArtist).map(Artist::getArtistId).toList().contains(artistId))
                    .collect(Collectors.toList());
        }

        if (roomSpecific) {
            guessData = guessData.stream()
                    .filter(guess -> guess.getRoomID().equals(roomArchive.getRoomId()))
                    .collect(Collectors.toList());
            // Practice data cannot be specified by room. its kind of the whole point
        }

        guessData.removeIf(guess -> !guess.isCorrectGuess()); // filters for just correct guesses, so its an accurate comparison to practiceData.

        List<Long> gameGuessTimes = guessData.stream().map(UserGuess::getTotalGuessTime).toList();
        List<Long> practiceGuessTimes = practiceData.stream().map(PracticeGuess::getTotalGuessTime).toList();

        long maxGame = gameGuessTimes.stream().max(Long::compare).orElse(0L);
        long maxPractice = practiceGuessTimes.stream().max(Long::compare).orElse(0L);
        long maxTime;
        if (maxGame > maxPractice) {
            maxTime = maxGame;
        } else {
            maxTime = maxPractice;
        }
        long minTime = 0;

        // round time up for binnage
        maxTime = ((maxTime + 999) / 1000) * 1000;

        int numberOfBins = 10;
        long binRange = (maxTime - minTime) / numberOfBins;


        if (binRange < 1000) {
            binRange = 1000;
            numberOfBins = (int) ((maxTime) / binRange); // could get complex, so just adjusting bin number
        }
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < numberOfBins; i++) {
            long startRange = minTime + (i * binRange);
            long endRange = startRange + binRange;
            labels.add(String.format("%d - %d", startRange / 1000, endRange / 1000));
        }

        GuessTimeHistogram.Dataset gameDataset = generateHistogramData(gameGuessTimes, "Game Data", numberOfBins, (int) binRange, StatsService.barColors.get(0));
        GuessTimeHistogram.Dataset practiceDataset = generateHistogramData(practiceGuessTimes, "Practice Data", numberOfBins, (int) binRange,  StatsService.barColors.get(1));

        return new GuessTimeHistogram(labels, List.of(gameDataset, practiceDataset));
    }

    public GuessTimeHistogram.Dataset generateHistogramData(List<Long> guessTimes, String label, int binCount, int rangePerBin, String backgroundColor) {

        List<Integer> data = new ArrayList<>(Collections.nCopies(binCount, 0));

        guessTimes.forEach(guessTime -> {
            int index = (int) (guessTime / rangePerBin);
            if (index >= binCount) {
                index = binCount - 1;
            }
            data.set(index, data.get(index) + 1);
        });

        GuessTimeHistogram.Dataset dataset = new GuessTimeHistogram.Dataset(label, data, backgroundColor);

        return dataset;
    }

    public List<RoundGuessStats> generateArtistGuessStats(RoomArchive roomArchive, ApplicationUser user) {
        // Assuming getGenericGuessList fetches guesses related to the given roomArchive
        List<GenericGuess> genericGuesses = getGenericGuessList(roomArchive);
        genericGuesses = genericGuesses.stream().filter(guess -> guess.getUser().equals(user)).toList();
        List<Artist> artistList = StatsService.gameStatsCache.getIfPresent(roomArchive.getRoomId()).getArtistList();

        Map<Artist, List<GenericGuess>> guessesByArtist = new HashMap<>();
        for (Artist artist : artistList) {
            guessesByArtist.put(artist, new ArrayList<>());
        }
        for (GenericGuess guess : genericGuesses) {
            for (SongArtist songArtist : guess.getSong().getSongArtists()) {
                Artist artist = songArtist.getArtist();
                if (guessesByArtist.get(artist) != null) {
                    guessesByArtist.get(artist).add(guess);
                }
            }
        }

        List<RoundGuessStats> roundGuessStatsList = new ArrayList<>();
        guessesByArtist.forEach((artist, guesses) -> {
            int totalRounds = (int) guesses.stream().filter(guess -> guess.getGuessCount() == 1).count();
            int correctGuesses = (int) guesses.stream().filter(GenericGuess::isCorrectGuess).count();
            double percentCorrect = (double) correctGuesses / totalRounds * 100;
            FrontendArtist frontendArtist = musicService.convertToFrontendArtists(List.of(artist)).get(0);

            RoundGuessStats stats = new RoundGuessStats(totalRounds, correctGuesses, percentCorrect, frontendArtist);
            roundGuessStatsList.add(stats);
        });

        return roundGuessStatsList;
    }
}

// TODO :

// Results page:
// Public access, but contains a rundown of all data gathered, using global graphs. (need to think of ideas)
// Figure out some + complete player specific graphs.
// User stats page (basically just personal graphs but uses all rooms. could present wierd issues with artist bias but is what it is).