package com.gabr.ecommerce.dto.admin;

import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AdminProductDto {
    private Long id;

    private String nameEn;
    private String nameAr;

    private String descriptionEn;
    private String descriptionAr;

    private Double price;
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
}
