package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.CategoryTreeDto;
import com.gabr.ecommerce.dto.admin.AdminCategoryDto;
import com.gabr.ecommerce.dto.admin.CategoryUpsertRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminCategoryService {

    Page<AdminCategoryDto> list(
            String q,
            Boolean active,
            Long parentId,
            int page,
            int size,
            String sortBy,
            String dir
    );

    AdminCategoryDto getById(Long id);

    AdminCategoryDto create(CategoryUpsertRequest request);

    AdminCategoryDto update(Long id, CategoryUpsertRequest request);

    void delete(Long id);

    List<CategoryTreeDto> getTree();
}
