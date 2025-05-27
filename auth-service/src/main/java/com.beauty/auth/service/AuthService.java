package com.beauty.auth.service;

import com.beauty.auth.entity.User;
import com.beauty.auth.repository.UserRepository;
import com.beauty.auth.security.JwtUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepo, JwtUtils jwtUtils) {
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
    }

    public String login(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() ->
                new BadCredentialsException("Email or password does not match, please try again")
        );
        return jwtUtils.generateToken(user);
    }

    public boolean validateToken(String token) {
        String authToken = (token.startsWith("Bearer ")) ? token.substring(7) : null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepo.findByEmail(authentication.getName()).orElseThrow(() ->
                new BadCredentialsException("Email or password does not match")
        );
        return jwtUtils.validateToken(authToken, user);
    }
}
