package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.CategoryDto;
import com.gabr.ecommerce.dto.CategoryTreeDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto dto);
    CategoryDto update(Long id, CategoryDto dto);
    CategoryDto getById(Long id);
    void delete(Long id);
    List<CategoryDto> getAll(int page, int size, String sortBy);
    List<CategoryDto> getByName(String name);
    List<CategoryTreeDto> getCategoryTree();
}
