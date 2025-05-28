package com.beauty.notification.service;

import com.beauty.notification.model.LogMessage;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Service
public class LogService {
    private static final String LOG_FILE_PATH = "logs/services.log";

    public void writeLogToFile(LogMessage logMessage) {
        try (FileWriter fileWriter = new FileWriter(LOG_FILE_PATH, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(logMessage);

        } catch (IOException e) {
            throw new RuntimeException("An IOException occurred: " + e.getMessage());
        }
    }
}
