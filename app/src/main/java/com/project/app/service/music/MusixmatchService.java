package com.project.app.service.music;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.project.app.api.musixmatch.MatcherLyricsResponse;
import com.project.app.api.musixmatch.MatcherTrackResponse;
import com.project.app.model.music.Artist;
import com.project.app.model.music.Song;
import com.project.app.model.music.SongArtist;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MusixmatchService {

    private static final String API_KEY = System.getenv("LL_MUSIXMATCH_KEY");
    private final String API_URL = "https://api.musixmatch.com/ws/1.1";

    public MatcherLyricsResponse fetchLyrics(Song song) throws UnsupportedEncodingException {
        System.out.printf("\n Inside fetchLyrics(Song) \n");
        // TODO: get an artistName (probably first) to send to overloaded function with songName.
        System.out.println("\n fetchLyrics: songArtistList(raw) = " + song.getSongArtists() + "\n");
        Set<SongArtist> songArtistList = song.getSongArtists();
        System.out.println("\n fetchLyrics: songArtistList = " + songArtistList + "\n");
        Optional<SongArtist> firstArtistOpt = songArtistList.stream().findFirst();
        Artist firstArtist;
        if (firstArtistOpt.isPresent()) {
            firstArtist = firstArtistOpt.get().getArtist();
        } else {
            return null;
        }
        System.out.println("fetchLyrics : firstArtist = " + firstArtist + "\n");
        String songName = song.getName();
        System.out.println("fetchLyrics : songName = " + songName + "\n");
        MatcherLyricsResponse matcherLyricsResponse = fetchLyrics(firstArtist.getName(), songName);
        System.out.printf("\n fetchLyrics(Song): matcherLyricsResponse = " + matcherLyricsResponse + "\n");
        return matcherLyricsResponse;
    }

    public MatcherLyricsResponse fetchLyrics(String artistName, String songName) throws UnsupportedEncodingException {
        System.out.printf("\n Inside fetchLyrics(artistName,  songName) \n");

        String encodedSongName = URLEncoder.encode(songName, StandardCharsets.UTF_8);
        String encodedArtistName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);

        System.out.println("\n NOT USED ATM | encoded songName + encodedArtistName : " + encodedSongName + encodedArtistName + "\n");


        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .path("/matcher.lyrics.get")
                .queryParam("apikey", API_KEY)
                .queryParam("q_track", songName)
                .queryParam("q_artist", artistName)
                // .queryParam("track_isrc", trackIsrc) // For if I ever bother with ISRC.
                .build()
                .toUriString();

        System.out.printf("\n url = " + url + "\n");
        RestTemplate restTemplate = new RestTemplate();


        try {
            System.out.printf("\n inside try-catch \n");

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // all this just to get the status_code
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode statusCodeNode = rootNode.path("message").path("header").path("status_code");
            System.out.printf("\n fetchLyrics:musixmatchCallStatusCode " + statusCodeNode.intValue() + "\n");

            if (statusCodeNode.isInt() && statusCodeNode.intValue() == 404) {
                // multiple causes for this, so easiest just to select a new song.
                System.out.printf("\n fetchLyrics: code 404 detected \n");
                    return null;

                }
            System.out.println("\n ResponseRAW: " + response + "\n");
            MatcherLyricsResponse lyricsResponse = objectMapper.readValue(response.getBody(), MatcherLyricsResponse.class);
            System.out.printf("\n fetchLyrics:response.body.message.header.response_code : " + lyricsResponse.getMessage().getHeader().getStatus_code() + "\n");
            if (lyricsResponse.getMessage().getBody().getLyrics().getLyricsBody().isEmpty()) {
                // rare issue only currently seen by https://api.musixmatch.com/ws/1.1/matcher.lyrics.get?apikey=CLASSIFIED&q_track=The Last Time&q_artist=Taylor Swift, where code=200 but lyricbody is empty. makes sense to cover it here incase it happens again.
                System.out.printf("\n fetchLyrics: code 200 but lyricBody.isEmpty() = true detected \n");
                return null;
            }

            return lyricsResponse;
        } catch (HttpClientErrorException ex) {
            System.out.printf("\n HttpClientErrorException: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.printf("\n Exception: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
        return new MatcherLyricsResponse();
    }

    public MatcherTrackResponse fetchTrack(String artistName, String songName, String albumName) {
        //TODO : IF EVER NEEDS TO BE USED, adopt fetchLyrics approach as musixmatch uses String responses not json for some reason. (04/01/24)
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(API_URL)
                .path("/matcher.track.get")
                .queryParam("apikey", API_KEY)
                .queryParam("q_track", songName)
                .queryParam("q_artist", artistName)
                .queryParam("q_album", albumName);
        if (!albumName.isEmpty()) {
            urlBuilder.queryParam("q_album", albumName);
        }
        String url = urlBuilder.build().toString();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MatcherTrackResponse> response = restTemplate.getForEntity(url, MatcherTrackResponse.class);
        return response.getBody();

    }
}
