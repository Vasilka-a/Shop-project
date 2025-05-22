package com.beauty.front.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigRestTemplate {
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder templateBuilder) {
        return templateBuilder.build();
    }
}
