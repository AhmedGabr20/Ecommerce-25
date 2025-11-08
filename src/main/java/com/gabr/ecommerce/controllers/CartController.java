package com.gabr.ecommerce.controllers;

import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.CartDto;
import com.gabr.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartDto>> getCart(@PathVariable int userId) {
        return ResponseEntity.ok(ApiResponse.success("Cart fetched", cartService.getUserCart(userId)));
    }

    @PostMapping("/{userId}/add/{productId}")
    public ResponseEntity<ApiResponse<CartDto>> addItem(
            @PathVariable int userId,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(ApiResponse.success("Item added", cartService.addItem(userId, productId, quantity)));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(@PathVariable int userId, @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Item removed", cartService.removeItem(userId, productId)));
    }
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<ApiResponse<CartDto>> clearCart(@PathVariable int userId) {
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", cartService.clearCart(userId)));
    }



}
