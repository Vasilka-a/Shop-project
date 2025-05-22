package com.beauty.front.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
  //  private Long id;
    private String productCode;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private String productImage;

}
