package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.CartDto;

public interface CartService {
    CartDto getUserCart(int userId);
    CartDto addItem(int userId, Long productId, int quantity);
    CartDto removeItem(int userId, Long productId);
    CartDto clearCart(int userId);
}
