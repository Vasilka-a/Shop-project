package com.beauty.auth.controller;

import com.beauty.auth.dto.AuthRequest;
import com.beauty.auth.kafka.producer.KafkaLogProducer;
import com.beauty.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KafkaLogProducer loggerService;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(KafkaLogProducer loggerService, AuthService authService, AuthenticationManager authenticationManager) {
        this.loggerService = loggerService;
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = authService.login(request.getEmail());

            loggerService.sendLogInfo("Auth-service", String.format("%s successful authenticated", request.getEmail()));
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
        } catch (AuthenticationException e) {
            loggerService.sendLogError("Auth-service", String.format("Failed login attempt for user: %s", request.getEmail()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        boolean isValid = authService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok("Token is valid");
        } else {
            loggerService.sendLogError("Auth-service", "Token validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validation failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}


