package com.beauty.auth.service;

import com.beauty.auth.entity.User;
import com.beauty.auth.repository.UserRepository;
import com.beauty.auth.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    private final static String TOKEN = "jwt-token";

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtils jwtUtils;
    @InjectMocks
    private AuthService authService;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@user.user")
                .password("12345")
                .rolesSet(new HashSet<>())
                .build();
    }

    @Test
    void loginForRegisteredUsers() {
        when(userRepository.findByEmail("user@user.user")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(user)).thenReturn(TOKEN);

        String token = authService.login("user@user.user");

        assertEquals(TOKEN, token);
        verify(userRepository, times(1)).findByEmail("user@user.user");
        verify(jwtUtils, times(1)).generateToken(user);
    }

    @Test
    void loginForUnregisteredUsers() {
        when(userRepository.findByEmail("wrong@user.user")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login("wrong@user.user"));
        verify(userRepository, times(1)).findByEmail("wrong@user.user");
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void validateTokenForValidToken() {
        String token = "Bearer " + TOKEN;

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("user@user.user", null)
        );

        when(userRepository.findByEmail("user@user.user")).thenReturn(Optional.of(user));
        when(jwtUtils.validateToken(TOKEN, user)).thenReturn(true);

        boolean result = authService.validateToken(token);

        assertTrue(result);
        verify(jwtUtils).validateToken(TOKEN, user);
    }

    @Test
    void validateTokenForInvalidToken() {
        String token = "Bearer invalid.token";

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("user@user.user", null)
        );

        when(userRepository.findByEmail("user@user.user")).thenReturn(Optional.of(user));
        when(jwtUtils.validateToken("invalid.token", user)).thenReturn(false);

        boolean result = authService.validateToken(token);

        assertFalse(result);
    }
}
