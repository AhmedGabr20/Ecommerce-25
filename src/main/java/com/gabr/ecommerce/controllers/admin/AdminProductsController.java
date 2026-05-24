package com.gabr.ecommerce.controllers.admin;

import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.admin.*;
import com.gabr.ecommerce.service.AdminProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductsController {

    private final AdminProductService adminProductService;

    @Value("${app.base-url}")
    String baseUrl ;

    @GetMapping
    public ApiResponse<Page<AdminProductDto>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        return ApiResponse.success("OK",
                adminProductService.list(q, categoryId, active, page, size, sortBy, dir)
        );
    }

    @PostMapping
    public ApiResponse<AdminProductDto> create(@Valid @RequestBody ProductUpsertRequest req) {
        return ApiResponse.success("Created", adminProductService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminProductDto> update(@PathVariable Long id, @Valid @RequestBody ProductUpsertRequest req) {
        return ApiResponse.success("Updated", adminProductService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminProductService.delete(id);
        return ApiResponse.success("Deleted", null);
    }
}

