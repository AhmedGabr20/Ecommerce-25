package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.CategoryTreeDto;
import com.gabr.ecommerce.dto.admin.AdminCategoryDto;
import com.gabr.ecommerce.dto.admin.CategoryUpsertRequest;
import com.gabr.ecommerce.entity.Category;
import com.gabr.ecommerce.repository.CategoryRepository;
import com.gabr.ecommerce.service.AdminCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Page<AdminCategoryDto> list(
            String q,
            Boolean active,
            Long parentId,
            int page,
            int size,
            String sortBy,
            String dir
    ) {

        Sort sort = "desc".equalsIgnoreCase(dir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return categoryRepository
                .adminSearch(
                        q == null ? "" : q.trim(),
                        active,
                        parentId,
                        pageable
                )
                .map(this::toDto);
    }

    @Override
    public AdminCategoryDto getById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        return toDto(category);
    }

    @Override
    public AdminCategoryDto create(CategoryUpsertRequest req) {

        Category parent = null;

        if (req.getParentId() != null) {

            parent = categoryRepository.findById(req.getParentId())
                    .orElseThrow(() ->
                            new RuntimeException("Parent category not found"));
        }

        Category category = Category.builder()
                .nameAr(req.getNameAr())
                .nameEn(req.getNameEn())
                .slug(req.getSlug())
                .descriptionAr(req.getDescriptionAr())
                .descriptionEn(req.getDescriptionEn())
                .imageUrl(req.getImageUrl())
                .bannerUrl(req.getBannerUrl())
                .parent(parent)
                .level(req.getLevel())
                .active(req.getActive())
                .sortOrder(req.getSortOrder())
                .metaTitle(req.getMetaTitle())
                .metaDescription(req.getMetaDescription())
                .metaKeywords(req.getMetaKeywords())
                .productCount(0)
                .build();

        return toDto(
                categoryRepository.save(category)
        );
    }

    @Override
    public AdminCategoryDto update(
            Long id,
            CategoryUpsertRequest req
    ) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        Category parent = null;

        if (req.getParentId() != null) {

            if (req.getParentId().equals(id)) {
                throw new RuntimeException(
                        "Category cannot be parent of itself"
                );
            }

            parent = categoryRepository.findById(req.getParentId())
                    .orElseThrow(() ->
                            new RuntimeException("Parent category not found"));
        }

        category.setNameAr(req.getNameAr());
        category.setNameEn(req.getNameEn());
        category.setSlug(req.getSlug());
        category.setDescriptionAr(req.getDescriptionAr());
        category.setDescriptionEn(req.getDescriptionEn());
        category.setImageUrl(req.getImageUrl());
        category.setBannerUrl(req.getBannerUrl());
        category.setParent(parent);
        category.setLevel(req.getLevel());
        category.setActive(req.getActive());
        category.setSortOrder(req.getSortOrder());
        category.setMetaTitle(req.getMetaTitle());
        category.setMetaDescription(req.getMetaDescription());
        category.setMetaKeywords(req.getMetaKeywords());

        return toDto(
                categoryRepository.save(category)
        );
    }

    @Override
    public void delete(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        /*
         * منع حذف Category فيها Sub Categories
         */
        if (categoryRepository.existsByParentId(id)) {
            throw new RuntimeException(
                    "Cannot delete category with child categories"
            );
        }

        /*
         * منع حذف Category فيها Products
         */
        if (category.getProductCount() != null
                && category.getProductCount() > 0) {

            throw new RuntimeException(
                    "Cannot delete category containing products"
            );
        }

        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryTreeDto> getTree() {

        List<Category> categories =
                categoryRepository.findAllWithParent();

        return categories.stream()
                .filter(c -> c.getParent() == null)
                .map(parent -> mapTree(parent, categories))
                .toList();
    }

    private CategoryTreeDto mapTree(
            Category parent,
            List<Category> all
    ) {

        List<CategoryTreeDto> children = all.stream()
                .filter(c ->
                        c.getParent() != null
                                && c.getParent().getId()
                                .equals(parent.getId()))
                .map(c -> mapTree(c, all))
                .toList();

        return CategoryTreeDto.builder()
                .id(parent.getId())
                .nameAr(parent.getNameAr())
                .nameEn(parent.getNameEn())
                .slug(parent.getSlug())

                .parentId(
                        parent.getParent() != null
                                ? parent.getParent().getId()
                                : null
                )

                .level(parent.getLevel())

                .active(parent.getActive())

                .productCount(parent.getProductCount())

                .imageUrl(parent.getImageUrl())

                .children(children)

                .build();
    }

    private AdminCategoryDto toDto(Category c) {

        return AdminCategoryDto.builder()
                .id(c.getId())
                .nameAr(c.getNameAr())
                .nameEn(c.getNameEn())
                .slug(c.getSlug())
                .descriptionAr(c.getDescriptionAr())
                .descriptionEn(c.getDescriptionEn())
                .imageUrl(c.getImageUrl())
                .bannerUrl(c.getBannerUrl())
                .parentId(
                        c.getParent() != null
                                ? c.getParent().getId()
                                : null
                )
                .parentName(
                        c.getParent() != null
                                ? c.getParent().getNameEn()
                                : null
                )
                .level(c.getLevel())
                .active(c.getActive())
                .sortOrder(c.getSortOrder())
                .productCount(c.getProductCount())
                .metaTitle(c.getMetaTitle())
                .metaDescription(c.getMetaDescription())
                .metaKeywords(c.getMetaKeywords())
//                .createdAt(c.getCreatedAt())
//                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
