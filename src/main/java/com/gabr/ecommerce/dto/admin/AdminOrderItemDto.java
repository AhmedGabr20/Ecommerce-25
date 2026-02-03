package com.gabr.ecommerce.dto.admin;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AdminOrderItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
}
