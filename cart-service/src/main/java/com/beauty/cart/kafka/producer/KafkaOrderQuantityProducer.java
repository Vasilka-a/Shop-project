package com.beauty.cart.kafka.producer;

import com.beauty.cart.kafka.model.OrderMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaOrderQuantityProducer {
    private final KafkaTemplate<String, OrderMessage> kafkaTemplate;
    private final String updateCountTopic;

    public KafkaOrderQuantityProducer(KafkaTemplate<String, OrderMessage> kafkaTemplate,
                                      @Value("${kafka.topic.product.update}") String updateCountTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.updateCountTopic = updateCountTopic;
    }

    public void sendMessage(String productCode, int quantity) {
        OrderMessage orderMessage = new OrderMessage(productCode, quantity);
        kafkaTemplate.send(updateCountTopic, orderMessage);
    }
}
