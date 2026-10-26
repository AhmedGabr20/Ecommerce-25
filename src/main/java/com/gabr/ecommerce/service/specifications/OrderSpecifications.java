package com.gabr.ecommerce.service.specifications;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class OrderSpecifications {

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> totalPriceGreaterThan(BigDecimal amount) {
        return (root, query, cb) ->
                amount == null ? null : cb.greaterThan(root.get("totalPrice"), amount);
    }

    public static Specification<Order> customerIdEquals(Long customerId) {
        return (root, query, cb) ->
                customerId == null ? null : cb.equal(root.get("customerId"), customerId);
    }
}