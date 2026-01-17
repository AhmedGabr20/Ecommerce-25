package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.CategoryDto;
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
        log.info("Creating Category: {}", dto.getName());
        Category savedCategory = categoryRepository.save(toCategory(dto));
        log.info("Created Category id={}", savedCategory.getId());
        return toCategoryDto(savedCategory);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        category.setName(dto.getName());
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
                .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                .map(this::toCategoryDto)
                .toList();
    }

    private CategoryDto toCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }
    private Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        return category;
    }
}
