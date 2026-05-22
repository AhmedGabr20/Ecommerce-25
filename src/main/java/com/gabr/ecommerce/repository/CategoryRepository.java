package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.entity.Category;
import com.gabr.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameArContainingIgnoreCaseOrNameEnContainingIgnoreCase(String nameAr, String nameEn);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.parent")
    List<Category> findAllWithParent();

}
