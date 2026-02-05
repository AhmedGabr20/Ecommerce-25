package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.OrderDto;
import java.util.List;

public interface OrderService {
    OrderDto placeOrder(Long userId);
    List<OrderDto> getUserOrders(Long userId);
    OrderDto getOrderById(Long orderId);
}
