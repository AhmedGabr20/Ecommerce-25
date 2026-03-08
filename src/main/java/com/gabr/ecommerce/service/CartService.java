package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.AddItemRequest;
import com.gabr.ecommerce.dto.CartDto;

import java.security.Principal;
import java.util.UUID;

public interface CartService {
    CartDto getUserCart(Long userId);
    CartDto getCurrentCart(Principal principal , UUID cartUuid);
    CartDto addItem(Principal principal, UUID  cartUuid, AddItemRequest request);
    CartDto removeItem(Principal principal, UUID  cartUuid, Long productId);
    CartDto clearCart(Principal principal,UUID cartUuid);
}
