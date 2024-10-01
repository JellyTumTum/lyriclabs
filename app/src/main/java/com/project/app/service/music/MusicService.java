package com.project.app.service.music;

import com.project.app.api.LyricData;
import com.project.app.api.rooms.FrontendArtist;
import com.project.app.model.music.Artist;
import com.project.app.model.music.Song;
import com.project.app.model.music.SongArtist;
import com.project.app.model.rooms.RoomArtist;
import com.project.app.model.rooms.RoomConfig;
import com.project.app.repository.music.ArtistRepository;
import com.project.app.repository.music.SongArtistRepository;
import com.project.app.repository.rooms.RoomArtistRepository;
import com.project.app.repository.rooms.RoomConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MusicService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private RoomArtistRepository roomArtistRepository;

    @Autowired
    private RoomConfigRepository roomConfigRepository;

    @Autowired
    private SongArtistRepository songArtistRepository;

    public void addRoomArtist(String roomID, Artist artist) {
        // assuming the room is already added, just need to add the artist.
        System.out.printf(artist.toString());
        artistRepository.save(artist); // if the artist is already in there it will just replace the old entry. --> literally free updating if that's the case.
        Optional<RoomConfig> roomConfig = roomConfigRepository.findByRoom_roomId(roomID); // under no circumstance should the room not exist.
        RoomArtist savedValue = roomArtistRepository.save(new RoomArtist(roomConfig.get(), artist));
        System.out.printf(savedValue.toString());


    }

    public void removeRoomArtist(String roomID, Artist artist) {

        System.out.printf(artist.toString());
        Optional<RoomConfig> optConfig = roomConfigRepository.findByRoom_roomId(roomID);
        optConfig.ifPresent(roomConfig -> roomArtistRepository.deleteByRoomIdAndArtistId(roomConfig, artist.getArtistId())); // crazy refactor from Intellij.

    }

    public void clearRoomArtistsByRoom(String roomID) {
        Optional<RoomConfig> roomConfig = roomConfigRepository.findByRoom_roomId(roomID);
        if (roomConfig.isPresent()) {
            Set<RoomArtist> artistSet = roomConfig.get().getRoomArtist();
            roomArtistRepository.deleteAll(artistSet);
            artistSet.clear();
        }
    }

    public List<Artist> getRoomArtists(String roomID) {
        return roomArtistRepository.findArtistsByRoomId(roomID);
    }

    public boolean containsFrontendArtist(List<Artist> artistList, FrontendArtist frontendArtist) {
        for (Artist artist : artistList) {
            if (Objects.equals(artist.getArtistId(), frontendArtist.getSpotifyID())) {
                return true;
            }
        }
        return false;
    }

    public List<FrontendArtist> convertToFrontendArtists(List<Artist> artistList) {
        if (artistList == null) {
            return null;
        }
        // This breaks it when it comes back to the backend whoops, due to thinning of information.
        List<FrontendArtist> returnList = new ArrayList<>();

        for (Artist artist : artistList) {
            returnList.add(new FrontendArtist(artist.getArtistId(), artist.getName(), artist.getArtURL()));
        }
        return returnList;
    }


    public List<LyricData> findSuitableLyrics(List<String> lyricList, int lyricsID) {

        List<LyricData> suitableSegments = new ArrayList<>();
        long wordCount;
        long uniqueWordCount;
//        System.out.println("\n [1000] Start Function");

        for (int index = 0; index < lyricList.size(); index++) {
//            System.out.println("\n [2000] Loop Start");
            String line = lyricList.get(index);
//            System.out.println("\n [2001] Line Retrieved - " + line + " [" + index + "]");

            wordCount = Pattern.compile("\\s+").splitAsStream(line.trim()).count();
//            System.out.println("\n [2002] Word Count Calculated - " + wordCount);
            uniqueWordCount = Pattern.compile("\\s+").splitAsStream(line.trim()).distinct().count();
//            System.out.println("\n [2003] Unique Word Count Calculated - " + uniqueWordCount);
//            System.out.println("\n currentIndex : " + index + " lyricList.size() = " + lyricList.size() + "\n");
            if (wordCount >= 9 && wordCount <= 15 && uniqueWordCount > 4) {
//                System.out.println("\n [2004] Added to Suitable Segments" + line);
                suitableSegments.add(new LyricData(line, uniqueWordCount, wordCount, lyricsID));
            } else if (wordCount < 9 && index < lyricList.size() - 1) {
//                System.out.println("\n [2005] Processing Next Line");
                line += " \n " + lyricList.get(index + 1); // adding \n so its still read as 2 lines on the frontend.
                wordCount = Pattern.compile("\\s+").splitAsStream(line.trim()).count();
//                System.out.println("\n [2006] Word Count Recalculated: 2007 - " + wordCount);
                uniqueWordCount = Pattern.compile("\\s+").splitAsStream(line.trim()).distinct().count();
//                System.out.println("\n [2007] Unique Word Count Recalculated - " + uniqueWordCount);

                if (wordCount >= 9 && wordCount <= 15 && uniqueWordCount > 4) {
//                    System.out.println("\n [2008] Adding to Suitable Segments After Recalculation - " + line);
                    suitableSegments.add(new LyricData(line, uniqueWordCount, wordCount, lyricsID));
                }
            }
        }
        if (suitableSegments.size() > 0) {
            System.out.printf("\n findSuitableLyrics: " + suitableSegments + "\n");
        } else {
            System.out.printf("\n findSuitableLyrics: NO SUITABLE LYRICS FOUND \n");
        }
        return suitableSegments;

    }

    public double calculateLyricWeight(LyricData chosenLyrics) {
        // TODO: Implement actual calculations
//        Test 5: 120 songs over various artists -> data from this are used for averages. not highest data sample but aint got all day. (24/01/24)
        double UF_AVERAGE = 0.9334204;
        double UF_LOWER_BOUND = 0.8;
        double UF_UPPER_BOUND = 1.0;

        double RF_AVERAGE = 1.09260812;
        double RF_LOWER_BOUND = 1.0;
        double RF_UPPER_BOUND = 1.25;

        double SF_AVERAGE = 4.154284;
        double SF_LOWER_BOUND = 3.4;
        double SF_UPPER_BOUND = 4.7;

        float uniquenessFactor = chosenLyrics.getUniqueWords() / (float) chosenLyrics.getWordCount();
        float repetitionFactor = calculateRepetitionScore(chosenLyrics);
        float simplicityFactor = calculateSimplicity(chosenLyrics);

        System.out.println("\n Uniqueness Factor: " + uniquenessFactor + "\n" +
                "Repetition Factor: " + repetitionFactor + "\n" +
                "Simplicity Factor: " + simplicityFactor + "\n");

        // Calculate Gaussian weights -> normal distribution.
        double ufWeight = gaussianWeight(uniquenessFactor, UF_AVERAGE, UF_LOWER_BOUND, UF_UPPER_BOUND);
        double rfWeight = 1 - gaussianWeight(repetitionFactor, RF_AVERAGE, RF_LOWER_BOUND, RF_UPPER_BOUND);
        double sfWeight = 1 - gaussianWeight(simplicityFactor, SF_AVERAGE, SF_LOWER_BOUND, SF_UPPER_BOUND);

        // equal weighting -> simplicity factor gets the .1 just cause its probably the most important
        double combinedScore = 0.33 * ufWeight + 0.33 * rfWeight + 0.34 * sfWeight;
        double finalWeight = 0.8 + combinedScore * 0.4;

        System.out.println("\n CalculateLyricWeight:Calculated Weights:\n" +
                "Uniqueness Factor Weight: " + ufWeight + "\n" +
                "Repetition Factor Weight: " + rfWeight + "\n" +
                "Simplicity Factor Weight: " + sfWeight + "\n" +
                "Combined Score: " + combinedScore + "\n" +
                "Final Weight: " + finalWeight + "\n");


        // Scale to 0.8-1.2 so that weight cant be crazy skewed and make something useless. can always be changed to be more impactful if it works. (24/01/24)
        return finalWeight;
    }

    public static double gaussianWeight(double value, double average, double lowerBound, double upperBound) {
        // Calculate s.d
        double stdDev = (upperBound - lowerBound) / 6; // 99.7% data within ±3σ

        // bam calculation
        return Math.exp(-Math.pow(value - average, 2) / (2 * Math.pow(stdDev, 2)));
    }

    public float calculateRepetitionScore(LyricData lyricData) {
        return  (float) lyricData.getWordCount() / lyricData.getUniqueWords();
    }

    public float calculateSimplicity(LyricData lyricData) {

        float totalChars = 0;
        for (String word : lyricData.getLyrics().split(" ")) {
            totalChars += word.length();
        }
        return (totalChars / lyricData.getWordCount());

    }

    public List<Artist> getArtistListFromSongArtists(Set<SongArtist> songArtists) {
        // another intellij refactor classic think for loop with getArtist() if this doesn't work in the future (04/01/2024)
        return songArtists.stream()
                .map(SongArtist::getArtist)
                .collect(Collectors.toList());
    }

    public List<List<Song>> loadSongsFromDatabase(List<Artist> artists) {
        List<List<Song>> artistSongs = new ArrayList<>(new ArrayList<>());
        for (Artist artist : artists) {
            List<Song> artistsSongs = songArtistRepository.findSongsByArtistWithLyrics(artist);

            // Filter the list to include only songs with lyrics that are saved.
            List<Song> filteredSongs = artistsSongs.stream()
                    .filter(song -> song.getLyricID() > 0)
                    .collect(Collectors.toList());

            artistSongs.add(filteredSongs);
        }


        return artistSongs;
    }
    public Song selectRandomSong(List<List<Song>> artistSongs) {
        Random random = new Random();
        List<Song> allSongs = new ArrayList<>();

        // move to 1 list
        for (List<Song> songList : artistSongs) {
            allSongs.addAll(songList);
        }

        if (allSongs.isEmpty()) {
            return null;
        }

        return allSongs.get(random.nextInt(allSongs.size()));
    }
}
