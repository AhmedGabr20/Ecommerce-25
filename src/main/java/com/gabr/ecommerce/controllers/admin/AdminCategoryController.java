package com.gabr.ecommerce.controllers.admin;

import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.CategoryTreeDto;
import com.gabr.ecommerce.dto.admin.AdminCategoryDto;
import com.gabr.ecommerce.dto.admin.CategoryUpsertRequest;
import com.gabr.ecommerce.service.AdminCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @GetMapping
    public ApiResponse<Page<AdminCategoryDto>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String dir
    ) {

        return
                ApiResponse.success(
                        "Categories fetched successfully",
                        adminCategoryService.list(
                                q,
                                active,
                                parentId,
                                page,
                                size,
                                sortBy,
                                dir
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCategoryDto>> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category fetched successfully",
                        adminCategoryService.getById(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminCategoryDto>> create(
            @RequestBody CategoryUpsertRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category created successfully",
                        adminCategoryService.create(request)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCategoryDto>> update(
            @PathVariable Long id,
            @RequestBody CategoryUpsertRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category updated successfully",
                        adminCategoryService.update(id, request)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        adminCategoryService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category deleted successfully",
                        null
                )
        );
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeDto>>> tree() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category tree fetched successfully",
                        adminCategoryService.getTree()
                )
        );
    }
}
