package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminOrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        select o from Order o
        join o.user u
        where (:status is null or o.status = :status)
          and (:username is null or lower(u.username) like lower(concat('%', :username, '%')))
    """)
    Page<Order> search(@Param("status") OrderStatus status,
                       @Param("username") String username,
                       Pageable pageable);


    // تفاصيل Order مع items + product لتفادي Lazy مشاكل + N+1

    @Query("""
        select distinct o from Order o
        join fetch o.user u
        left join fetch o.items i
        left join fetch i.product p
        where o.id = :id
    """)
    Order findDetailsById(@Param("id") Long id);

}
