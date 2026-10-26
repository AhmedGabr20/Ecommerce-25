package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.entity.*;
import com.gabr.ecommerce.service.camunda.CamundaOrderProcessService;
import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.dto.OrderDto;
import com.gabr.ecommerce.dto.OrderItemDto;
import com.gabr.ecommerce.exception.BusinessException;
import com.gabr.ecommerce.exception.ErrorCode;
import com.gabr.ecommerce.repository.CartRepository;
import com.gabr.ecommerce.repository.OrderRepository;
import com.gabr.ecommerce.repository.UserRepository;
import com.gabr.ecommerce.service.OrderService;
import com.gabr.ecommerce.service.specifications.OrderSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CamundaOrderProcessService camundaOrderProcessService;


    @Override
    public OrderDto placeOrder(Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        // ✅ Create cart if missing
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .user(user)
                                .totalPrice(BigDecimal.ZERO)
                                .build()
                ));

        if (cart.getItems() == null || cart.getItems().isEmpty())
            throw new BusinessException(ErrorCode.CART_EMPTY);

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);

        // 2. Calculate total from cart items
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            BigDecimal itemTotal = cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            totalPrice = totalPrice.add(itemTotal);

            // 3. Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getUnitPrice());

            // 4. Add item to order collection
            order.getItems().add(orderItem);
        }

        order.setTotalPrice(cart.getTotalPrice());
        Order SavedOrder = orderRepository.save(order);
        camundaOrderProcessService.startOrderProcess(SavedOrder.getId(),SavedOrder.getUser().getEmail(),SavedOrder.getUser().getEmail());

        // 7. Clear cart after successful order creation
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setCoupon(null);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cartRepository.save(cart);

        return toDto(SavedOrder);
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(this::toDto).toList();
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        return orderRepository.findById(orderId).map(this::toDto).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public List<Order> search(OrderStatus status,
                              BigDecimal minAmount,
                              Long customerId) {

        Specification<Order> spec = Specification
                .allOf(
                        OrderSpecifications.hasStatus(status),
                        OrderSpecifications.totalPriceGreaterThan(minAmount),
                        OrderSpecifications.customerIdEquals(customerId)
                );

        return orderRepository.findAll(spec);
    }



    private OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(i -> new OrderItemDto(
                                i.getProduct().getId(),
                                i.getProduct().getNameEn(),
                                i.getQuantity(),
                                i.getPrice()))
                        .toList())
                .build();
    }

}
