package com.beauty.cart.kafka.producer;

import com.beauty.cart.kafka.model.LogMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaLogProducer {
    private final KafkaTemplate<String, com.beauty.cart.kafka.model.LogMessage> kafkaTemplate;
    private final String logTopic;

    public KafkaLogProducer(KafkaTemplate<String, com.beauty.cart.kafka.model.LogMessage> kafkaTemplate,
                            @Value("${kafka.topic.log}") String logTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.logTopic = logTopic;
    }


    public void sendLogInfo(String serviceName, String message) {
        LogMessage logMessage = new LogMessage(
                serviceName,
                "INFO",
                message,
                LocalDateTime.now()
        );
        kafkaTemplate.send(logTopic, logMessage);
    }

    public void sendLogError(String serviceName, String message) {
        LogMessage logMessage = new LogMessage(
                serviceName,
                "ERROR",
                message,
                LocalDateTime.now()
        );
        kafkaTemplate.send(logTopic, logMessage);
    }
}
