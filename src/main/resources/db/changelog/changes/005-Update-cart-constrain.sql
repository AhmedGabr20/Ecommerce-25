-- drop old FK
ALTER TABLE cart_item
DROP CONSTRAINT fk_cart_item_cart;

-- recreate FK with cascade
ALTER TABLE cart_item
    ADD CONSTRAINT fk_cart_item_cart
        FOREIGN KEY (cart_id)
            REFERENCES cart(id)
            ON DELETE CASCADE;