package com.gabr.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_products_sku", columnNames = "sku"),
                @UniqueConstraint(name = "uk_products_slug", columnNames = "slug")
        },
        indexes = {
                @Index(name = "idx_products_category_id", columnList = "category_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // New localized fields
    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_ar")
    private String nameAr;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_ar", columnDefinition = "TEXT")
    private String descriptionAr;


    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    private Integer stock;

    @Column(length = 64)
    private String sku;

    @Column(length = 255)
    private String slug;

    @Column(length = 120)
    private String brand;

    @Column(length = 10)
    private String currency = "EGP";

    @Column(name = "is_active")
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Images
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    @JsonManagedReference
    private List<ProductImage> images = new ArrayList<>();

    // timestamps
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;

        // set defaults if missing
        if (currency == null) currency = "EGP";
        if (active == null) active = true;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        if (currency == null) currency = "EGP";
        if (active == null) active = true;
    }
}
