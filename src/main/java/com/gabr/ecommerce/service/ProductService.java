package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.ProductDto;

import java.util.List;

public interface ProductService {

        ProductDto create(ProductDto dto);
        ProductDto update(Long id, ProductDto dto);
        ProductDto getById(Long id);
        void delete(Long id);
        List<ProductDto> getAll();
}


