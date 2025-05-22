package com.beauty.notification.service;

import com.beauty.notification.model.LogMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@Slf4j
@Service
public class LogService {
    private static final String LOG_FILE_PATH = "logs/services.log";

public void writeLogToFile(LogMessage logMessage) {
    try (FileWriter fileWriter = new FileWriter(LOG_FILE_PATH, true);
         PrintWriter printWriter = new PrintWriter(fileWriter)) {
        printWriter.println(logMessage);

    } catch (IOException e) {
        log.error("An IOException occurred: {}", e.getMessage());
    }
}
}
