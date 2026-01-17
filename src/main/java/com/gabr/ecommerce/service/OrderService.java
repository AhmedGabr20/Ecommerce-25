package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.OrderDto;
import java.util.List;

public interface OrderService {
    OrderDto placeOrder(int userId);
    List<OrderDto> getUserOrders(int userId);
    OrderDto getOrderById(Long orderId);
}
