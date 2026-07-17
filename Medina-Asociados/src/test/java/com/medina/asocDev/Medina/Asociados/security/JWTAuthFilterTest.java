package com.medina.asocDev.Medina.Asociados.security;

import com.medina.asocDev.Medina.Asociados.entity.TokenBlacklisted;
import com.medina.asocDev.Medina.Asociados.repo.TokenBlacklistedRepository;
import com.medina.asocDev.Medina.Asociados.service.CustomUserDetailsService;
import com.medina.asocDev.Medina.Asociados.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTAuthFilterTest {

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private TokenBlacklistedRepository tokenBlacklistedRepository;

    @InjectMocks
    private JWTAuthFilter jwtAuthFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_continuesWithoutAuth() throws ServletException, IOException {
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_blankAuthorizationHeader_continuesWithoutAuth() throws ServletException, IOException {
        request.addHeader("Authorization", "");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_invalidToken_continuesWithoutAuth() throws ServletException, IOException {
        String invalidToken = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + invalidToken);

        when(jwtUtils.extractUsername(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws ServletException, IOException {
        String validToken = "valid.jwt.token";
        String userEmail = "test@example.com";
        request.addHeader("Authorization", "Bearer " + validToken);

        UserDetails userDetails = new User(userEmail, "password", java.util.Collections.emptyList());

        when(jwtUtils.extractUsername(validToken)).thenReturn(userEmail);
        when(tokenBlacklistedRepository.findByToken(validToken)).thenReturn(Optional.empty());
        when(customUserDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtUtils.isValidToken(validToken, userDetails)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userEmail, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_blacklistedToken_continuesWithoutAuth() throws ServletException, IOException {
        String blacklistedToken = "blacklisted.jwt.token";
        String userEmail = "blacklisted@example.com";
        request.addHeader("Authorization", "Bearer " + blacklistedToken);

        when(jwtUtils.extractUsername(blacklistedToken)).thenReturn(userEmail);
        when(tokenBlacklistedRepository.findByToken(blacklistedToken)).thenReturn(Optional.of(new TokenBlacklisted()));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
