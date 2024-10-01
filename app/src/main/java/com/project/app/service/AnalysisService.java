package com.project.app.service;

import com.project.app.api.Graphs.Results.PercentGuessLineGraphData;
import com.project.app.api.Graphs.Results.ResultsResponse;
import com.project.app.api.Graphs.ScatterGraph;
import com.project.app.model.ApplicationUser;
import com.project.app.model.PracticeGuess;
import com.project.app.model.UserGuess;
import com.project.app.model.rooms.RoomArchive;
import com.project.app.repository.PracticeGuessRepository;
import com.project.app.repository.UserGuessRepository;
import com.project.app.repository.UserRepository;
import com.project.app.repository.rooms.RoomArchiveRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.AbstractMap.SimpleEntry;


@Service
public class AnalysisService {

    @Autowired
    private PracticeGuessRepository practiceGuessRepository;

    @Autowired
    private UserGuessRepository userGuessRepository;

    @Autowired
    private RoomArchiveRepository roomArchiveRepository;

    @Autowired
    private UserRepository userRepository;

    public static Map<Integer, String> usernames = new HashMap<>();

    public static List<RoomArchive> roomArchiveList;
    public static Map<String, RoomArchive> roomArchiveMap;

    public static List<UserGuess> userGuessList;

    public static List<PracticeGuess> practiceGuessList;

    public static Long lastRefreshTimeMS = 0L;
    public static long CUT_OFF_TIME_MS = TimeUnit.MINUTES.toMillis(30); // set to 30 minute atm, can be adjusted if needed.

    public static ResultsResponse currentResultsData;

    public static final List<String> heatmapColors = Arrays.asList(
            "#1DB954", // Green
            "#4AC16D",
            "#7EC286",
            "#AFC29E",
            "#DFC3B6",
            "#FFC2CC",
            "#FFA6A3",
            "#FF897A",
            "#FF6D52",
            "#FF0000"  // Red
    );

    @Async
    public synchronized void fetchData(boolean forcedLoad) { // --> should be ran at the start so all data used per load is up to date.
        long currentTimeMS = System.currentTimeMillis();
        roomArchiveMap = new HashMap<>();
        System.out.println("\n AnalysisService|fetchData : inside function \n");

        if (currentTimeMS - lastRefreshTimeMS > CUT_OFF_TIME_MS || forcedLoad) {
            usernames = userRepository.findAll().stream()
                    .collect(Collectors.toMap(
                            ApplicationUser::getUserId, // Key Mapper
                            ApplicationUser::getUsername));
            roomArchiveList = roomArchiveRepository.findAll();
            userGuessList = userGuessRepository.findAll();
            practiceGuessList = practiceGuessRepository.findAll();
            ResultsResponse newData = new ResultsResponse();
            for (RoomArchive roomArchive : roomArchiveList) {
                roomArchiveMap.put(roomArchive.getRoomId(), roomArchive);
            }
            newData.setPercentGuessLineGraphData(getPercentGuessLineGraphData());
            System.out.println("\nAnalysisService|fetchData: percentGuessLineGraph Added \n");
            newData.setRoomAverageResponseAccuracyScatterGraph(getScatterGraphForRooms(true));
            System.out.println("\nAnalysisService|fetchData: Scatter Graph 1 added \n");
            newData.setTimeRoomAverageResponseAccuracyScatterGraph(getScatterGraphForRooms(false)); // probably like really inefficient but oh well.
            System.out.println("\nAnalysisService|fetchData: Scatter Graph 2 added \n");
            newData.setMultiplayerPracticeTimeComparisonScatterGraph(getPracticeVsMultiplayerGraph());
            System.out.println("\nAnalysisService|fetchData: Scatter Graph 3 added \n");
            System.out.println("\nAnalysisService|fetchData: DONE. \n");
            newData.setResponseMessage("[SUCCESS]");
            currentResultsData = newData;
            lastRefreshTimeMS = currentTimeMS;
        }
    }

    public Integer fetchMaxGuessTimeForGuess(UserGuess userGuess) {
        RoomArchive matchingRoom = roomArchiveList.stream()
                .filter(room -> room.getRoomId().equals(userGuess.getRoomID()))
                .findFirst()
                .orElse(null);

        if (matchingRoom == null) {
            userGuessRepository.deleteById(userGuess.getGuessID()); // prevents them from sticking around if their room is for some reason no longer archived
            System.out.println("\n AnalysisService|fetchMaxGuessTimeForGuess:  guessID = " + userGuess.getGuessID() + " has been removed due to room " + userGuess.getRoomID() + "not being available anymore \n");
            return null;
        }

        return matchingRoom.getMaxGuessTime();
    }

