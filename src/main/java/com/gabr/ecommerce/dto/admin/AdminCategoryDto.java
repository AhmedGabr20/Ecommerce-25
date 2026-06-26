package com.gabr.ecommerce.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class AdminCategoryDto {

    private Long id;

    private String nameAr;
    private String nameEn;
    private String slug;

    private String descriptionAr;
    private String descriptionEn;

    private String imageUrl;
    private String bannerUrl;

    private Long parentId;
    private String parentName;

    private Integer level;

    private Boolean active;

    private Integer sortOrder;

    private Integer productCount;

    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}