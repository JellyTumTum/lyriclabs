package com.project.app.service;

import com.project.app.model.music.Song;
import com.project.app.repository.music.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SchedulerService {

    @Autowired
    private SongRepository songRepository;

    @Scheduled(fixedRate = 1800000) // 30 minutes
    public void clearOldLyrics() {

        System.out.println("\n\n\n CLEARING OLD LYRICS \n\n\n");
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime minTime = currentTime.minusHours(12);
        List<Song> songs = songRepository.findSongsWithExpiredLyrics(minTime);

        for (Song song : songs) {
            song.removeLyricInformation(); // Clear lyrics
            song.setLastAccessed(LocalDateTime.now());
        }

        songRepository.saveAll(songs);
        System.out.println("\n\n\n DONE CLEARING OLD LYRICS \n\n\n");
    }


}
