package com.beauty.cart.exception;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
