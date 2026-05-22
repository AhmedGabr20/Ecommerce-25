package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.ProductDto;
import com.gabr.ecommerce.dto.ProductImageDto;
import com.gabr.ecommerce.entity.Category;
import com.gabr.ecommerce.entity.Product;
import com.gabr.ecommerce.entity.ProductImage;
import com.gabr.ecommerce.repository.CategoryRepository;
import com.gabr.ecommerce.repository.ProductImageRepository;
import com.gabr.ecommerce.repository.ProductRepository;
import com.gabr.ecommerce.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository ;
    private  final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;


    private ProductDto toDto(Product p){
        String primaryUrl = null;
        if (p.getImages() != null) {
            var primary = p.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getPrimaryImage()))
                    .findFirst()
                    .orElse(p.getImages().stream()
                            .sorted((a,b) -> Integer.compare(
                                    a.getSortOrder() == null ? 0 : a.getSortOrder(),
                                    b.getSortOrder() == null ? 0 : b.getSortOrder()
                            ))
                            .findFirst()
                            .orElse(null));

            if (primary != null) primaryUrl = primary.getUrl();
        }
        return ProductDto.builder()
                .id(p.getId())
                .nameAr(p.getNameAr())
                .nameEn(p.getNameEn())
                .descriptionAr(p.getDescriptionAr())
                .descriptionEn(p.getDescriptionEn())
                .sku(p.getSku())
                .slug(p.getSlug())
                .brand(p.getBrand())
                .currency(p.getCurrency())
                .active(p.getActive())
                .price(p.getPrice())
                .stock(p.getStock())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getNameEn() : null)
                .primaryImageUrl(primaryUrl)
                .images(p.getImages() == null ? null :
                        p.getImages().stream().map(this::toImageDto).toList()
                )
                .build();
    }
    private ProductImageDto toImageDto(ProductImage img) {
        return ProductImageDto.builder()
                .id(img.getId())
                .url(img.getUrl())
                .altEn(img.getAltEn())
                .altAr(img.getAltAr())
                .primaryImage(img.getPrimaryImage())
                .sortOrder(img.getSortOrder())
                .build();
    }

    private Product toEntity(ProductDto dto){
        Category category = categoryRepository.findById(dto.getCategoryId()).get();
        return Product.builder()
                .id(dto.getId())
                .nameAr(dto.getNameAr())
                .nameEn(dto.getNameEn())
                .descriptionAr(dto.getDescriptionAr())
                .descriptionEn(dto.getDescriptionEn())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(category)
                .build();
    }

//    @CacheEvict(value = "products", allEntries = true)
    @Override
    public ProductDto create(ProductDto dto) {
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required");
        }
        log.info("Creating product: {}", dto.getNameEn());
        Product savedProduct = productRepository.save(toEntity(dto));
        log.info("Created product id={}", savedProduct.getId());
        return toDto(savedProduct);
    }

    @Override
    public ProductDto update(Long id, ProductDto dto) {
        Product existingProduct = productRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Product not found"));
        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found"));
        existingProduct.setNameAr(dto.getNameAr());
        existingProduct.setNameEn(dto.getNameEn());
        existingProduct.setDescriptionAr(dto.getDescriptionAr());
        existingProduct.setDescriptionEn(dto.getDescriptionEn());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setStock(dto.getStock());
        existingProduct.setCategory(category);
        return toDto(productRepository.save(existingProduct));
    }

    @Override
    public ProductDto getById(Long id) {
        return toDto(productRepository.findDetailsById(id).orElseThrow(()-> new EntityNotFoundException("Product not found")));
    }

    @Override
    public void delete(Long id) {
    //    productRepository.delete(existingProduct);
        if(!productRepository.existsById(id)) throw new EntityNotFoundException("Product not found");
        productRepository.deleteById(id);
    }

   // @Cacheable("products")
    @Override
    public List<ProductDto> getAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        return productRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(p -> {
                    String primaryUrl = productImageRepository.findOrderedUrls(p.getId())
                            .stream().findFirst().orElse(null);
                    return toDtoList(p, primaryUrl);
                })
                .toList();
    }

    @Override
    public List<ProductDto> getByName(String name) {
        return productRepository
                .findByNameEnContainingIgnoreCaseOrNameArContainingIgnoreCase(
                        name, name
                )
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> getByCategory(Long categoryId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        return productRepository.findByCategory(category,pageable)
                .getContent()
                .stream()
                .map(p -> {
                    String primaryUrl = productImageRepository.findOrderedUrls(p.getId())
                            .stream().findFirst().orElse(null);
                    return toDtoList(p, primaryUrl);
                })
                .toList();
    }

    private void applyDto(ProductDto dto, Product p, Category category) {

        p.setNameEn(dto.getNameEn());
        p.setNameAr(dto.getNameAr());
        p.setDescriptionEn(dto.getDescriptionEn());
        p.setDescriptionAr(dto.getDescriptionAr());

        p.setSku(dto.getSku());
        p.setSlug(dto.getSlug());
        p.setBrand(dto.getBrand());

        p.setCurrency(dto.getCurrency() == null ? "EGP" : dto.getCurrency());
        p.setActive(dto.getActive() == null ? true : dto.getActive());

        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setCategory(category);
    }

    private ProductDto toDtoList(Product p, String primaryImageUrl) {
        return ProductDto.builder()
                .id(p.getId())
                .nameAr(p.getNameAr())
                .nameEn(p.getNameEn())
                .descriptionAr(p.getDescriptionAr())
                .descriptionEn(p.getDescriptionEn())
                .sku(p.getSku())
                .slug(p.getSlug())
                .brand(p.getBrand())
                .currency(p.getCurrency())
                .active(p.getActive())
                .price(p.getPrice())
                .stock(p.getStock())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getNameEn() : null)
                .primaryImageUrl(primaryImageUrl)
                .images(null)
                .build();
    }

}
