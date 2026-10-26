package com.gabr.ecommerce.dto.admin;

import lombok.*;

import java.math.BigDecimal;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AdminOrderItemDto {
    private Long id;
    private Long productId;
    private String productNameAr;
    private String productNameEn;
    private int quantity;
    private BigDecimal price;
}
