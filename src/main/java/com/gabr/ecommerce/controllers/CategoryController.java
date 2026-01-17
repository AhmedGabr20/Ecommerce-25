package com.gabr.ecommerce.controllers;

import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.CategoryDto;
import com.gabr.ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Category", description = "Category management APIs")
@RequestMapping("/api/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@RequestBody CategoryDto dto) {
        CategoryDto CreatedCategory = categoryService.create(dto);
        return ResponseEntity.created(URI.create("/api/categories/" + CreatedCategory.getId()))
                .body(ApiResponse.success("Category created successfully", CreatedCategory));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> get(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success("Category fetched successfully", categoryService.getById(id)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> update(@PathVariable Long id, @RequestBody CategoryDto dto) {
        CategoryDto updated = categoryService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updated));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        List<CategoryDto> list = categoryService.getAll(page, size, sortBy);
        return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully", list));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> search(@RequestParam String name) {
        List<CategoryDto> list = categoryService.getByName(name);
        return ResponseEntity.ok(ApiResponse.success("Search results", list));
    }

}
