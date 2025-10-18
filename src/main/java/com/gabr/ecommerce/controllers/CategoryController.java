package com.gabr.ecommerce.controllers;

import com.gabr.ecommerce.dto.CategoryDto;
import com.gabr.ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Category", description = "Category management APIs")
@RequestMapping("/api/category")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto dto) {
        CategoryDto CreatedCategory = categoryService.create(dto);
        return ResponseEntity.created(URI.create("/api/category/" + CreatedCategory.getId())).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> get(@PathVariable Long id){
        return ResponseEntity.ok(categoryService.getById(id));
    }
}
