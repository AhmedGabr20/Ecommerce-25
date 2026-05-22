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

    private List<CategoryTreeDto> children;

}