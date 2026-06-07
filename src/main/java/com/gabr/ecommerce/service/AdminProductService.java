package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.admin.AdminProductDto;
import com.gabr.ecommerce.dto.admin.ProductUpsertRequest;
import org.springframework.data.domain.Page;

public interface AdminProductService {
    public Page<AdminProductDto> list(String q, Long categoryId, Boolean active, int page, int size, String sortBy, String dir);
    AdminProductDto create(ProductUpsertRequest req);
    AdminProductDto update(Long id, ProductUpsertRequest req);
    void delete(Long id);
    byte[] exportPdf(
            String q,
            Long categoryId,
            Boolean active
    );
}
