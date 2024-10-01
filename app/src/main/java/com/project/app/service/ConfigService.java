package com.project.app.service;

import com.project.app.api.rooms.FrontendUserInfo;
import com.project.app.api.rooms.HostMigration;
import com.project.app.model.ApplicationUser;
import com.project.app.model.rooms.Room;
import com.project.app.repository.UserRepository;
import com.project.app.repository.rooms.RoomConfigRepository;
import com.project.app.repository.rooms.RoomRepository;
import com.project.app.repository.rooms.RoomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomConfigRepository roomConfigRepository;

    @Autowired
    private RoomUserRepository roomUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate template;

}
