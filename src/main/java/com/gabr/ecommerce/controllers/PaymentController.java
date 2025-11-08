package com.gabr.ecommerce.controllers;

import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.PaymentDto;
import com.gabr.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDto>> pay(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "CASH") String method) {
        return ResponseEntity.ok(ApiResponse.success(
                "Payment processed successfully",
                paymentService.processPayment(orderId, method)));
    }
}
