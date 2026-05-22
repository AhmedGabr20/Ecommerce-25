package com.gabr.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameAr;
    private String nameEn;

    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(columnDefinition = "TEXT")
    private String descriptionEn;

    private String imageUrl;

    private String bannerUrl;

    private Integer level;

    private Boolean active = true;

    private Integer sortOrder;

    private Integer productCount = 0;

    private String metaTitle;

    private String metaDescription;

    private String metaKeywords;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
}
