package com.gabr.ecommerce.dto.admin;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AdminProductDto {
    private Long id;

    private String nameEn;
    private String nameAr;

    private String descriptionEn;
    private String descriptionAr;

    private BigDecimal price;
    private Integer stock;

    private String sku;
    private String slug;
    private String brand;
    private String currency;
    private Boolean active;

    private Long categoryId;
    private String categoryName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String imageUrl;
}
