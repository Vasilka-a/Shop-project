package com.beauty.product.kafka.listener;

import com.beauty.product.kafka.model.OrderMessage;
import com.beauty.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class OrderListener {
    private final ProductService productService;

    public OrderListener(ProductService productService) {
        this.productService = productService;
    }

    @KafkaListener(topics = "${kafka.topic.product.update}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderMessage(OrderMessage orderMessage ) {
        productService.updateQuantityAfterBought(orderMessage.getProductCode(), orderMessage.getQuantity());
    }

}