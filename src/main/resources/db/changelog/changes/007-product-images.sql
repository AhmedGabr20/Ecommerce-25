--liquibase formatted sql

--changeset Gabr:007-product-images

CREATE TABLE IF NOT EXISTS product_images (
                                              id BIGSERIAL PRIMARY KEY,
                                              product_id BIGINT NOT NULL,
                                              url TEXT NOT NULL,
                                              alt_en VARCHAR(255),
    alt_ar VARCHAR(255),
    is_primary BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_product_images_product
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_product_images_product_id ON product_images(product_id);
