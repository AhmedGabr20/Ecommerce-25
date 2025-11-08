package com.gabr.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gabr.ecommerce.entity.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(int userId);
}
