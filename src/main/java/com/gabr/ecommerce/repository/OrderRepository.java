package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.dto.admin.RevenuePointDto;
import com.gabr.ecommerce.dto.admin.TopProductDto;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gabr.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

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
             where o.status = com.gabr.ecommerce.constant.OrderStatus.PAID
             AND o.createdAt >= :from
             AND o.createdAt < :to 
             group by cast(o.createdAt as date)
             order by cast(o.createdAt as date)      
    """)
    List<RevenuePointDto> revenueByDay (@Param("from")LocalDateTime from, @Param("to")LocalDateTime to);

    @Query("""
            select new com.gabr.ecommerce.dto.admin.TopProductDto(
            p.id,
            p.name,
            coalesce(sum(oi.quantity),0),
            coalesce(sum(oi.price),0)
            )
            from OrderItem oi 
            join oi.order o 
            join oi.product p
            where o.status = com.gabr.ecommerce.constant.OrderStatus.PAID
            AND o.createdAt >= :from
            AND o.createdAt < :to  
            group by p.id , p.name
            ORDER BY SUM(oi.quantity) desc 
""")
    List<TopProductDto> topProducts(@Param("from") LocalDateTime from, @Param("to")LocalDateTime to);

    List<Order> findByUserId(int userId);
}
