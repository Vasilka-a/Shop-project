package com.beauty.auth.service;

import com.beauty.auth.entity.User;
import com.beauty.auth.repository.UserRepository;
import com.beauty.auth.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    private final static String TOKEN = "jwt-token";

    private UserRepository userRepository;
    private JwtUtils jwtUtils;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtUtils = mock(JwtUtils.class);
        authService = new AuthService(userRepository, jwtUtils);
    }


    @Test
    void loginShouldReturnTokenIfUserExists() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setPassword("12345");
        user.setRolesSet(new HashSet<>());

        when(userRepository.findByEmail("user@user.com")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(user)).thenReturn(TOKEN);

        String token = authService.login("user@user.com");

        assertEquals(TOKEN, token);
        verify(userRepository, times(1)).findByEmail("user@user.com");
        verify(jwtUtils, times(1)).generateToken(user);
    }

    @Test
    void loginShouldThrowIfUserNotFound() {
        when(userRepository.findByEmail("wrong@user.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login("wrong@user.com"));
        verify(userRepository, times(1)).findByEmail("wrong@user.com");
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void validateTokenShouldReturnTrueForValidToken() {
        String token = "Bearer valid.jwt.token";

        User user = new User();
        user.setEmail("user@user.com");

        // Уст фейк аутентификацию
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("user@user.com", null)
        );

        when(userRepository.findByEmail("user@user.com")).thenReturn(Optional.of(user));
        when(jwtUtils.validateToken("valid.jwt.token", user)).thenReturn(true);

        boolean result = authService.validateToken(token);

        assertTrue(result);
        verify(jwtUtils).validateToken("valid.jwt.token", user);
    }

    @Test
    void validateTokenShouldReturnFalseForInvalidToken() {
        String token = "Bearer invalid.token";

        User user = new User();
        user.setEmail("user@user.com");

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("user@user.com", null)
        );

        when(userRepository.findByEmail("user@user.com")).thenReturn(Optional.of(user));
        when(jwtUtils.validateToken("invalid.token", user)).thenReturn(false);

        boolean result = authService.validateToken(token);

        assertFalse(result);
    }

    @Test
    void validateTokenShouldThrowIfUserNotFound() {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("notfound@user.com", null)
        );

        when(userRepository.findByEmail("notfound@user.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.validateToken("Bearer some.token"));
    }

}
