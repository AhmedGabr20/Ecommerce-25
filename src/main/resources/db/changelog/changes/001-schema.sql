--liquibase formatted sql

--changeset gabr:001-schema

------------------------------------------------------------
-- APP USER
------------------------------------------------------------

CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,

                          email VARCHAR(255) NOT NULL UNIQUE,

                          password VARCHAR(255) NOT NULL,

                          enabled BOOLEAN NOT NULL DEFAULT TRUE,

                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

------------------------------------------------------------
-- ROLES
------------------------------------------------------------

CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,

                       name VARCHAR(255) NOT NULL UNIQUE
);

------------------------------------------------------------
-- USER_ROLE
------------------------------------------------------------

CREATE TABLE user_role (

                           user_id BIGINT NOT NULL,

                           role_id BIGINT NOT NULL,

                           PRIMARY KEY(user_id, role_id),

                           CONSTRAINT fk_user_role_user
                               FOREIGN KEY(user_id)
                                   REFERENCES app_user(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT fk_user_role_role
                               FOREIGN KEY(role_id)
                                   REFERENCES roles(id)
                                   ON DELETE CASCADE
);

------------------------------------------------------------
-- USER PROFILE
------------------------------------------------------------

CREATE TABLE user_profile (

                              user_id BIGINT PRIMARY KEY,

                              first_name VARCHAR(255),

                              last_name VARCHAR(255),

                              phone VARCHAR(255),

                              image VARCHAR(255),

                              CONSTRAINT fk_user_profile_user
                                  FOREIGN KEY(user_id)
                                      REFERENCES app_user(id)
                                      ON DELETE CASCADE
);

------------------------------------------------------------
-- USER ADDRESS
------------------------------------------------------------

CREATE TABLE user_address (

                              id BIGSERIAL PRIMARY KEY,

                              user_id BIGINT NOT NULL,

                              city VARCHAR(255),

                              street VARCHAR(255),

                              CONSTRAINT fk_user_address_user
                                  FOREIGN KEY(user_id)
                                      REFERENCES app_user(id)
                                      ON DELETE CASCADE
);

------------------------------------------------------------
-- REFRESH TOKEN
------------------------------------------------------------

CREATE TABLE refresh_token (

                               id BIGSERIAL PRIMARY KEY,

                               token VARCHAR(1000) NOT NULL UNIQUE,

                               expiry_date TIMESTAMPTZ,

                               user_id BIGINT,

                               CONSTRAINT fk_refresh_token_user
                                   FOREIGN KEY(user_id)
                                       REFERENCES app_user(id)
                                       ON DELETE CASCADE
);

------------------------------------------------------------
-- INDEXES
------------------------------------------------------------

CREATE INDEX idx_refresh_token_user
    ON refresh_token(user_id);

CREATE INDEX idx_user_address_user
    ON user_address(user_id);

CREATE INDEX idx_user_role_role
    ON user_role(role_id);

CREATE INDEX idx_app_user_email
    ON app_user(email);

------------------------------------------------------------
-- CATEGORY
------------------------------------------------------------

CREATE TABLE category (

                          id BIGSERIAL PRIMARY KEY,

                          name_ar VARCHAR(255) NOT NULL,

                          name_en VARCHAR(255) NOT NULL,

                          slug VARCHAR(255) NOT NULL UNIQUE,

                          description_ar TEXT,

                          description_en TEXT,

                          image_url VARCHAR(512),

                          banner_url VARCHAR(512),

                          parent_id BIGINT,

                          level INT,

                          active BOOLEAN NOT NULL DEFAULT TRUE,

                          sort_order INT,

                          product_count INT NOT NULL DEFAULT 0,

                          meta_title VARCHAR(255),

                          meta_description VARCHAR(512),

                          meta_keywords VARCHAR(512),

                          CONSTRAINT fk_category_parent
                              FOREIGN KEY(parent_id)
                                  REFERENCES category(id)
                                  ON DELETE SET NULL
);

------------------------------------------------------------
-- PRODUCTS
------------------------------------------------------------

CREATE TABLE products (

                          id BIGSERIAL PRIMARY KEY,

                          name_en VARCHAR(255),

                          name_ar VARCHAR(255),

                          description_en TEXT,

                          description_ar TEXT,

                          slug VARCHAR(255) UNIQUE,

                          sku VARCHAR(64),

                          brand VARCHAR(120),

                          price NUMERIC(19,2) NOT NULL,

                          stock INT,

                          currency VARCHAR(10) DEFAULT 'EGP',

                          is_active BOOLEAN DEFAULT TRUE,

                          category_id BIGINT,

                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          version BIGINT DEFAULT 0,

                          CONSTRAINT fk_products_category
                              FOREIGN KEY(category_id)
                                  REFERENCES category(id)
);

------------------------------------------------------------
-- PRODUCT IMAGES
------------------------------------------------------------

CREATE TABLE product_images (

                                id BIGSERIAL PRIMARY KEY,

                                product_id BIGINT NOT NULL,

                                url TEXT NOT NULL,

                                alt_en VARCHAR(255),

                                alt_ar VARCHAR(255),

                                is_primary BOOLEAN DEFAULT FALSE,

                                sort_order INT DEFAULT 0,

                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_product_images_product
                                    FOREIGN KEY(product_id)
                                        REFERENCES products(id)
                                        ON DELETE CASCADE
);

------------------------------------------------------------
-- INDEXES
------------------------------------------------------------

CREATE INDEX idx_category_slug
    ON category(slug);

CREATE INDEX idx_category_parent
    ON category(parent_id);

CREATE INDEX idx_category_active
    ON category(active);

CREATE INDEX idx_products_slug
    ON products(slug);

CREATE INDEX idx_products_category
    ON products(category_id);

CREATE INDEX idx_products_active
    ON products(is_active);

CREATE INDEX idx_products_sku
    ON products(sku);

CREATE INDEX idx_product_images_product
    ON product_images(product_id);

------------------------------------------------------------
-- COUPON
------------------------------------------------------------

CREATE TABLE coupon (

                        id BIGSERIAL PRIMARY KEY,

                        code VARCHAR(255) NOT NULL UNIQUE,

                        discount_percentage NUMERIC(5,2),

                        expiry_date TIMESTAMP,

                        active BOOLEAN NOT NULL DEFAULT TRUE
);

------------------------------------------------------------
-- CART
------------------------------------------------------------

CREATE TABLE cart (

                      id BIGSERIAL PRIMARY KEY,

                      cart_uuid UUID UNIQUE,

                      user_id BIGINT,

                      total_price NUMERIC(19,2),

                      discount_amount NUMERIC(19,2) DEFAULT 0,

                      coupon_id BIGINT,

                      version BIGINT NOT NULL DEFAULT 0,

                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                      last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                      CONSTRAINT fk_cart_user
                          FOREIGN KEY(user_id)
                              REFERENCES app_user(id),

                      CONSTRAINT fk_cart_coupon
                          FOREIGN KEY(coupon_id)
                              REFERENCES coupon(id)
);

------------------------------------------------------------
-- CART ITEM
------------------------------------------------------------

CREATE TABLE cart_item (

                           id BIGSERIAL PRIMARY KEY,

                           quantity INT NOT NULL,

                           unit_price NUMERIC(19,2) NOT NULL,

                           cart_id BIGINT NOT NULL,

                           product_id BIGINT NOT NULL,

                           CONSTRAINT fk_cart_item_cart
                               FOREIGN KEY(cart_id)
                                   REFERENCES cart(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT fk_cart_item_product
                               FOREIGN KEY(product_id)
                                   REFERENCES products(id)
);

------------------------------------------------------------
-- ORDERS
------------------------------------------------------------

CREATE TABLE orders (

                        id BIGSERIAL PRIMARY KEY,

                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                        status VARCHAR(50) NOT NULL,

                        total_price NUMERIC(19,2),

                        user_id BIGINT,

                        CONSTRAINT fk_orders_user
                            FOREIGN KEY(user_id)
                                REFERENCES app_user(id)
);

------------------------------------------------------------
-- ORDER ITEM
------------------------------------------------------------

CREATE TABLE order_item (

                            id BIGSERIAL PRIMARY KEY,

                            quantity INT NOT NULL,

                            price NUMERIC(19,2) NOT NULL,

                            order_id BIGINT NOT NULL,

                            product_id BIGINT NOT NULL,

                            CONSTRAINT fk_order_item_order
                                FOREIGN KEY(order_id)
                                    REFERENCES orders(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_order_item_product
                                FOREIGN KEY(product_id)
                                    REFERENCES products(id)
);

------------------------------------------------------------
-- PAYMENT
------------------------------------------------------------

CREATE TABLE payment (

                         id BIGSERIAL PRIMARY KEY,

                         amount NUMERIC(19,2),

                         method VARCHAR(50),

                         payment_date TIMESTAMP,

                         status VARCHAR(50),

                         order_id BIGINT UNIQUE,

                         CONSTRAINT fk_payment_order
                             FOREIGN KEY(order_id)
                                 REFERENCES orders(id)
                                 ON DELETE CASCADE
);

------------------------------------------------------------
-- INDEXES
------------------------------------------------------------

CREATE INDEX idx_cart_uuid
    ON cart(cart_uuid);

CREATE INDEX idx_cart_user
    ON cart(user_id);

CREATE INDEX idx_cart_coupon
    ON cart(coupon_id);

CREATE INDEX idx_cart_item_cart
    ON cart_item(cart_id);

CREATE INDEX idx_cart_item_product
    ON cart_item(product_id);

CREATE INDEX idx_orders_user
    ON orders(user_id);

CREATE INDEX idx_orders_status
    ON orders(status);

CREATE INDEX idx_order_item_order
    ON order_item(order_id);

CREATE INDEX idx_order_item_product
    ON order_item(product_id);

CREATE INDEX idx_payment_order
    ON payment(order_id);

CREATE INDEX idx_coupon_code
    ON coupon(code);

------------------------------------------------------------
-- END OF SCHEMA
------------------------------------------------------------