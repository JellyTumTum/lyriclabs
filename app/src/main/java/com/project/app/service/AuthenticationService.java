package com.project.app.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.project.app.api.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.model.ApplicationUser;
import com.project.app.model.Role;
import com.project.app.repository.RoleRepository;
import com.project.app.repository.UserRepository;

@Service
@Transactional
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;


    public String insertUserIdInSessionKey(String sessionKey, int userID) {
        return userID + ":" + sessionKey;
    }

    public ApplicationUser registerUser(String username, String email, String password){

        String encodedPassword = passwordEncoder.encode(password);
        Role userRole = roleRepository.findByAuthority("USER").get();

        Set<Role> authorities = new HashSet<>();

        authorities.add(userRole);

        return userRepository.save(new ApplicationUser(0, username, email,  encodedPassword, authorities));
    }

    public LoginResponse loginUserByUsername(String username, String password){

        try {
            ApplicationUser user;
            Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                return new LoginResponse("Username does not exist");
            }

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = tokenService.generateJwt(auth, user);

            String sessionKey = tokenService.generateSessionKey(user);

            return new LoginResponse(user, token, sessionKey);

        } catch (BadCredentialsException e) {
            return new LoginResponse("Invalid password");
        }
    }

    public LoginResponse loginUserByEmail(String email, String password) {
        try {
            ApplicationUser user;
            Optional<ApplicationUser> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                return new LoginResponse("Email does not exist");
            }

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), password)
            );

            String token = tokenService.generateJwt(auth, user);
            String sessionKey = tokenService.generateSessionKey(user);

            return new LoginResponse(user, token, sessionKey);

        } catch (AuthenticationException e) {
            return new LoginResponse("Invalid Password");
        }
    }

}