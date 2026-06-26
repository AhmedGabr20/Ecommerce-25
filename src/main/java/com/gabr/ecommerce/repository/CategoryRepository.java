package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.entity.Category;
import com.gabr.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameArContainingIgnoreCaseOrNameEnContainingIgnoreCase(String nameAr, String nameEn);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.parent")
    List<Category> findAllWithParent();

    @Query("""
            select c
            from Category c
            where
            (:q = '' or
             lower(c.nameEn) like lower(concat('%',:q,'%'))
             or lower(c.nameAr) like lower(concat('%',:q,'%')))
            and
            (:active is null or c.active = :active)
            and
            (:parentId is null or c.parent.id = :parentId)
            """)
    Page<Category> adminSearch(
            @Param("q") String q,
            @Param("active") Boolean active,
            @Param("parentId") Long parentId,
            Pageable pageable
    );

    boolean existsByParentId(Long parentId);

//    @Query("""
//        select c
//        from Category c
//        left join fetch c.parent
//    """)
//    List<Category> findAllWithParent();

}
