package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.CartDto;

public interface CartService {
    CartDto getUserCart(Long userId);
    CartDto addItem(Long userId, Long productId, int quantity);
    CartDto removeItem(Long userId, Long productId);
    CartDto clearCart(Long userId);
}
