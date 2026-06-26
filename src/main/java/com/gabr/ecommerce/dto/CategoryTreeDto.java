package com.gabr.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryTreeDto {

    private Long id;

    private String nameAr;
    private String nameEn;

    private String slug;

    private Long parentId;

    private Integer level;

    private Boolean active;

    private Integer productCount;

    private String imageUrl;

    private List<CategoryTreeDto> children;
}