package com.project.app.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.app.model.ApplicationUser;
import com.project.app.model.Role;
import com.project.app.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("In the user details service");

        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user is not valid"));
    }

    public boolean isEmailAlreadyRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isUsernameAlreadyRegistered(String username) {
        return userRepository.existsByUsername(username);
    }

    public Optional<ApplicationUser> getUserByUsername(String username) { // alternative to loadUserByUsername() that doesn't return userDetails as not required.
        return userRepository.findByUsername(username);
    }

}