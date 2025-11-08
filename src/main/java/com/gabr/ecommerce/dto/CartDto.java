package com.gabr.ecommerce.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDto {
    private Long id;
    private Long userId;
    private Double totalPrice;
    private List<CartItemDto> items;
}
