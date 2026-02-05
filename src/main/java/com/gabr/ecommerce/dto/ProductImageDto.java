package com.gabr.ecommerce.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDto implements Serializable {
    private Long id;
    private String url;
    private String altEn;
    private String altAr;
    private Boolean primaryImage;
    private Integer sortOrder;
}
