package com.gabr.ecommerce;

import com.gabr.ecommerce.dto.ProductDto;
import com.gabr.ecommerce.entity.Product;
import com.gabr.ecommerce.repository.ProductRepository;
import com.gabr.ecommerce.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {
    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductServiceImpl service;

    public ProductServiceTest(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProduct() {
        Product p = Product.builder().id(1L).name("Hammer").price(100.0).stock(5).build();
        when(repo.save(any())).thenReturn(p);

        ProductDto dto = ProductDto.builder().name("Hammer").price(100.0).stock(5).build();
        ProductDto result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals("Hammer", result.getName());
        verify(repo, times(1)).save(any());
    }
}
