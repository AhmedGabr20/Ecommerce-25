package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.dto.admin.AdminOrderDto;
import com.gabr.ecommerce.dto.admin.AdminOrderItemDto;
import com.gabr.ecommerce.entity.Order;
import com.gabr.ecommerce.exception.BusinessException;
import com.gabr.ecommerce.exception.ErrorCode;
import com.gabr.ecommerce.repository.AdminOrderRepository;
import com.gabr.ecommerce.service.AdminOrderService;
import com.gabr.ecommerce.service.camunda.CamundaOrderProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final AdminOrderRepository adminOrderRepository;
    private final CamundaOrderProcessService camundaOrderProcessService;


    @Override
    @Transactional(readOnly = true)
    public Page<AdminOrderDto> list(OrderStatus status, String username, int page, int size, String sortBy, String dir) {
        Sort sort = "desc".equalsIgnoreCase(dir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        OrderStatus s = (status == null ) ? null : status;
        String u = (username == null || username.isBlank()) ? null : username;

        Page<Order> orders = adminOrderRepository.search(s,u,pageable);
        return orders.map(this::toDtoBasic);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminOrderDto details(Long orderId) {
        Order order = adminOrderRepository.findDetailsById(orderId);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);

        return toDtoFull(order);
    }


    @Override
    @Transactional
    public AdminOrderDto updateStatus(Long orderId, OrderStatus newStatus) {
        Order o = adminOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        OrderStatus current = o.getStatus();
        //  لو Admin اختار SHIPPED وهو currently PAID -> ده Approve Shipment
        if (OrderStatus.PAID.equals(current) && OrderStatus.SHIPPED.equals(newStatus)) {
            camundaOrderProcessService.approveShipment(orderId, true);
            // متعملش update هنا: Worker بتاع external task "order.setStatusShipped" هيحدّث DB
            return toDtoBasic(o);
        }
        //  لو Admin اختار CANCELED وهو currently PAID -> ده Reject Shipment
        if (OrderStatus.PAID.equals(current) && OrderStatus.CANCELED.equals(newStatus)) {
            camundaOrderProcessService.approveShipment(orderId, false);
            // هنا الـ BPMN عندك مسار NO بيروح End مباشرة
            // الأفضل: خلي مسار NO يعمل external task لتحديث CANCELED

            return toDtoBasic(o);
        }
        o.setStatus(newStatus);
        Order saved = adminOrderRepository.save(o);
        return toDtoBasic(saved);
    }

    private AdminOrderDto toDtoBasic(Order o) {
        return AdminOrderDto.builder()
                .id(o.getId())
                .userId(o.getUser() != null ? o.getUser().getId() : null)
                .username(o.getUser() != null ? o.getUser().getUsername() : null)
                .createdAt(o.getCreatedAt())
                .totalPrice(o.getTotalPrice())
                .status(o.getStatus())
                .items(null)
                .build();
    }

    private AdminOrderDto toDtoFull(Order o) {
        return AdminOrderDto.builder()
                .id(o.getId())
                .userId(o.getUser() != null ? o.getUser().getId() : null)
                .username(o.getUser() != null ? o.getUser().getUsername() : null)
                .createdAt(o.getCreatedAt())
                .totalPrice(o.getTotalPrice())
                .status(o.getStatus())
                .items(o.getItems().stream().map(i -> AdminOrderItemDto.builder()
                        .id(i.getId())
                        .productId(i.getProduct() != null ? i.getProduct().getId() : null)
                        .productName(i.getProduct() != null ? i.getProduct().getNameEn() : null)
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .build()
                ).collect(Collectors.toList()))
                .build();
    }

}
