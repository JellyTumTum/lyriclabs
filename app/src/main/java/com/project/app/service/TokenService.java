package com.project.app.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.project.app.configuration.StompPrincipal;
import com.project.app.model.ApplicationUser;
import com.project.app.model.Token;
import com.project.app.repository.TokensRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private TokensRepository tokensRepository;

    public String generateSessionKey(ApplicationUser user) {
        // Generate a random session key & Hash
        String sessionKey = generateRandomSessionKey();
        String hashedSessionKey = hashSessionKey(sessionKey);

        // Calculate the expiry time (5 days from now) and add to a tokenObj
        Instant expiryTime = Instant.now().plus(Duration.ofDays(5));
        Token tokenData = new Token(hashedSessionKey, user, expiryTime);

        // Save to the database
        deleteByUserID(Long.valueOf(user.getUserId()));
        saveToTable(tokenData);

        // Return the unhashed session key
        return sessionKey;
    }

    private String generateRandomSessionKey() {
        // Generate a random session key of the desired length
        int keyLength = 64; // Adjust this as needed
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder key = new StringBuilder(keyLength);
        Random random = new Random();
        for (int i = 0; i < keyLength; i++) {
            int index = random.nextInt(characters.length());
            key.append(characters.charAt(index));
        }
        return key.toString();
    }

    public String hashSessionKey(String sessionKey) {
        // Use a secure hash function (e.g., BCrypt) to hash the session key
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(sessionKey);
    }

    public String generateJwt(Authentication auth, ApplicationUser user){

        Instant now = Instant.now();

        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .claim("roles", scope)
                .build();

        System.out.printf("token: " + jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String regenerateJwt(String username) {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(username)
                .claim("roles", "USER") // USER is hardcoded but for now it's the only option so works fine as a workaround.
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public StompPrincipal extractPrincipalFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String username = jwt.getClaim("sub");
            System.out.printf("\n STOMP PRINCIPAL USERNAME = " + username + "\n");
            return new StompPrincipal(username);
        } catch (Exception e) {
            throw new SecurityException("Invalid JWT provided", e);
        }
    }

    public Optional<Token> returnTokenByUserID(long userID) { return tokensRepository.findByUserId(userID); }

    @Transactional
    public int deleteTokenData(String token) {
        int result = tokensRepository.deleteByToken(token);
        System.out.printf("DELETETOKENDATA RESULT = :" + result);
        return result;
    }

    public void saveToTable(Token tokenData) {
        //TODO FIX ERRORS WHEN SENDING A TEST LOGIN FROM POSTMAN
        tokensRepository.insertAll(
                tokenData.getUser().getUserId().longValue(),
                tokenData.getToken(),
                tokenData.getExpiryTime());
    }

    public int deleteByUserID(long userID) {
        return tokensRepository.deleteByUserId(userID);
    }

}
