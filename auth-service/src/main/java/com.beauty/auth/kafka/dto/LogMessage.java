package com.beauty.auth.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogMessage {
    private String serviceName;
    private String level;
    private String message;
    private LocalDateTime timestamp;
}

