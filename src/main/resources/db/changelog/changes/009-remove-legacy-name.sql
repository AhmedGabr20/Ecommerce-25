--liquibase formatted sql

--changeset Gabr:010-remove-legacy-name

ALTER TABLE products
DROP COLUMN IF EXISTS name,
    DROP COLUMN IF EXISTS description;
