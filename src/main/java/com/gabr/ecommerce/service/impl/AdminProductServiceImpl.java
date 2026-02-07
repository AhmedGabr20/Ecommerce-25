package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.admin.*;
import com.gabr.ecommerce.entity.Category;
import com.gabr.ecommerce.entity.Product;
import com.gabr.ecommerce.repository.CategoryRepository;
import com.gabr.ecommerce.repository.ProductRepository;
import com.gabr.ecommerce.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<AdminProductDto> list(String q, Long categoryId, Boolean active, int page, int size, String sortBy, String dir){
    Sort sort = "desc".equalsIgnoreCase(dir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        String query = (q == null) ? "" : q.trim();
        return productRepository.adminSearch(query, categoryId, active, pageable)
                .map(this::toDto);
    }

    @Override
    public AdminProductDto create(ProductUpsertRequest req) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product p = Product.builder()
                .nameEn(req.getNameEn())
                .nameAr(req.getNameAr())
                .descriptionEn(req.getDescriptionEn())
                .descriptionAr(req.getDescriptionAr())
                .price(req.getPrice())
                .stock(req.getStock())
                .sku(req.getSku())
                .slug(req.getSlug())
                .brand(req.getBrand())
                .currency(req.getCurrency())
                .active(req.getActive())
                .category(category)
                .build();

        return toDto(productRepository.save(p));
    }

    @Override
    public AdminProductDto update(Long id, ProductUpsertRequest req) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        p.setNameEn(req.getNameEn());
        p.setNameAr(req.getNameAr());
        p.setDescriptionEn(req.getDescriptionEn());
        p.setDescriptionAr(req.getDescriptionAr());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        p.setSku(req.getSku());
        p.setSlug(req.getSlug());
        p.setBrand(req.getBrand());
        p.setCurrency(req.getCurrency());
        p.setActive(req.getActive());
        p.setCategory(category);

        return toDto(productRepository.save(p));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    private AdminProductDto toDto(Product p) {
        return AdminProductDto.builder()
                .id(p.getId())
                .nameEn(p.getNameEn())
                .nameAr(p.getNameAr())
                .descriptionEn(p.getDescriptionEn())
                .descriptionAr(p.getDescriptionAr())
                .price(p.getPrice())
                .stock(p.getStock())
                .sku(p.getSku())
                .slug(p.getSlug())
                .brand(p.getBrand())
                .currency(p.getCurrency())
                .active(p.getActive())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

}
