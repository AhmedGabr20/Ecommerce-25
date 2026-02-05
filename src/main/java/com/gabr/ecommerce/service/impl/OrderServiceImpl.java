package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.dto.OrderDto;
import com.gabr.ecommerce.dto.OrderItemDto;
import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.entity.Cart;
import com.gabr.ecommerce.entity.Order;
import com.gabr.ecommerce.entity.OrderItem;
import com.gabr.ecommerce.exception.BusinessException;
import com.gabr.ecommerce.exception.ErrorCode;
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
    public OrderDto placeOrder(Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        // ✅ Create cart if missing
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .user(user)
                                .totalPrice(0.0)
                                .build()
                ));

        if (cart.getItems() == null || cart.getItems().isEmpty())
            throw new BusinessException(ErrorCode.CART_EMPTY);

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
        //    total += item.getPrice();
            order.getItems().add(orderItem);
            total += item.getPrice() * item.getQuantity();
        }
        order.setTotalPrice(total);
        Order SavedOrder = orderRepository.save(order);
    //    cartRepository.delete(cart);

        // ✅ clear cart instead of deleting it
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
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
