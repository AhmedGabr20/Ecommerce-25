package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.dto.admin.RevenuePointDto;
import com.gabr.ecommerce.dto.admin.TopProductDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gabr.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface OrderRepository extends
        JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {

    long countByStatus(OrderStatus status);

    @Query("select count (o) from Order o")
    long countAllOrders();

    @Query("select coalesce(sum(o.totalPrice),0) from Order o where o.status = com.gabr.ecommerce.constant.OrderStatus.PAID ")
    double sumPaidRevenue();

    @Query("""
             select new com.gabr.ecommerce.dto.admin.RevenuePointDto(
               cast(o.createdAt as date),
               coalesce(sum(o.totalPrice),0.0) 
             )
             from Order o
             where o.status in (com.gabr.ecommerce.constant.OrderStatus.PAID , com.gabr.ecommerce.constant.OrderStatus.COMPLETED)
             AND o.createdAt >= :from
             AND o.createdAt < :to 
             group by cast(o.createdAt as date)
             order by cast(o.createdAt as date)      
    """)
    List<RevenuePointDto> revenueByDay (@Param("from")LocalDateTime from, @Param("to")LocalDateTime to);

    @Query("""
    SELECT new com.gabr.ecommerce.dto.admin.TopProductDto(
        p.id,
        p.nameEn,
        p.nameAr,
        COALESCE(SUM(oi.quantity), 0L),
        SUM(oi.price)
    )
    FROM OrderItem oi
    JOIN oi.order o
    JOIN oi.product p
    where o.status in (com.gabr.ecommerce.constant.OrderStatus.PAID , com.gabr.ecommerce.constant.OrderStatus.COMPLETED)
      AND o.createdAt >= :from
      AND o.createdAt < :to
    GROUP BY p.id, p.nameEn, p.nameAr
    ORDER BY SUM(oi.quantity) DESC
""")
    List<TopProductDto> topProducts(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

//    @EntityGraph(
//            attributePaths = {
//                    "user",
//                    "items",
//                    "items.product"
//            },
//            type = EntityGraph.EntityGraphType.FETCH
//    )
    List<Order> findByUserId(Long userId);

//    @EntityGraph(
//            attributePaths = {
//                    "user",
//                    "items",
//                    "items.product"
//            },
//            type = EntityGraph.EntityGraphType.FETCH
//    )
    @Override
    Optional<Order> findById(Long id);
}
