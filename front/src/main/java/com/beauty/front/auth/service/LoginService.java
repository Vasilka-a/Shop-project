package com.beauty.front.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class LoginService {
    private final RestTemplate restTemplate;
    private final String gatewayServiceUrl;

    public LoginService(RestTemplate restTemplate, @Value("${service.api-gateway.url}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.gatewayServiceUrl = authServiceUrl;
    }

    public ResponseEntity<String> login(String email, String password) {
        String url = gatewayServiceUrl + "/api/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "email", email,
                "password", password
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(url, request, String.class);
    }

}
