package com.gabr.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID cartUuid;

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        lastModifiedAt = LocalDateTime.now();
    }
}

