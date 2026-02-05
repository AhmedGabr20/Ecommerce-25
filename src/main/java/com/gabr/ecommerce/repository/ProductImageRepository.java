package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.entity.ProductImage;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Query("""
        select i.url from ProductImage i
        where i.product.id = :productId
        order by i.primaryImage desc, i.sortOrder asc
    """)
    java.util.List<String> findOrderedUrls(@Param("productId") Long productId);
}
