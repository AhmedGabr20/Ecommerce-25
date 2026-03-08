package com.gabr.ecommerce.service;

import com.gabr.ecommerce.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface EmailService {
    void sendWelcomeEmail(String to, String username);
    void sendOrderConfirmation(String to, Long orderId, BigDecimal total, List<OrderItem> items);
    void sendReminderEmail(String to, String username, Long orderId, String approvalUrl, Double orderTotal);
}
