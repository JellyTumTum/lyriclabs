package com.project.app.controller;

import com.project.app.api.HistoryPublic;
import com.project.app.api.rooms.*;
import com.project.app.model.ApplicationUser;
import com.project.app.model.UserGuess;
import com.project.app.model.music.Artist;
import com.project.app.model.rooms.Room;
import com.project.app.model.rooms.RoomConfig;
import com.project.app.model.rooms.RoomUser;
import com.project.app.repository.rooms.RoomUserRepository;
import com.project.app.service.*;
import com.project.app.service.music.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;

@RestController
public class PracticeController {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private MusicService musicService;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private RoomUserRepository roomUserRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private PracticeService practiceService;


    @MessageMapping("/executeRound")
    public void execRound(PracticeRoundRequest startRoundRequest, Principal principal) {
        Optional<Room> optRoom = roomService.findByRoomId(startRoundRequest.getRoomID());
        optRoom.ifPresent(room -> practiceService.executeRound(room));

    }

    @MessageMapping("/endPractice")
    public void executeEnd(PracticeRoundRequest endPracticeRequest, Principal principal) {
        Optional<Room> optRoom = roomService.findByRoomId(endPracticeRequest.getRoomID());
        optRoom.ifPresent(room -> gameService.endGame(room.getRoomId()));
    }

}