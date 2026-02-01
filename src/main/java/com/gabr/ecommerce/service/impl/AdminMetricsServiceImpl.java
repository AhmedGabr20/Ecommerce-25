package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.dto.admin.AdminSummaryDto;
import com.gabr.ecommerce.dto.admin.RevenuePointDto;
import com.gabr.ecommerce.dto.admin.TopProductDto;
import com.gabr.ecommerce.repository.OrderRepository;
import com.gabr.ecommerce.repository.UserRepository;
import com.gabr.ecommerce.service.AdminMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMetricsServiceImpl implements AdminMetricsService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public AdminSummaryDto summary() {
        long totalUsers = userRepository.countAll();
        long totalOrders = orderRepository.countAllOrders();
        long paid = orderRepository.countByStatus(OrderStatus.PAID);
        long _new = orderRepository.countByStatus(OrderStatus.NEW);
        long shipped = orderRepository.countByStatus(OrderStatus.SHIPPED);
        long completed = orderRepository.countByStatus(OrderStatus.COMPLETED);
        long canceled = orderRepository.countByStatus(OrderStatus.CANCELED);

        double revenue = orderRepository.sumPaidRevenue();
        double aov = paid == 0 ? 0 : revenue / paid;

        return AdminSummaryDto.builder()
                .totalUsers(totalUsers)
                .totalOrders(totalOrders)
                .paidOrders(paid)
                .newOrders(_new)
                .shippedOrders(shipped)
                .completedOrders(completed)
                .canceledOrders(canceled)
                .totalRevenue(revenue)
                .avgOrderValue(aov)
                .build();
    }

    @Override
    public List<RevenuePointDto> revenue(LocalDate from, LocalDate to) {
        LocalDateTime fromDT = from.atStartOfDay();
        LocalDateTime toDT = to.plusDays(1).atStartOfDay();
        return orderRepository.revenueByDay(fromDT, toDT);
    }

    @Override
    public List<TopProductDto> topProducts(LocalDate from, LocalDate to, int limit) {
        LocalDateTime fromDT = from.atStartOfDay();
        LocalDateTime toDT = to.plusDays(1).atStartOfDay();
        return orderRepository.topProducts(fromDT, toDT).stream().limit(limit).toList();
    }
}
