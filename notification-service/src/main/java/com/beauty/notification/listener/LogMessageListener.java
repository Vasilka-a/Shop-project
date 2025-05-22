package com.beauty.notification.listener;

import com.beauty.notification.model.LogMessage;
import com.beauty.notification.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogMessageListener {

    private final LogService logService;

    public LogMessageListener(LogService logService) {
        this.logService = logService;
    }


    @KafkaListener(
        topics = "${kafka.topic.log}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(LogMessage logMessage, Acknowledgment ack) {
        try {
            logService.writeLogToFile(logMessage);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing message: {}", logMessage, e);
            // Здесь можно добавить логику повторной обработки или отправки в dead letter queue
            ack.acknowledge(); // В реальном приложении здесь может быть другая логика
        }
    }
} 