package com.gabr.ecommerce.service;

import com.gabr.ecommerce.entity.OrderItem;

import java.util.List;

public interface EmailService {
    void sendWelcomeEmail(String to, String username);
    void sendOrderConfirmation(String to, Long orderId, Double total, List<OrderItem> items);
}
