package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.CategoryDto;
import com.gabr.ecommerce.dto.CategoryTreeDto;
import com.gabr.ecommerce.dto.ProductDto;
import com.gabr.ecommerce.entity.Category;
import com.gabr.ecommerce.repository.CategoryRepository;
import com.gabr.ecommerce.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(CategoryDto dto) {
        Category category = toCategory(dto);

        if(dto.getParentId() != null){
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);

        return toCategoryDto(saved);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        category.setNameAr(dto.getNameAr());
        category.setNameEn(dto.getNameEn());
        category.setSlug(dto.getSlug());
        category.setDescriptionAr(dto.getDescriptionAr());
        category.setDescriptionEn(dto.getDescriptionEn());
        category.setImageUrl(dto.getImageUrl());
        category.setBannerUrl(dto.getBannerUrl());
        category.setLevel(dto.getLevel());
        category.setActive(dto.getActive());
        category.setSortOrder(dto.getSortOrder());
        category.setMetaTitle(dto.getMetaTitle());
        category.setMetaDescription(dto.getMetaDescription());
        category.setMetaKeywords(dto.getMetaKeywords());

        if(dto.getParentId() != null){
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
            category.setParent(parent);
        }

        return toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto getById(Long id) {
        return toCategoryDto(categoryRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Category not found")));
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> getAll(int page, int size, String sortBy) {
        return categoryRepository.findAll(PageRequest.of(page,size, Sort.by(sortBy)))
                .map(this::toCategoryDto).toList();
    }

    @Override
    public List<CategoryDto> getByName(String name) {
        return categoryRepository.findAll().stream()
                .filter(c ->
                        c.getNameAr().toLowerCase().contains(name.toLowerCase()) ||
                                c.getNameEn().toLowerCase().contains(name.toLowerCase())
                )
                .map(this::toCategoryDto)
                .toList();
    }

    @Override
    public List<CategoryTreeDto> getCategoryTree() {

        List<Category> categories = categoryRepository.findAllWithParent();

        return categories.stream()
                .filter(c -> c.getParent() == null)
                .map(parent -> mapTree(parent, categories))
                .toList();
    }
    private CategoryTreeDto mapTree(Category parent, List<Category> all) {

        List<CategoryTreeDto> children = all.stream()
                .filter(c ->
                        c.getParent() != null &&
                                c.getParent().getId().equals(parent.getId()))
                .map(c -> mapTree(c, all))
                .toList();

        return CategoryTreeDto.builder()
                .id(parent.getId())
                .nameAr(parent.getNameAr())
                .nameEn(parent.getNameEn())
                .slug(parent.getSlug())
                .children(children)
                .build();
    }

    private CategoryDto toCategoryDto(Category category) {

        return CategoryDto.builder()
                .id(category.getId())
                .nameAr(category.getNameAr())
                .nameEn(category.getNameEn())
                .slug(category.getSlug())
                .descriptionAr(category.getDescriptionAr())
                .descriptionEn(category.getDescriptionEn())
                .imageUrl(category.getImageUrl())
                .bannerUrl(category.getBannerUrl())
                .level(category.getLevel())
                .active(category.getActive())
                .sortOrder(category.getSortOrder())
                .productCount(category.getProductCount())
                .metaTitle(category.getMetaTitle())
                .metaDescription(category.getMetaDescription())
                .metaKeywords(category.getMetaKeywords())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }
    private Category toCategory(CategoryDto dto) {

        return Category.builder()
                .id(dto.getId())
                .nameAr(dto.getNameAr())
                .nameEn(dto.getNameEn())
                .slug(dto.getSlug())
                .descriptionAr(dto.getDescriptionAr())
                .descriptionEn(dto.getDescriptionEn())
                .imageUrl(dto.getImageUrl())
                .bannerUrl(dto.getBannerUrl())
                .level(dto.getLevel())
                .active(dto.getActive())
                .sortOrder(dto.getSortOrder())
                .productCount(dto.getProductCount())
                .metaTitle(dto.getMetaTitle())
                .metaDescription(dto.getMetaDescription())
                .metaKeywords(dto.getMetaKeywords())
                .build();
    }
}
