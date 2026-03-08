package com.gabr.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto implements Serializable {

    private Long id;

    // New localized fields (recommended)
    private String nameEn;
    private String nameAr;

    private String descriptionEn;
    private String descriptionAr;

    private String sku;
    private String slug;
    private String brand;

    private String currency;
    private Boolean active;

    @NotNull @Min(value = 0, message = "price must be >= 0")
    private BigDecimal price;

    @NotNull @Min(value = 0, message = "stock must be >= 0")
    private Integer stock;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String categoryName;

    // Images
    private String primaryImageUrl;
    private List<ProductImageDto> images;
}
