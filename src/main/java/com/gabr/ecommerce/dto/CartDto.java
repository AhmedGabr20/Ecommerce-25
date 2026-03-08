package com.gabr.ecommerce.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDto {
    private Long id;
    private Long userId;
    private BigDecimal totalPrice;
    private UUID cartUuid;
    private List<CartItemDto> items;
}
