package com.gabr.ecommerce.dto.admin;

import lombok.Data;

@Data
public class CategoryUpsertRequest {

    private String nameAr;
    private String nameEn;
    private String slug;

    private String descriptionAr;
    private String descriptionEn;

    private String imageUrl;
    private String bannerUrl;

    private Long parentId;

    private Integer level;

    private Boolean active;

    private Integer sortOrder;

    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
}