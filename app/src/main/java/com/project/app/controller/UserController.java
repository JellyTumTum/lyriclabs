package com.project.app.controller;

import com.project.app.api.LoadResponse;
import com.project.app.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private TokenService tokenService;


    @GetMapping("/test")
    public String helloUserController(){
        return "User access level";
    }

    @GetMapping("/logout")
    public String logoutUser(@CookieValue(name = "sessionKey", required = false) String sessionCookie, HttpServletResponse response) {

        System.out.printf("inside logout function");
        if (sessionCookie == null) {
            System.out.printf("no sessionCookie found");
            return "null sessionCookie";
        }
        // grab userID from the sessionKey cookie.
        String userIDString = sessionCookie.substring(0, sessionCookie.indexOf(":"));
        // remove session from tokens table to 'log them out'
        int affectedRows = tokenService.deleteByUserID(Long.parseLong(userIDString));
        // send over empty cookie to log out from frontend.
        // invalidate JWT. --> turns out not nessecary due to their short life span. LETS GO
        Cookie responseCookie = new Cookie("sessionKey", "");
        responseCookie.setMaxAge(0); // basically deletes cookie because they are outside the session now anyway
        responseCookie.setSecure(false);  // TODO change to true when on HTTPS
        responseCookie.setHttpOnly(true);
        responseCookie.setPath("/");
        response.addCookie(responseCookie);
        if (affectedRows > 0) { return "User has been deleted from the database"; }
        else {return "no user has been deleted, OOPS i guess"; }
    }

    @GetMapping("/pageload")
    public LoadResponse pageLoad() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoadResponse loadResponse;
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();

            String username = jwt.getClaimAsString("sub"); // "sub" is the subject claim, which holds the username

            // Construct and return the LoadResponse object
            loadResponse = new LoadResponse("", username, "(Y) successful authorization and username is returned");
            return loadResponse;
        } else {
            loadResponse = new LoadResponse("", "", "(N) Jwt Token invalid, need to re-acquire auth.");
        }
        return loadResponse;
    }



}