    public PercentGuessLineGraphData getPercentGuessLineGraphData() {
        System.out.println("\n AnalysisService|getPercentGuessLineGraphData : inside function \n");
        Map<Integer, Double> averageCorrectGuessPercentageByTime = userGuessList.stream()
                .filter(UserGuess::isCorrectGuess)
                .filter(guess -> fetchMaxGuessTimeForGuess(guess) != null) // Filter out guesses with null maxGuessTime --> their roomArchive probably isnt available anymore
                .collect(Collectors.groupingBy(
                        guess -> fetchMaxGuessTimeForGuess(guess) * 1000,
                        Collectors.averagingDouble(guess -> ((double) guess.getTotalGuessTime() / (fetchMaxGuessTimeForGuess(guess) * 1000)) * 100)
                ));

        Map<Integer, Double> averageFirstGuessPercentageByTime = userGuessList.stream()
                .filter(guess -> guess.getGuessCount() == 1)
                .filter(guess -> fetchMaxGuessTimeForGuess(guess) != null) // Filter out guesses with null maxGuessTime --> their roomArchive probably isnt available anymore
                .collect(Collectors.groupingBy(
                        guess -> fetchMaxGuessTimeForGuess(guess) * 1000,
                        Collectors.averagingDouble(guess -> ((double) guess.getTotalGuessTime() / (fetchMaxGuessTimeForGuess(guess) * 1000)) * 100)
                ));

        System.out.println("\n avgC = " + averageCorrectGuessPercentageByTime);
        System.out.println("\n avgF = " + averageFirstGuessPercentageByTime + "\n");

        List<Integer> sortedLabelsInSeconds = new ArrayList<>(averageFirstGuessPercentageByTime.keySet())
                .stream()
                .distinct()
                .sorted()
                .map(time -> time / 1000) // Converting to second for labelling
                .toList();


        List<String> labels = sortedLabelsInSeconds.stream()
                .map(String::valueOf)
                .toList();


        List<Double> correctGuessDataPoints = sortedLabelsInSeconds.stream()
                .map(time -> averageCorrectGuessPercentageByTime.getOrDefault(time * 1000, 0.0))
                .toList();


        List<Double> firstGuessDataPoints = sortedLabelsInSeconds.stream()
                .map(time -> averageFirstGuessPercentageByTime.getOrDefault(time * 1000, 0.0))
                .toList();


        List<PercentGuessLineGraphData.Dataset> datasets = new ArrayList<>();
        datasets.add(new PercentGuessLineGraphData.Dataset(correctGuessDataPoints, StatsService.outlineColors.get(0), StatsService.barColors.get(0), "Correct Guesses"));
        datasets.add(new PercentGuessLineGraphData.Dataset(firstGuessDataPoints, StatsService.outlineColors.get(1), StatsService.barColors.get(1), "First Guesses"));

        return new PercentGuessLineGraphData(labels, datasets);
    }

