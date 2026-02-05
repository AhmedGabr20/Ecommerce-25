package com.gabr.ecommerce.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopProductDto {
    private Long productId;
    private String nameEn;
    private String nameAr;
    private long totalQty;
    private double totalRevenue;
}
