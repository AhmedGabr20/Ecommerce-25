package com.gabr.ecommerce.dto.admin;

import com.gabr.ecommerce.constant.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AdminOrderDto {
    private Long id;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private OrderStatus status; // NEW/PAID/...
    private List<AdminOrderItemDto> items;
}
