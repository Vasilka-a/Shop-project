package com.beauty.product.controllerAdvice;

import com.beauty.product.exception.InvalidQuantityException;
import com.beauty.product.exception.ProductNotFoundException;
import com.beauty.product.kafka.producer.KafkaLogProducer;
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
    public ResponseEntity<Error> handlerProductNutFound(ProductNotFoundException e) {
        loggerService.sendLogError("Product-Service", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Error(e.getMessage()));
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<Error> handlerInvalidQuantity(InvalidQuantityException e) {
        loggerService.sendLogError("Product-Service", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(e.getMessage()));
    }

}

