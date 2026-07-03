package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.AddItemRequest;
import com.gabr.ecommerce.dto.CartDto;
import com.gabr.ecommerce.dto.CartItemDto;
import com.gabr.ecommerce.entity.*;
import com.gabr.ecommerce.repository.CartRepository;
import com.gabr.ecommerce.repository.CouponRepository;
import com.gabr.ecommerce.repository.ProductRepository;
import com.gabr.ecommerce.repository.UserRepository;
import com.gabr.ecommerce.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;



    @Override
    public CartDto getUserCart(Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
        return toDto(cart);
    }

    @Override
    public CartDto getCurrentCart(Principal principal, UUID cartUuid) {
        Cart cart = resolveCart(principal, cartUuid);
        return toDto(cart);
    }

    @Override
    public CartDto addItem(Principal principal, UUID cartUuid, AddItemRequest request) {
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Cart cart = resolveCart(principal, cartUuid);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new IllegalStateException("Product is not active");
        }

        List<CartItem> items = Optional.ofNullable(cart.getItems()).orElse(new ArrayList<>());

        CartItem existing = items.stream()
                .filter(i -> i.getProduct() != null && i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        // 5️⃣ حساب الكمية الجديدة
        int newQty = request.getQuantity();
        if (existing != null) {
            newQty += existing.getQuantity();
        }

        if (product.getStock() < newQty) {
            throw new IllegalStateException("Not enough stock");
        }

        if (existing != null) {
            existing.setQuantity(newQty);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            items.add(item);
        }

        cart.setItems(items);
        cart.setTotalPrice(calculateTotal(cart));

        cartRepository.save(cart);

        return toDto(cart);
    }

    @Override
    public CartDto removeItem(Principal principal, UUID  cartUuid, Long productId) {
        Cart cart = resolveCart(principal, cartUuid);

        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));

        cart.setTotalPrice(calculateTotal(cart));

     //   publishCartEvent(cart);

        return toDto(cart);
    }

    @Override
    public CartDto clearCart(Principal principal,UUID  cartUuid) {
        Cart cart = resolveCart(principal, cartUuid);

        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);

     //   publishCartEvent(cart);

        return toDto(cart);
    }

    private CartDto toDto(Cart cart) {
        List<CartItemDto> itemDtos = (cart.getItems() == null)
                ? Collections.emptyList()
                : cart.getItems().stream()
                .map(i -> new CartItemDto(
                        i.getProduct().getId(),
                        i.getProduct().getNameEn(),
                        i.getQuantity(),
                        i.getUnitPrice()))
                .collect(Collectors.toList());

        return CartDto.builder()
                .id(cart.getId())
                .cartUuid(cart.getCartUuid())
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .totalPrice(cart.getTotalPrice())
                .items(itemDtos)
                .build();
    }

    @Transactional
    public void mergeGuestCart(UUID  guestCartUuid, Long userId) {

        Cart guestCart = cartRepository.findByCartUuid(guestCartUuid)
                .orElseThrow(() -> new EntityNotFoundException("Guest cart not found"));

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Cart userCart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .user(user)
                                .cartUuid(UUID.randomUUID())
                                .build()
                ));

        for (CartItem guestItem : guestCart.getItems()) {

            CartItem existing = userCart.getItems()
                    .stream()
                    .filter(i -> i.getProduct().getId().equals(guestItem.getProduct().getId()))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + guestItem.getQuantity());
            } else {
                guestItem.setCart(userCart);
                userCart.getItems().add(guestItem);
            }
        }

        cartRepository.delete(guestCart);

        userCart.setTotalPrice(calculateTotal(userCart));
    }

    private Cart getOrCreateCart(UUID  cartUuid) {

        if (cartUuid != null) {
            return cartRepository.findByCartUuid(cartUuid)
                    .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        }

        Cart cart = Cart.builder()
                .cartUuid(UUID.randomUUID())
                .totalPrice(BigDecimal.ZERO)
                .build();

        return cartRepository.save(cart);
    }

    public CartDto applyCoupon(UUID  cartUuid, String code) {

        Cart cart = cartRepository.findByCartUuid(cartUuid)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));

        if (!coupon.getActive() || coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Coupon expired");
        }

        cart.setCoupon(coupon);

        BigDecimal discount = cart.getTotalPrice()
                .multiply(coupon.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100));

        cart.setDiscountAmount(discount);

        cart.setTotalPrice(cart.getTotalPrice().subtract(discount));

        return toDto(cart);
    }

    private BigDecimal calculateTotal(Cart cart) {
        return cart.getItems().stream()
                .map(item ->
                        item.getUnitPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Cart resolveCart(Principal principal, UUID  cartUuid) {

        // Logged-in user
        if (principal != null) {

            AppUser user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            Cart userCart = cartRepository.findByUser(user).orElse(null);
            Cart guestCart = cartUuid != null
                    ? cartRepository.findByCartUuid(cartUuid).orElse(null)
                    : null;

            if (userCart == null && guestCart != null) {
                guestCart.setUser(user);
                return cartRepository.save(guestCart);
            }
            if (userCart != null && guestCart != null && !userCart.getId().equals(guestCart.getId())) {
                mergeGuestCart(cartUuid, user.getId());
                return cartRepository.save(userCart);
            }
            if (userCart != null) return userCart;
            return cartRepository.save(
                    Cart.builder()
                            .user(user)
                            .cartUuid(UUID.randomUUID())
                            .build()
            );
        }

        // Guest user
        if (cartUuid != null) {
            return cartRepository.findByCartUuid(cartUuid)
                    .orElseGet(() ->
                            cartRepository.save(
                                    Cart.builder()
                                            .cartUuid(UUID.randomUUID())
                                            .totalPrice(BigDecimal.ZERO)
                                            .build()
                            )
                    );
        }

        // Create new guest cart
        return cartRepository.save(
                Cart.builder()
                        .cartUuid(UUID.randomUUID())
                        .totalPrice(BigDecimal.ZERO)
                        .build()
        );
    }

}
