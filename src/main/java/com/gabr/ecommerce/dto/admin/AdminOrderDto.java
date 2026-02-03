package com.gabr.ecommerce.dto.admin;

import com.gabr.ecommerce.constant.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AdminOrderDto {
    private Long id;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private Double totalPrice;
    private OrderStatus status; // NEW/PAID/...
    private List<AdminOrderItemDto> items;
}
