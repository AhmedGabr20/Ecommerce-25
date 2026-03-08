--liquibase formatted sql

--changeset gabr:001-schema

CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(255) NOT NULL UNIQUE,
                          password VARCHAR(255),
                          role VARCHAR(255)
);

CREATE TABLE category (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255)
);

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,

                          name_en VARCHAR(255),
                          name_ar VARCHAR(255),

                          description_en TEXT,
                          description_ar TEXT,

                          slug VARCHAR(255),
                          sku VARCHAR(64),
                          brand VARCHAR(120),

                          price NUMERIC(19,2) NOT NULL,
                          stock INT,

                          currency VARCHAR(10) DEFAULT 'EGP',
                          is_active BOOLEAN DEFAULT TRUE,

                          category_id BIGINT,

                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW(),

                          version BIGINT DEFAULT 0,

                          CONSTRAINT fk_products_category
                              FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE cart (
                      id BIGSERIAL PRIMARY KEY,

                      cart_uuid UUID UNIQUE,

                      user_id BIGINT,

                      total_price NUMERIC(19,2),

                      discount_amount NUMERIC(19,2) DEFAULT 0,

                      coupon_id BIGINT,

                      version BIGINT NOT NULL DEFAULT 0,

                      CONSTRAINT fk_cart_user
                          FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE cart_item (
                           id BIGSERIAL PRIMARY KEY,

                           quantity INT NOT NULL,

                           unit_price NUMERIC(19,2) NOT NULL,

                           cart_id BIGINT,
                           product_id BIGINT,

                           CONSTRAINT fk_cart_item_cart
                               FOREIGN KEY (cart_id) REFERENCES cart(id),

                           CONSTRAINT fk_cart_item_product
                               FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,

                        created_at TIMESTAMP,

                        status VARCHAR(255) NOT NULL,

                        total_price NUMERIC(19,2),

                        user_id BIGINT,

                        CONSTRAINT fk_orders_user
                            FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE order_item (
                            id BIGSERIAL PRIMARY KEY,

                            quantity INT NOT NULL,

                            price NUMERIC(19,2) NOT NULL,

                            order_id BIGINT,
                            product_id BIGINT,

                            CONSTRAINT fk_order_item_order
                                FOREIGN KEY (order_id) REFERENCES orders(id),

                            CONSTRAINT fk_order_item_product
                                FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE payment (
                         id BIGSERIAL PRIMARY KEY,

                         amount NUMERIC(19,2),

                         method VARCHAR(255),

                         payment_date TIMESTAMP,

                         status VARCHAR(255),

                         order_id BIGINT UNIQUE,

                         CONSTRAINT fk_payment_order
                             FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE refresh_token (
                               id BIGSERIAL PRIMARY KEY,

                               token VARCHAR(255),

                               expiry_date TIMESTAMPTZ,

                               user_id BIGINT,

                               CONSTRAINT fk_refresh_token_user
                                   FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE coupon (
                        id BIGSERIAL PRIMARY KEY,

                        code VARCHAR(255) UNIQUE NOT NULL,

                        discount_percentage NUMERIC(5,2),

                        expiry_date TIMESTAMP,

                        active BOOLEAN DEFAULT TRUE
);

CREATE TABLE product_images (
                                id BIGSERIAL PRIMARY KEY,

                                product_id BIGINT NOT NULL,

                                url TEXT NOT NULL,

                                alt_en VARCHAR(255),
                                alt_ar VARCHAR(255),

                                is_primary BOOLEAN DEFAULT FALSE,

                                sort_order INT DEFAULT 0,

                                created_at TIMESTAMP DEFAULT NOW(),

                                CONSTRAINT fk_product_images_product
                                    FOREIGN KEY (product_id)
                                        REFERENCES products(id)
                                        ON DELETE CASCADE
);

CREATE INDEX idx_product_images_product_id
    ON product_images(product_id);