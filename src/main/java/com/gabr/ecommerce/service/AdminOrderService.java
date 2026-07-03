package com.gabr.ecommerce.service;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.dto.admin.AdminOrderDto;
import org.springframework.data.domain.Page;

public interface AdminOrderService {

    Page<AdminOrderDto> list(OrderStatus status, String email, int page, int size, String sortBy, String dir);
    AdminOrderDto details(Long orderId);
    AdminOrderDto updateStatus(Long orderId, OrderStatus newStatus);
}
