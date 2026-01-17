package com.gabr.ecommerce.dto;

import com.gabr.ecommerce.entity.Cart;
import com.gabr.ecommerce.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDto {
    private Long id;
    private String productName;
    private int quantity;
    private double price;
}
