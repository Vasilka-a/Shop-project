package com.beauty.notification.listener;

import com.beauty.notification.model.LogMessage;
import com.beauty.notification.service.LogService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LogMessageListener {

    private final LogService logService;

    public LogMessageListener(LogService logService) {
        this.logService = logService;
    }


    @KafkaListener(topics = "${kafka.topic.log}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(LogMessage logMessage) {
        logService.writeLogToFile(logMessage);
    }
} 