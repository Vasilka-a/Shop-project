package com.beauty.front.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestProduct{
    private String productName;
    private String productCode;
    private BigDecimal price;
    private String description;
    private int quantity;
    private String productImage;
}
