package com.project.app.controller;

import com.project.app.api.LoginResponse;
import com.project.app.api.RegistrationResponse;
import com.project.app.api.UserInfoDTO;
import com.project.app.model.UserStats;
import com.project.app.repository.TokensRepository;
import com.project.app.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.project.app.model.ApplicationUser;
import com.project.app.service.AuthenticationService;
import com.project.app.service.UserStatsService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    private TokensRepository tokensRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserStatsService userStatsService;


    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@RequestBody UserInfoDTO body) {
        ResponseEntity<RegistrationResponse> response;
        String email = body.getEmail() == null ? "" : body.getEmail();
        System.out.printf(body.toString() + "\n");
        boolean usernameRegistered = userService.isUsernameAlreadyRegistered(body.getUsername());
        if (body.getUsername().isEmpty()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RegistrationResponse("No username entered", false));
        }
        if (body.getPassword().isEmpty()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RegistrationResponse("No username entered.", false));
        }
        System.out.println("Is email registered: " + usernameRegistered);
        ApplicationUser newUser = null;
        boolean accountCreated = false;
        if (!usernameRegistered) {
            newUser = authenticationService.registerUser(body.getUsername(), email, body.getPassword());
            response = ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RegistrationResponse("Account Created", true));
            accountCreated = true;
        } else {
            response = ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RegistrationResponse("Username in use", false));
        }

        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody UserInfoDTO body, HttpServletResponse response){
        if (body.getUsername().isEmpty() && body.getEmail().isEmpty()) {
            return ResponseEntity.ok().body(new LoginResponse("No username entered"));
        }
        boolean loginWithEmail = body.getUsername().equals(""); // if logging in with Email username field would be blank.
        LoginResponse userInfo;
        if (!loginWithEmail) {
            userInfo = authenticationService.loginUserByUsername(body.getUsername(), body.getPassword());
        } else {
            userInfo = authenticationService.loginUserByEmail(body.getEmail(), body.getPassword());
        }
        if (userInfo.getJwt() == null) {
            return ResponseEntity.ok(userInfo);
        }
        String modifiedSessionKey = authenticationService.insertUserIdInSessionKey(userInfo.getSessionKey(), userInfo.getUserId());
        userInfo.setSessionKey(""); // clearing this attribute so it isn't sent over to the frontend outside of the cookie.

        Cookie sessionCookie = new Cookie("sessionKey", modifiedSessionKey);
        sessionCookie.setMaxAge(5 * 24 * 60 * 60);
        sessionCookie.setSecure(false);  // TODO change to true when on HTTPS
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");

        response.addCookie(sessionCookie);

        return ResponseEntity.ok().body(userInfo);
    }

    @GetMapping("/test")
    public String testCall() {
        return "Test Call";
    }

}