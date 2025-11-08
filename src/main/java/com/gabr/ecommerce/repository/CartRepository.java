package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(AppUser user);
}
