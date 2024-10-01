package com.project.app.service.music;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.app.api.WrappedSongChoice;
import com.project.app.api.rooms.GameData;
import com.project.app.api.spotify.*;
import com.project.app.model.music.*;
import com.project.app.repository.music.*;
import com.project.app.repository.rooms.RoomArtistRepository;
import com.project.app.service.GameService;
import jakarta.annotation.PostConstruct;
import org.hibernate.service.spi.Wrapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


// Cache
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SpotifyService {

    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String CLIENT_ID = System.getenv("LL_SPOTIFY_ID");
    private static final String CLIENT_SECRET = System.getenv("LL_SPOTIFY_SECRET");

    private static final String SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1";
    private static final String SEARCH_URL = SPOTIFY_API_BASE_URL + "/search";
    private static final String ARTIST_URL = SPOTIFY_API_BASE_URL + "/artists";
    private static final String ALBUM_URL = SPOTIFY_API_BASE_URL + "/albums";


    public static Cache<String, Artist> artistMap = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    private static Cache<String, List<Artist>> cacheMap = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    public static Queue<Long> apiCalltimes = new ConcurrentLinkedQueue<>();

    public static int RATE_LIMIT = 90; // spotify don't say their own limit, saw 180 per minute on Google in a rolling 30-second window so.

    public static int RESERVE_CALLS = RATE_LIMIT / 3;
    private String accessToken;
    private long expiryTime = -1;

    @Autowired
    private MusicService musicService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistAlbumRepository artistAlbumRepository;

    @Autowired
    private RoomArtistRepository roomArtistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private MusixmatchService musixmatchService;

    @Autowired
    private SongArtistRepository songArtistRepository;


    // Method to initialize the token at startup
    @PostConstruct
    public void init() {
        refreshAccessToken();
    }

    // Synchronized method to ensure thread-safe access
    public synchronized String getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() > expiryTime || this.expiryTime == -1) {
            refreshAccessToken();
        }
        return accessToken;
    }

    public void refreshAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // data type spotify expects.
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(TOKEN_URL, request, JsonNode.class);
        JsonNode jsonNode = response.getBody();

        this.accessToken = jsonNode.path("access_token").asText();
        this.expiryTime = System.currentTimeMillis() + (3600 * 1000);
    }

    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public boolean checkRateLimitStatus() {
        long now = System.currentTimeMillis();

        // Remove expired timestamps
        while (!apiCalltimes.isEmpty() && now - apiCalltimes.peek() > 30000) {
            apiCalltimes.poll();
        }
        // Check rate limit
        if (apiCalltimes.size() < RATE_LIMIT) {
            apiCalltimes.add(now);
            return true;
        } else {
            return false;
        }
    }

    public int getAvailableCallCount() {
        long now = System.currentTimeMillis();

        // Remove expired timestamps
        while (!apiCalltimes.isEmpty() && now - apiCalltimes.peek() > 30000) {
            apiCalltimes.poll();
        }

        // Calculate the number of available calls
        int temp = Math.max(RATE_LIMIT - apiCalltimes.size(), 0);
        System.out.println("\n getAvailableCallCount : callCount = " + temp + "\n");
        return temp;
    }

    public List<Artist> findArtist(String artistName, int maxResults) {

        // Caching baby (21/12/23)
        artistName = artistName.trim().replace(' ', '+');
        /* trims space for some efficiency,
        replaces ' ' with + to remove issues when specifically encountered
        when searching "Taylor ..." IDK why after the space it broke,
        but postman was using + by default and didn't get error so. */
        List<Artist> artistList = cacheMap.getIfPresent(artistName);
        if (artistList != null) {
            System.out.printf("CACHE FOR THE WIN");
            return artistList;
        }
        // boo no cache
        HttpHeaders headers = getHeaders();


        String url = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                .queryParam("q", artistName)
                .queryParam("type", "artist")
                .queryParam("limit", maxResults)
                .toUriString();
        System.out.println("URL: " + url + "\n");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        boolean canCall = checkRateLimitStatus();
        ResponseEntity<ArtistSearchResponse> response;
        if (canCall) {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, ArtistSearchResponse.class);
            ArtistSearchResponse artistSearchResponse = response.getBody();
            try {
                artistList = mapToArtistList(artistSearchResponse.getArtists().getItems(), maxResults);
            } catch (NullPointerException e) {
                System.out.printf("ArtistSearchResponse == null" + e);
                artistList = new ArrayList<>();
            }
        } else {
            return null;
        }


        for (Artist a : artistList) {
            SpotifyService.artistMap.put(a.getArtistId(), a);
        }
        cacheMap.put(artistName, artistList);
        return artistList;
    }

    private List<Artist> mapToArtistList(List<ArtistItem> artistItems, int maxResults) {
        List<Artist> artists = new ArrayList<>();
        for (ArtistItem item : artistItems) {
            Artist artist = new Artist(item);
//            artist.setArtistId(item.getId());
//            artist.setName(item.getName());
//            artist.setPopularity(item.getPopularity());
//            artist.setArtURL(item.selectProfilePicture());
//            artist.setFollowerCount((long) item.getFollowers().getTotal());
            // Set other fields if needed
            artists.add(artist);
        }
        return artists;
    }

    public Artist getArtistDetailsBySpotifyID(String spotifyID) {

        Optional<Artist> optArtist = artistRepository.findByArtistId(spotifyID);
        Artist artistDetails;
        if (optArtist.isPresent()) {
            artistDetails = optArtist.get();
            return artistDetails;
        } else {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHeaders();
            String url = ARTIST_URL + "/" + spotifyID;

            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            if (checkRateLimitStatus()) {
                ResponseEntity<ArtistItem> response = restTemplate.exchange(url, HttpMethod.GET, entity, ArtistItem.class);
                return new Artist(response.getBody());

            } else {
                return null;
            }
        }
    }

    public Artist getFullArtistDetails(String spotifyID) {
        // uses artistMap to get regular artist from frontendArtist.
        // realistically doesn't need a function
        Artist artist = SpotifyService.artistMap.asMap().get(spotifyID); // .asMap() removes the use of concurrentMap, which can have negatives but done seem to effect this use case.
        if (artist == null) {
            return getArtistDetailsBySpotifyID(spotifyID);
        } else {
            return artist;
        }
    }

    public List<List<Album>> loadAlbums(String roomID, List<Artist> artists) {
        Set<Artist> artistSet = new HashSet<>(artists);
//        System.out.printf(" \n loadAlbums:artistsSet = " + artistSet + "\n");
        GameService.roomDataMap.get(roomID).setArtistsList(artistSet);
        GameService.roomDataMap.get(roomID).setSelectedArtists(artistSet);
//        System.out.printf(" GameService.roomDataMap = " + GameService.roomDataMap + "\n");
        List<List<Album>> albumList = new ArrayList<>();
        for (Artist artist : artists) {
            List<Album> albums = getArtistAlbums(artist.getArtistId());
            albumList.add(albums);
        }
        return albumList;
    }

    public List<Album> getArtistAlbums(String artistSpotifyID) {
        // Fetch all artists albums
        // add them to database with linkage to artist.
        String apiURL = ARTIST_URL + "/" + artistSpotifyID + "/albums"; // custom made for this due to having extra stuff added on.
        System.out.printf("\n " + apiURL + "\n");
        HttpHeaders headers = getHeaders();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        if (getAvailableCallCount() > RESERVE_CALLS) {
            ResponseEntity<ArtistAlbumResponse> response = restTemplate.exchange(apiURL, HttpMethod.GET, entity, ArtistAlbumResponse.class);
            ArtistAlbumResponse artistAlbumResponse = response.getBody();
            return artistAlbumToAlbumConverter(artistAlbumResponse);
        } else {
            List<Album> artistAlbums = artistAlbumRepository.findAlbumsByArtistId(artistSpotifyID);
            if (artistAlbums.isEmpty() && checkRateLimitStatus()) {
                ResponseEntity<ArtistAlbumResponse> response = restTemplate.exchange(apiURL, HttpMethod.GET, entity, ArtistAlbumResponse.class);
                ArtistAlbumResponse artistAlbumResponse = response.getBody();
                return artistAlbumToAlbumConverter(artistAlbumResponse);
            } else {
                return artistAlbums; // here this else is the more common one to occur (hopefully).
            }
        }
    }

    public List<Album> artistAlbumToAlbumConverter(ArtistAlbumResponse response) {

        Map<ReducedArtistInfo, Artist> cachedArtists = new HashMap<>();
        List<AlbumItem> albumItems = response.getItems();
        List<Album> albumList = new ArrayList<>();
        for (AlbumItem item : albumItems) {
            // create Album Object + save to db.
            // save all artists to database and create Set<ArtistAlbum>
            Set<ArtistAlbum> albumArtists = new HashSet<>();
            Album currentAlbum = new Album(item);

            for (ReducedArtistInfo a : item.getArtists()) {
                Artist artist;
                Artist cacheCall = cachedArtists.get(a);
                if (cacheCall == null) {
                    artist = getArtistDetailsBySpotifyID(a.getId());
                    cachedArtists.put(a, artist);
                } else {
                    artist = cacheCall;
                }
                artistRepository.save(artist);
                ArtistAlbum link = new ArtistAlbum(artist, currentAlbum);
//                artistAlbumRepository.save(link);
                albumArtists.add(link);
            }
            currentAlbum.setArtistAlbums(albumArtists);
            albumRepository.save(currentAlbum);
            albumList.add(currentAlbum);
        }
        return albumList;
    }

    public Song fetchSongByAlbum(Album album, List<Artist> artists, String roomID) {
        // Retrieve a song for a given album
        // save them to the songs table. (done within getFullSongsDetailsHref())
        // create relationships between both songArtists and songs. (no album relations due to many-many)

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        String url = ALBUM_URL + "/" + album.getAlbumId() + "/tracks";
        Random random = new Random();

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<AlbumSongResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, AlbumSongResponse.class);
        AlbumSongResponse albumSongResponse = response.getBody();
        List<ReducedSongInfo> rawSongs = albumSongResponse.getItems();
        ReducedSongInfo song;
        // list of just ids so its fast, cause the ammount of streaming and filtering going on seems bad already.
        Set<String> artistIds = artists.stream()
                .map(Artist::getArtistId)
                .collect(Collectors.toSet());

        // Step 2: Filter the rawSongs list
        List<ReducedSongInfo> filteredSongs = rawSongs.stream()
                .filter(songInfo -> songInfo.getArtists().stream()
                        .anyMatch(artist -> artistIds.contains(artist.getId())))
                .toList();

        // Step 3: reduce filteredSongs to not include any songs already used.
        if (roomID != null || !roomID.isEmpty()) { // allows function to for some reason work if i dont want to pass a roomID in.
            GameService.roomDataMap.get(roomID).getSongList();
            // further removes songs to prevent duplicate songs appearing.
            Set<Song> roomSongList = GameService.roomDataMap.get(roomID).getSongList();
            Set<String> usedSongIds = roomSongList.stream()
                    .map(Song::getSongId)
                    .collect(Collectors.toSet());
            filteredSongs = filteredSongs.stream()
                    .filter(songInfo -> !usedSongIds.contains(songInfo.getId()))
                    .toList();
        }



        // Step 3: Randomly select a song from the filtered list
        if (!filteredSongs.isEmpty()) {
            int randomIndex = random.nextInt(filteredSongs.size());
            song = filteredSongs.get(randomIndex);

            Optional<Song> optSong = songRepository.findBySongId(song.getId());
            if (optSong.isPresent()) {
                return optSong.get();
            } else {
                if (song.getLinkToOriginal() == null) {
                    return getFullSongDetailsHref(song.getHref(), album);
                } else if (!song.getLinkToOriginal().getHref().isEmpty()) {
                    return getFullSongDetailsHref(song.getLinkToOriginal().getHref(), album);
                } else {
                    return getFullSongDetailsHref(song.getHref(), album);
                }
            }

        } else {
            return null;
        }
    }

    public List<Song> fetchSongsByAlbum(Album album) {

        // ** DEPRECATED **
        // due to more efficient option, fetchSongByAlbum(). Keeping incase it ever becomes useful.

        // Retrieve all songs for a given album
        // save them to the songs table. (done within getFullSongsDetailsHref())
        // create relationships between both songArtists and songs. (no album relations due to many-many)

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        String url = ALBUM_URL + "/" + album.getAlbumId() + "/tracks";

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        System.out.println("\n fetchSongsByAlbum: url = " + url + "\n");
        ResponseEntity<AlbumSongResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, AlbumSongResponse.class);
        AlbumSongResponse albumSongResponse = response.getBody();
        List<ReducedSongInfo> reducedSongs = albumSongResponse.getItems();
        List<Song> songs = new ArrayList<>();
        for (ReducedSongInfo song : reducedSongs) {

            if (song.getLinkToOriginal() == null) {
                System.out.printf("\n case 1 \n");
                songs.add(getFullSongDetailsHref(song.getHref(), album));
            } else if (!song.getLinkToOriginal().getHref().isEmpty()) {
                System.out.printf("\n case 2 \n");
                songs.add(getFullSongDetailsHref(song.getLinkToOriginal().getHref(), album));
            } else {
                System.out.printf("\n case 3 \n");
                songs.add(getFullSongDetailsHref(song.getHref(), album));
            }
        }
        return songs;

    }

    public List<Song> fetchSongsByAlbum(String spotifyAlbumID) {

        // ** DEPRECATED **
        // due to more efficient option, fetchSongByAlbum(). Keeping incase it ever becomes useful.

        Optional<Album> matchingAlbum = albumRepository.findByAlbumId(spotifyAlbumID);
        if (matchingAlbum.isPresent()) {
            return fetchSongsByAlbum(matchingAlbum.get());
        } else {
            // rare case, shouldn't turn up but should get function done at some point.
            return fetchSongsByAlbum(getFullAlbumDetails(spotifyAlbumID));
        }
    }

    public Album getFullAlbumDetails(String spotifyAlbumID) {
        //TODO: Complete function
        System.out.printf("TODO: Complete function \n");
        return new Album();
    }


    public Song getFullSongDetails(String spotifySongID) {
        // TODO: if needed complete.
        return new Song();
    }

    public Song getFullSongDetailsHref(String href, Album album) {

        System.out.printf("\n getFullSongDetailshref: href = " + href + "\n");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<ReducedSongInfo> response = restTemplate.exchange(href, HttpMethod.GET, entity, ReducedSongInfo.class);
        ReducedSongInfo reducedSongInfo = response.getBody();
        System.out.printf(reducedSongInfo.getName());
        Song song = new Song(reducedSongInfo, album);
        Set<SongArtist> songArtists = new HashSet<>();
        for (ReducedArtistInfo artistInfo : reducedSongInfo.getArtists()) {
            Artist artist = getFullArtistDetails(artistInfo.getId());
            artistRepository.save(artist);
            Optional<SongArtist> songArtist = songArtistRepository.findBySongAndArtist(song, artist);
            if (songArtist.isPresent()) {
                songArtists.add(songArtist.get());
            } else {
                songArtists.add(new SongArtist(artist, song));
            }

        }
        song.setSongArtists(songArtists);
        System.out.printf("\n getFullSongDetailsHref: songArtist : " + song.getSongArtists() + "\n");
        songRepository.save(song);
        for (SongArtist songArtist : songArtists) {
            System.out.println("\n getFullSongDetailsHref: songArtist: " + songArtist + "\n");
            boolean inDatabase = songArtistRepository.existsBySongAndArtist(songArtist.getSong(), songArtist.getArtist());
            if (!inDatabase) {
                songArtistRepository.save(songArtist);
            }

        }
        return song;

    }

    public WrappedSongChoice chooseSongFromRoomAlbums(List<List<Album>> artistAlbums, List<Artist> artists, String roomID) {

        Random random = new Random();

        int randomArtistIndex;
        int randomAlbumIndex;
        List<Album> chosenArtistAlbums;
        Album chosenAlbum;
        Song chosenSong;

        if (artistAlbums.isEmpty()) {
            return new WrappedSongChoice(-1, -1, null);
        }

        randomArtistIndex = random.nextInt(artistAlbums.size());
        chosenArtistAlbums = artistAlbums.get(randomArtistIndex);

        if (chosenArtistAlbums.size() == 0) {
            return new WrappedSongChoice(randomArtistIndex, -1, null);
        }
        randomAlbumIndex = random.nextInt(chosenArtistAlbums.size());
        chosenAlbum = chosenArtistAlbums.get(randomAlbumIndex);

        chosenSong = fetchSongByAlbum(chosenAlbum, artists, roomID);

        return new WrappedSongChoice(randomArtistIndex, randomAlbumIndex, chosenSong);

    }

//    public Song chooseSongFromAlbumList(List<Album> albumList, List<Artist> artists) {
//
//        // ** DEPRECATED ** Due to inclusion of spareSongs into gameData.
//
//        Random random = new Random();
//        int randomAlbumIndex;
//        Album chosenAlbum;
//        Song chosenSong;
//
//        randomAlbumIndex = random.nextInt(albumList.size());
//        chosenAlbum = albumList.get(randomAlbumIndex);
//        chosenSong = fetchSongByAlbum(chosenAlbum, artists);
//        return chosenSong;
//
//
//    }
}





