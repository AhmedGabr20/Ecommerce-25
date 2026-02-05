package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameEnContainingIgnoreCaseOrNameArContainingIgnoreCase(
            String nameEn, String nameAr
    );

    // ✅ details: fetch images + category
    @Query("""
        select distinct p from Product p
        left join fetch p.images i
        left join fetch p.category c
        where p.id = :id
    """)
    Optional<Product> findDetailsById(@Param("id") Long id);

    // ✅ list: join category فقط (بدون images) عشان الـ dto يجيب categoryName بدون lazy
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAll(Pageable pageable);

    // ✅ search: join category فقط (بدون images)
    @EntityGraph(attributePaths = {"category"})
    List<Product> findByNameEnContainingIgnoreCaseOrNameArContainingIgnoreCase(
            String nameEn, String nameAr, Pageable pageable
    );

}