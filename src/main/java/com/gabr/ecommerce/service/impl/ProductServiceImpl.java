package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.ProductDto;
import com.gabr.ecommerce.entity.Product;
import com.gabr.ecommerce.repository.ProductRepository;
import com.gabr.ecommerce.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository ;
    private ProductDto toDto(Product p){
        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .build();
    }
    private Product toEntity(ProductDto dto){
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();
    }

    @Override
    public ProductDto create(ProductDto dto) {
        Product savedProduct = productRepository.save(toEntity(dto));
        return toDto(savedProduct);
    }

    @Override
    public ProductDto update(Long id, ProductDto dto) {
        Product existingProduct = productRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Product not found"));
        existingProduct.setName(dto.getName());
        existingProduct.setDescription(dto.getDescription());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setStock(dto.getStock());
        return toDto(productRepository.save(existingProduct));
    }

    @Override
    public ProductDto getById(Long id) {
        return toDto(productRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Product not found")));
    }

    @Override
    public void delete(Long id) {
    //    Product existingProduct = productRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Product not found"));
    //    productRepository.delete(existingProduct);
        if(!productRepository.existsById(id)) throw new EntityNotFoundException("Product not found");
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductDto> getAll() {
    //    return productRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
        return productRepository.findAll().stream().map(this::toDto).toList();
    }
}
