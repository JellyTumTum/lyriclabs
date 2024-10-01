package com.project.app.model;

import java.time.Instant;

import jakarta.persistence.*;


@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length=1023)
    private String token;

    @Column(nullable = false)
    private Instant expiryTime; // Store the expiry time as a timestamp

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    public Token() {
    }

    public Token(String tokenValue, ApplicationUser user, Instant expiryTime) {
        this.token = tokenValue;
        this.user = user;
        this.expiryTime = expiryTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Instant expiryTime) {
        this.expiryTime = expiryTime;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }
// Getters and setters
    // ...
}
