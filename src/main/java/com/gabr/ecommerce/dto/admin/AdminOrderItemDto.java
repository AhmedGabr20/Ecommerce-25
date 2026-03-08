package com.gabr.ecommerce.dto.admin;

import lombok.*;

import java.math.BigDecimal;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AdminOrderItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
}
