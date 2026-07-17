package com.medina.asocDev.Medina.Asociados.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilsTest {

    private JWTUtils jwtUtils;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtils = new JWTUtils("dGhpcyBpcyBhIHRlc3Qgc2VjcmV0IGtleSBmb3IgSkhXIFRva2Vu");
        userDetails = new User("testuser", "password", java.util.Collections.emptyList());
    }

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtUtils.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtUtils.generateToken(userDetails);
        String username = jwtUtils.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void extractExpiration_isInFuture() {
        String token = jwtUtils.generateToken(userDetails);
        Date expiration = jwtUtils.extractExpiration(token);
        assertTrue(expiration.after(new Date(System.currentTimeMillis() - 1000)));
    }

    @Test
    void isValidToken_validToken_returnsTrue() {
        String token = jwtUtils.generateToken(userDetails);
        assertTrue(jwtUtils.isValidToken(token, userDetails));
    }

    @Test
    void isValidToken_wrongUser_returnsFalse() {
        String token = jwtUtils.generateToken(userDetails);
        UserDetails wrongUser = new User("otheruser", "password", java.util.Collections.emptyList());
        assertFalse(jwtUtils.isValidToken(token, wrongUser));
    }

    @Test
    void generateToken_differentUsers_differentTokens() {
        String token1 = jwtUtils.generateToken(userDetails);
        UserDetails user2 = new User("user2", "password", java.util.Collections.emptyList());
        String token2 = jwtUtils.generateToken(user2);
        assertNotEquals(token1, token2);
    }
}
