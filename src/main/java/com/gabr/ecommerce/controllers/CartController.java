package com.gabr.ecommerce.controllers;

import com.gabr.ecommerce.dto.AddItemRequest;
import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.CartDto;
import com.gabr.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping()
    public ResponseEntity<ApiResponse<CartDto>> getCart(
            @RequestHeader(value = "X-CART-UUID", required = false) UUID cartUuid,
            Principal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cart fetched", cartService.getCurrentCart(principal, cartUuid)));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addItem(
            @RequestHeader(value = "X-CART-UUID", required = false) UUID cartUuid,
            @RequestBody @Valid AddItemRequest request,
            Principal principal
            ) {
        return ResponseEntity.ok(ApiResponse.success("Item added", cartService.addItem(principal, cartUuid, request)));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(
            @RequestHeader(value = "X-CART-UUID", required = false) UUID cartUuid,
            @PathVariable Long productId,
            Principal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success("Item removed", cartService.removeItem(principal, cartUuid,productId)));
    }
    @DeleteMapping()
    public ResponseEntity<ApiResponse<CartDto>> clearCart(
            @RequestHeader(value = "X-CART-UUID", required = false) UUID cartUuid,
            Principal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", cartService.clearCart(principal, cartUuid)));
    }



}
