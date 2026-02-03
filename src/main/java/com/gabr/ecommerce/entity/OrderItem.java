package com.gabr.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@ToString(exclude = {"order"})
@EqualsAndHashCode(exclude = {"order"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private double price;
}
