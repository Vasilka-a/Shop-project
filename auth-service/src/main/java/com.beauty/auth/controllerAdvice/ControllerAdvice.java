package com.beauty.auth.controllerAdvice;

import com.beauty.auth.kafka.producer.KafkaLogProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;


@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    private final KafkaLogProducer loggerService;

    public ControllerAdvice(KafkaLogProducer loggerService) {
        this.loggerService = loggerService;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handlerProductNutFound(BadCredentialsException e) {
        loggerService.sendLogError("Auth-Service", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Error(e.getMessage()));
    }
}

