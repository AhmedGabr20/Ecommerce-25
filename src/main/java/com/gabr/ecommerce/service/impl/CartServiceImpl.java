package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.CartDto;
import com.gabr.ecommerce.dto.CartItemDto;
import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.entity.Cart;
import com.gabr.ecommerce.entity.CartItem;
import com.gabr.ecommerce.entity.Product;
import com.gabr.ecommerce.repository.CartRepository;
import com.gabr.ecommerce.repository.ProductRepository;
import com.gabr.ecommerce.repository.UserRepository;
import com.gabr.ecommerce.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;



    @Override
    public CartDto getUserCart(int userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
        return toDto(cart);
    }

    @Override
    public CartDto addItem(int userId, Long productId, int quantity) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
        CartItem existing = cart.getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).findFirst().orElse(null);
        if(existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setPrice(existing.getQuantity() * product.getPrice());
        }else {
            CartItem item = CartItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(quantity * product.getPrice())
                    .cart(cart)
                    .build();
            cart.getItems().add(item);
        }
        cart.setTotalPrice(cart.getItems().stream().mapToDouble(CartItem::getPrice).sum());
        return toDto(cart);
    }

    @Override
    public CartDto removeItem(int userId, Long productId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        cart.setTotalPrice(cart.getItems().stream().mapToDouble(CartItem::getPrice).sum());

        return toDto(cart);
    }

    @Override
    public CartDto clearCart(int userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        return toDto(cart);
    }

    private CartDto toDto(Cart cart) {
        List<CartItemDto> itemDtos = (cart.getItems() == null)
                ? Collections.emptyList()
                : cart.getItems().stream()
                .map(i -> new CartItemDto(
                        i.getProduct().getId(),
                        i.getProduct().getName(),
                        i.getQuantity(),
                        i.getPrice()))
                .collect(Collectors.toList());

        return CartDto.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .totalPrice(cart.getTotalPrice())
                .items(itemDtos)
                .build();
    }

}
