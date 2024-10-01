package com.project.app.controller;

import com.project.app.api.LoadRequest;
import com.project.app.api.LoadResponse;
import com.project.app.model.Token;
import com.project.app.service.AuthenticationService;
import com.project.app.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@RestController
public class TokensController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationService authenticationService;

    public Cookie createSessionKeyCookie(String sessionKey, boolean isValid) {
        Cookie sessionCookie;
        if (isValid) {
            sessionCookie = new Cookie("sessionKey", sessionKey);
            sessionCookie.setMaxAge(5 * 24 * 60 * 60);
            sessionCookie.setSecure(false);  // TODO:  change to true when on HTTPS
            sessionCookie.setHttpOnly(true);
            sessionCookie.setPath("/");
        } else {
            sessionCookie = new Cookie("sessionKey", "");
            sessionCookie.setMaxAge(0); // basically deletes cookie because they are outside the session now anyway
            sessionCookie.setSecure(false);  // TODO: change to true when on HTTPS
            sessionCookie.setHttpOnly(true);
            sessionCookie.setPath("/");
        }
        return sessionCookie;
    }


    @PostMapping("/noAuth")
    public ResponseEntity<LoadResponse> lostAuth(@CookieValue(name = "sessionKey", required = false) String sessionCookie, HttpServletResponse response) {
        System.out.printf("passed over cookie : " + sessionCookie + "\n");
        LoadResponse loadResponse;
        Cookie responseCookie;
        if (sessionCookie == null || sessionCookie.isEmpty()) {
            responseCookie = createSessionKeyCookie("", false);
            response.addCookie(responseCookie);
            loadResponse = new LoadResponse("", "", "(NU) Null sessionCookie");
            return ResponseEntity.ok().body(loadResponse);
        }

        System.out.printf("inside lostAuth " + sessionCookie + "\n");
        int delimiterIndex = sessionCookie.indexOf(":");
        if (delimiterIndex != -1) {
            String userIDString = sessionCookie.substring(0, delimiterIndex);
            String sessionKey = sessionCookie.substring(delimiterIndex + 1);
            int userID = Integer.parseInt(userIDString);

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            Optional<Token> potentialTokenData = tokenService.returnTokenByUserID(userID);
            boolean isEntryPresent = potentialTokenData.isPresent();
            boolean doesKeyMatch = false;
            Token tokenData;
            System.out.printf("isEntryPresent : " + isEntryPresent);
            if (isEntryPresent) {
                tokenData = potentialTokenData.get();
                doesKeyMatch = encoder.matches(sessionKey, tokenData.getToken());
            }

            if (isEntryPresent && doesKeyMatch) {
                tokenData = potentialTokenData.get();
                Instant now = Instant.now();
                Duration timeLeft = Duration.between(now, tokenData.getExpiryTime());
                System.out.printf("timeLeft on token:  " + timeLeft);
                if (timeLeft.isNegative()) {
                    // refreshToken has elasped. need to relog brother.
                    int result = tokenService.deleteTokenData(tokenData.getToken());
                    System.out.printf("timeLeft is negative , result = " + result);
                    responseCookie = createSessionKeyCookie("", false);
                    response.addCookie(responseCookie);
                    loadResponse = new LoadResponse("", "", "(NE) Refresh Token has Expired | Session Timeout: you need to login again.");

                } else if (timeLeft.toHours() <= 24) {
                    // refresh token only has day left, might as well refresh the refresh wouldn't want the user being logged out.
                    tokenService.deleteByUserID(userID);
                    String newSessionKey = tokenService.generateSessionKey(tokenData.getUser());
                    newSessionKey = userID + ":" + newSessionKey;
                    responseCookie = createSessionKeyCookie(newSessionKey, true);
                    response.addCookie(responseCookie);
                    // /\ automatically adds to database. /\ //
                    //need new JWT as its expired.
                    String newJwt = tokenService.regenerateJwt(tokenData.getUser().getUsername());
                    loadResponse = new LoadResponse("",
                            tokenData.getUser().getUsername(),
                            "(YR) Refresh Token had less than 24 hours, new one has been sent over + into database",
                            newJwt);

                } else {
                    // duration has between 1 and 5d left.
                    System.out.printf("session between 1 and 5d left");
                    String newJwt = tokenService.regenerateJwt(tokenData.getUser().getUsername());
                    responseCookie = createSessionKeyCookie(sessionCookie, true);
                    response.addCookie(responseCookie);
                    loadResponse = new LoadResponse("",
                            tokenData.getUser().getUsername(),
                            "(YC) Refresh Token valid for > 24h, kept same token",
                            newJwt);

                }
                return ResponseEntity.ok().body(loadResponse);
            } else {
                // no token valid, no user is logged in
                System.out.printf("invalid refresh token.");
                responseCookie = createSessionKeyCookie("", false);
                response.addCookie(responseCookie);
                loadResponse = new LoadResponse("", "", "(NT) No session token present matching one provided.");
                return ResponseEntity.ok().body(loadResponse);
            }


        } else {
            responseCookie = createSessionKeyCookie("", false);
            response.addCookie(responseCookie);
            loadResponse = new LoadResponse("", "", "(NI) Incorrect Session Token Format (May of been incorrectly constructed or edited by user");
            return ResponseEntity.ok().body(loadResponse);
        }
    }


    @GetMapping("/asdf")
    public String asdf() {
        return "did you need auth for this? who knows";
    }
}













