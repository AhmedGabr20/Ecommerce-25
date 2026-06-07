package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.admin.*;
import com.gabr.ecommerce.entity.Category;
import com.gabr.ecommerce.entity.Product;
import com.gabr.ecommerce.entity.ProductImage;
import com.gabr.ecommerce.repository.CategoryRepository;
import com.gabr.ecommerce.repository.ProductRepository;
import com.gabr.ecommerce.service.AdminProductService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.layout.font.FontProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<AdminProductDto> list(String q, Long categoryId, Boolean active, int page, int size, String sortBy, String dir){
    Sort sort = "desc".equalsIgnoreCase(dir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        String query = (q == null) ? "" : q.trim();
        return productRepository.adminSearch(query, categoryId, active, pageable)
                .map(this::toDto);
    }

    @Override
    public AdminProductDto create(ProductUpsertRequest req) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product p = Product.builder()
                .nameEn(req.getNameEn())
                .nameAr(req.getNameAr())
                .descriptionEn(req.getDescriptionEn())
                .descriptionAr(req.getDescriptionAr())
                .price(req.getPrice())
                .stock(req.getStock())
                .sku(req.getSku())
                .slug(req.getSlug())
                .brand(req.getBrand())
                .currency(req.getCurrency())
                .active(req.getActive())
                .category(category)
                .build();

        //  DTO -> Entity
        List<ProductImage> images = req.getImages()
                .stream()
                .map(imgDto -> ProductImage.builder()
                        .product(p)
                        .url(imgDto.getUrl())
                        .altEn(imgDto.getAltEn())
                        .altAr(imgDto.getAltAr())
                        .primaryImage(imgDto.getPrimaryImage())
                        .sortOrder(imgDto.getSortOrder())
                        .build())
                .toList();

        p.setImages(images);

        return toDto(productRepository.save(p));
    }

    @Override
    public AdminProductDto update(Long id, ProductUpsertRequest req) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        p.setNameEn(req.getNameEn());
        p.setNameAr(req.getNameAr());
        p.setDescriptionEn(req.getDescriptionEn());
        p.setDescriptionAr(req.getDescriptionAr());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        p.setSku(req.getSku());
        p.setSlug(req.getSlug());
        p.setBrand(req.getBrand());
        p.setCurrency(req.getCurrency());
        p.setActive(req.getActive());
        p.setCategory(category);

        //  DTO -> Entity
        List<ProductImage> images = req.getImages()
                .stream()
                .map(imgDto -> ProductImage.builder()
                        .product(p)
                        .url(imgDto.getUrl())
                        .altEn(imgDto.getAltEn())
                        .altAr(imgDto.getAltAr())
                        .primaryImage(imgDto.getPrimaryImage())
                        .sortOrder(imgDto.getSortOrder())
                        .build())
                .toList();

        p.setImages(images);

        return toDto(productRepository.save(p));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public byte[] exportPdf(
            String q,
            Long categoryId,
            Boolean active
    ) {

        Pageable pageable = Pageable.unpaged();

        List<Product> products =
                productRepository
                        .adminSearch(
                                q == null ? "" : q,
                                categoryId,
                                active,
                                pageable
                        )
                        .getContent();

        try {

            String html = buildProductsHtml(
                    products,
                    q
            );

            ByteArrayOutputStream target =
                    new ByteArrayOutputStream();

            ConverterProperties properties =
                    new ConverterProperties();

            FontProvider fontProvider =
                    new DefaultFontProvider(false, false, false);

            fontProvider.addFont(
                    com.itextpdf.io.font.FontProgramFactory.createFont(
                            getClass()
                                    .getResource("/fonts/Dubai-Regular.ttf")
                                    .getPath()
                    )
            );

            properties.setFontProvider(fontProvider);
            properties.setCharset("UTF-8");
        //    properties.setBaseUri("src/main/resources/");
            properties.setImmediateFlush(false);

            HtmlConverter.convertToPdf(
                    html,
                    target,
                    properties
            );

            return target.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to export PDF",
                    e
            );
        }
    }

    private AdminProductDto toDto(Product p) {
        String imageUrl = null;

        if (p.getImages() != null && !p.getImages().isEmpty()) {

            imageUrl = p.getImages()
                    .stream()
                    .filter(ProductImage::getPrimaryImage)
                    .findFirst()
                    .map(ProductImage::getUrl)
                    .orElse(
                            p.getImages().get(0).getUrl()
                    );
        }
        return AdminProductDto.builder()
                .id(p.getId())
                .nameEn(p.getNameEn())
                .nameAr(p.getNameAr())
                .descriptionEn(p.getDescriptionEn())
                .descriptionAr(p.getDescriptionAr())
                .price(p.getPrice())
                .stock(p.getStock())
                .sku(p.getSku())
                .slug(p.getSlug())
                .brand(p.getBrand())
                .currency(p.getCurrency())
                .active(p.getActive())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getNameEn() : null)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .imageUrl(imageUrl)
                .build();
    }

    private String buildProductsHtml(
            List<Product> products,
            String q
    ) throws IOException {

        StringBuilder rows = new StringBuilder();

        for (Product p : products) {

            rows.append("""
            <tr>
                <td>%s</td>
                <td>%s</td>
                <td>%s</td>
                <td>%s ج.م</td>
                <td>%s</td>
            </tr>
        """.formatted(
                    p.getId(),
                    p.getNameAr(),
                    p.getSku(),
                    p.getPrice(),
                    p.getStock()
            ));
        }

        InputStream inputStream = getClass()
                .getResourceAsStream(
                        "/templates/products-report.html"
                );

        if (inputStream == null) {
            throw new RuntimeException(
                    "Template not found"
            );
        }

        String html = new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
        );

        return html
                .replace(
                        "{{ROWS}}",
                        rows.toString()
                )
                .replace(
                        "{{DATE}}",
                        java.time.LocalDateTime.now().toString()
                )
                .replace(
                        "{{TOTAL}}",
                        String.valueOf(products.size())
                )
                .replace(
                        "{{SEARCH}}",
                        q == null || q.isBlank()
                                ? "كل المنتجات"
                                : q
                );
    }
}
