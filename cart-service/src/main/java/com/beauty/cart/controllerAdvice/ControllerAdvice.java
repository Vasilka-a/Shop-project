package com.beauty.cart.controllerAdvice;

import com.beauty.cart.exception.ProductNotFoundException;
import com.beauty.cart.kafka.producer.KafkaLogProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;


@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    private final KafkaLogProducer loggerService;

    public ControllerAdvice(KafkaLogProducer loggerService) {
        this.loggerService = loggerService;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Error> handlerProductNotFound(ProductNotFoundException e) {
        loggerService.sendLogError("Cart-Service", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Error(e.getMessage()));
    }
}

