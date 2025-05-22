package com.beauty.cart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private String productCode;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private String productImage;
}
