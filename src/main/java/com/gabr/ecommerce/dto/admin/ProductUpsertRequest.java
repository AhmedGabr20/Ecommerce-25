package com.gabr.ecommerce.dto.admin;

import com.gabr.ecommerce.dto.ProductImageDto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ProductUpsertRequest {

    @NotBlank
    private String nameEn;

    @NotBlank
    private String nameAr;

    private String descriptionEn;
    private String descriptionAr;

    @NotNull @Positive
    private BigDecimal price;

    @NotNull @Min(0)
    private Integer stock;

    @NotNull
    private Long categoryId;

    @Size(max = 64)
    private String sku;

    @Size(max = 255)
    private String slug;

    @Size(max = 120)
    private String brand;

    @Size(max = 10)
    private String currency; // default "EGP" in entity

    private Boolean active; // default true in entity

    // Images
    private String primaryImageUrl;
    private List<ProductImageDto> images;
}
