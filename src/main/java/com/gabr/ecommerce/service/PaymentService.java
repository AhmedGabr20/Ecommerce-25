package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.PaymentDto;

public interface PaymentService {
    PaymentDto processPayment(Long orderId, String method);
}
