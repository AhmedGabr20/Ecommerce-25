--changeset Gabr:004-Update-cart


-- Add created_at, last_modified_at

ALTER TABLE cart
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE cart
    ADD COLUMN last_modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;