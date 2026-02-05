package com.gabr.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "product_images",
        indexes = {
                @Index(name = "idx_product_images_product_id", columnList = "product_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "alt_en")
    private String altEn;

    @Column(name = "alt_ar")
    private String altAr;

    @Column(name = "is_primary")
    private Boolean primaryImage = false;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (primaryImage == null) primaryImage = false;
        if (sortOrder == null) sortOrder = 0;
    }
}
