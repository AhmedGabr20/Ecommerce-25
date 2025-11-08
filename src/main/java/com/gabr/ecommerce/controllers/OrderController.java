package com.gabr.ecommerce.controllers;

import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.OrderDto;
import com.gabr.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}/place")
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(@PathVariable int userId) {
        return ResponseEntity.ok(
                ApiResponse.success("Order placed successfully", orderService.placeOrder(userId)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getUserOrders(@PathVariable int userId) {
        return ResponseEntity.ok(
                ApiResponse.success("User orders fetched", orderService.getUserOrders(userId)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(
                ApiResponse.success("Order fetched", orderService.getOrderById(orderId)));
    }
}