    public Map<String, Map.Entry<Double, Double>> getAverageResponseTimeAndAccuracyForFirstGuesses() {
        return userGuessList.stream()
                .filter(guess -> guess.getGuessCount() == 1) // filter by just first guesses, as that's what would be effected most by responseTime.
                .collect(Collectors.groupingBy(UserGuess::getRoomID)) // Group by roomID so each room is respresented by a plot point in the graph.
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<UserGuess> firstGuesses = entry.getValue();
                            double avgResponseTime = firstGuesses.stream()
                                    .mapToLong(UserGuess::getTotalGuessTime)
                                    .average()
                                    .orElse(Double.NaN);
                            double accuracy = firstGuesses.stream()
                                    .filter(UserGuess::isCorrectGuess)
                                    .count() / (double) firstGuesses.size() * 100;
                            return new SimpleEntry<>(avgResponseTime, accuracy);
                        }
                ));
    }

    public ScatterGraph getScatterGraphForRooms(boolean playerCountHeatMap) {
        Map<String, Map.Entry<Double, Double>> roomAnalysis = getAverageResponseTimeAndAccuracyForFirstGuesses();

        List<ScatterGraph.ScatterDataSet> datasets = roomAnalysis.entrySet().stream()
                .filter(entry -> {
                    Double accuracy = entry.getValue().getValue();
                    return accuracy < 98; // Include only if accuracy is less than 98%
                })
                .map(entry -> {
                    String roomID = entry.getKey();
                    if (roomArchiveMap == null) {
                        for (RoomArchive roomArchive : roomArchiveList) {
                            roomArchiveMap.put(roomArchive.getRoomId(), roomArchive);
                        }
                    }
                    Double avgResponseTime = entry.getValue().getKey() / 1000; // converting to seconds, hopefully.
                    Double accuracy = entry.getValue().getValue();
                    ScatterGraph.ScatterDataSet dataSet = new ScatterGraph.ScatterDataSet();
                    dataSet.setLabel("Room " + roomID);
                    if (playerCountHeatMap) {
                        Integer playerCount = roomArchiveMap.get(roomID).getPlayerCount();
                        if (playerCount > 0) {
                            playerCount -=1; //. Added - 1 to prevent max player rooms from being out of range. (22/04/24)
                        }
                        dataSet.setBackgroundColor(heatmapColors.get(playerCount)); // lower playerCount = 'lower heat'.
                    } else {
                        Integer guessTime = roomArchiveMap.get(roomID).getMaxGuessTime();
                        dataSet.setBackgroundColor(heatmapColors.get((Math.round(15 - guessTime) * 2 / 3))); // higher guessTime = greener. * 2/3 to account for 2 colors. teechnically means guess times will share colors but oh well more than 10 gradations is not worth the effort.
                    }
                    ScatterGraph.ScatterDataSet.Point point = new ScatterGraph.ScatterDataSet.Point(avgResponseTime, accuracy, roomID);
                    dataSet.setData(List.of(point));
                    return dataSet;
                })
                .collect(Collectors.toList());

        // Generating labels (in this case, the room identifiers)
        List<String> labels = List.of("Click to View Stats");

        return new ScatterGraph(labels, datasets);
    }

    @Transactional
    public ScatterGraph getPracticeVsMultiplayerGraph() {
        // Fetch practice and multiplayer guess times for each user
        Map<Integer, Double> averagePracticeGuessTimeByUser = practiceGuessList.stream()
                .collect(Collectors.groupingBy(
                        guess -> guess.getUser().getUserId(),
                        Collectors.averagingDouble(guess -> guess.getTotalGuessTime() / 1000.0)
                ));

        Map<Integer, Double> averageMultiplayerGuessTimeByUser = userGuessList.stream()
                .filter(UserGuess::isCorrectGuess) // Consider only correct guesses
                .collect(Collectors.groupingBy(
                        guess -> guess.getUser().getUserId(),
                        Collectors.averagingDouble(guess -> guess.getTotalGuessTime() / 1000.0)
                ));

        Map<Integer, Double> averageFirstGuessTimeByUser = userGuessList.stream()
                .filter(guess -> guess.getGuessCount() == 1) // Consider only the first guess
                .collect(Collectors.groupingBy(
                        guess -> guess.getUser().getUserId(),
                        Collectors.averagingDouble(guess -> guess.getTotalGuessTime() / 1000.0)
                ));

        List<String> labels = new ArrayList<>();
        List<ScatterGraph.ScatterDataSet> datasets = new ArrayList<>();

        System.out.println("\nAnalysisService|getPracticeVsMultiplayerGraph: averageMultiplayerGuessTimeByUser:  " + averageMultiplayerGuessTimeByUser + "\n");
        System.out.println("\nAnalysisService|getPracticeVsMultiplayerGraph: averageFirstGuessTimeByUser:  " + averageFirstGuessTimeByUser + "\n");
        System.out.println("\nAnalysisService|getPracticeVsMultiplayerGraph: averagePracticeGuessTimeByUser:  " + averagePracticeGuessTimeByUser + "\n");

        List<ScatterGraph.ScatterDataSet.Point> correctGuessDataPoints = averageMultiplayerGuessTimeByUser.entrySet().stream()
                .filter(entry -> averagePracticeGuessTimeByUser.containsKey(entry.getKey())).map(entry -> {
                    Integer userID = entry.getKey();
                    String username = AnalysisService.usernames.get(userID);
                    Double multiplayerCorrectGuessTime = entry.getValue();
                    Double practiceGuessTime = averagePracticeGuessTimeByUser.get(userID);
                    if (practiceGuessTime != null && practiceGuessTime > 0) {
                        labels.add(username);
                        return new ScatterGraph.ScatterDataSet.Point(multiplayerCorrectGuessTime, practiceGuessTime, username);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<ScatterGraph.ScatterDataSet.Point> firstGuessDataPoints = averageFirstGuessTimeByUser.entrySet().stream()
                .map(entry -> {
                    Integer userID = entry.getKey();
                    String username = AnalysisService.usernames.get(userID);
                    Double multiplayerFirstGuessTime = entry.getValue();
                    Double practiceGuessTime = averagePracticeGuessTimeByUser.getOrDefault(userID, 0.0);
                    if (practiceGuessTime != null && practiceGuessTime > 0) {
                        labels.add(username);
                        return new ScatterGraph.ScatterDataSet.Point(multiplayerFirstGuessTime, practiceGuessTime, username);
                    }
                    return null;
                })
                .collect(Collectors.toList());

        firstGuessDataPoints = firstGuessDataPoints.stream().filter(Objects::nonNull).toList();
        correctGuessDataPoints = correctGuessDataPoints.stream().filter(Objects::nonNull).toList();

        datasets.add(new ScatterGraph.ScatterDataSet(
                "Correct Guess (Multiplayer, Practice)",
                correctGuessDataPoints,
                StatsService.barColors.get(0)
        ));

        datasets.add(new ScatterGraph.ScatterDataSet(
                "First Guess (Multiplayer, Practice)",
                firstGuessDataPoints,
                StatsService.barColors.get(1)
        ));

        return new ScatterGraph(labels, datasets);
    }


}
