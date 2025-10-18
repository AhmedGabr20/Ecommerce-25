package com.gabr.ecommerce.controllers;

import com.gabr.ecommerce.dto.ProductDto;
import com.gabr.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Products", description = "Product management APIs")
@RequestMapping("/api/products")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto dto){
        ProductDto createdProduct = productService.create(dto);
        return ResponseEntity.created(URI.create("/api/products/" + createdProduct.getId())).body(createdProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> get(@PathVariable Long id){
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id")  String sortBy
    ){
        return ResponseEntity.ok(productService.getAll(page,size,sortBy));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id , @RequestBody ProductDto dto){
        return ResponseEntity.ok(productService.update(id,dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> search(@RequestParam String name){
        return ResponseEntity.ok(productService.getByName(name));
    }
}
