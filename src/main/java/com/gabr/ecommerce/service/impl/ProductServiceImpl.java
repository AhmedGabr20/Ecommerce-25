package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.ProductDto;
import com.gabr.ecommerce.entity.Category;
import com.gabr.ecommerce.entity.Product;
import com.gabr.ecommerce.repository.CategoryRepository;
import com.gabr.ecommerce.repository.ProductRepository;
import com.gabr.ecommerce.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository ;
    private  final CategoryRepository categoryRepository;
    private ProductDto toDto(Product p){
        Category c = p.getCategory();
        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .categoryId(c!=null?c.getId():null)
                .categoryName(c!=null?c.getName():null)
                .build();
    }
    private Product toEntity(ProductDto dto){
        Category category = categoryRepository.findById(dto.getCategoryId()).get();
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(category)
                .build();
    }

    @CacheEvict(value = "products", allEntries = true)
    @Override
    public ProductDto create(ProductDto dto) {
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required");
        }
        log.info("Creating product: {}", dto.getName());
        Product savedProduct = productRepository.save(toEntity(dto));
        log.info("Created product id={}", savedProduct.getId());
        return toDto(savedProduct);
    }

    @Override
    public ProductDto update(Long id, ProductDto dto) {
        Product existingProduct = productRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Product not found"));
        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found"));
        existingProduct.setName(dto.getName());
        existingProduct.setDescription(dto.getDescription());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setStock(dto.getStock());
        existingProduct.setCategory(category);
        return toDto(productRepository.save(existingProduct));
    }

    @Override
    public ProductDto getById(Long id) {
        return toDto(productRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Product not found")));
    }

    @Override
    public void delete(Long id) {
    //    productRepository.delete(existingProduct);
        if(!productRepository.existsById(id)) throw new EntityNotFoundException("Product not found");
        productRepository.deleteById(id);
    }

    @Cacheable("products")
    @Override
    public List<ProductDto> getAll(int page, int size, String sortBy) {
        return productRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public List<ProductDto> getByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream().map(this::toDto).toList();
    }
}
