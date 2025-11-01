package com.gabr.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto implements Serializable {
    private Long id;

    @NotBlank(message = "name required")
    private String name;

    private String description;

    @NotNull @Min(value = 0, message = "price must be >= 0")
    private Double price;

    @NotNull @Min(value = 0, message = "stock must be >= 0")
    private Integer stock;
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String categoryName;
}

