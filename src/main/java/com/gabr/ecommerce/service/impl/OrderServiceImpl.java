package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.dto.OrderDto;
import com.gabr.ecommerce.dto.OrderItemDto;
import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.entity.Cart;
import com.gabr.ecommerce.entity.Order;
import com.gabr.ecommerce.entity.OrderItem;
import com.gabr.ecommerce.repository.CartRepository;
import com.gabr.ecommerce.repository.OrderRepository;
import com.gabr.ecommerce.repository.UserRepository;
import com.gabr.ecommerce.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;


    @Override
    public OrderDto placeOrder(int userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        if (cart.getItems().isEmpty())
            throw new EntityNotFoundException("Cart is empty");

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);

        double total = 0.0;
        for (var item : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(item.getProduct())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build();
            total += item.getPrice();
            order.getItems().add(orderItem);
        }
        order.setTotalPrice(total);
        Order SavedOrder = orderRepository.save(order);
        cartRepository.delete(cart);

        return toDto(SavedOrder);
    }

    @Override
    public List<OrderDto> getUserOrders(int userId) {
        return orderRepository.findByUserId(userId).stream().map(this::toDto).toList();
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        return orderRepository.findById(orderId).map(this::toDto).orElseThrow(() -> new EntityNotFoundException("Order not found"));
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
                                i.getProduct().getName(),
                                i.getQuantity(),
                                i.getPrice()))
                        .toList())
                .build();
    }

}
