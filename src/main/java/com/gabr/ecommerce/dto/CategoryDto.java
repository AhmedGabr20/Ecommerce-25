package com.gabr.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {

    private Long id;

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

    private Integer productCount;

    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;

}
