package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.dto.PaymentDto;
import com.gabr.ecommerce.entity.Order;
import com.gabr.ecommerce.entity.Payment;
import com.gabr.ecommerce.exception.BusinessException;
import com.gabr.ecommerce.exception.ErrorCode;
import com.gabr.ecommerce.repository.OrderRepository;
import com.gabr.ecommerce.repository.PaymentRepository;
import com.gabr.ecommerce.service.EmailService;
import com.gabr.ecommerce.service.PaymentService;
import com.gabr.ecommerce.service.camunda.CamundaOrderProcessService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final CamundaOrderProcessService camundaOrderProcessService;


    @Override
    public PaymentDto processPayment(Long orderId, String method) {
        if (paymentRepository.existsByOrderId(orderId)) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        // simulate success (you can expand to actual integration later)
        if (order.getStatus() == OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_PAID);
        }

        Payment payment = Payment.builder()
                .order(order)
                .method(method)
                .status("SUCCESS")
                .amount(order.getTotalPrice())
                .paymentDate(LocalDateTime.now())
                .build();

        Payment save = paymentRepository.save(payment);
        // update order status
        camundaOrderProcessService.paymentReceived(order.getId());
        //order.setStatus(OrderStatus.PAID);
        //orderRepository.save(order);
        // send email notification
        emailService.sendOrderConfirmation(order.getUser().getUsername(), order.getId(), order.getTotalPrice(),order.getItems());
        return PaymentDto.builder()
                .id(save.getId())
                .orderId(save.getOrder().getId())
                .status(save.getStatus())
                .method(save.getMethod())
                .amount(save.getAmount())
                .paymentDate(save.getPaymentDate())
                .build();
    }
}
