--liquibase formatted sql

--changeset Gabr:004-insert-categories
INSERT INTO category (name)
SELECT 'Power Tools'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Power Tools');

INSERT INTO category (name)
SELECT 'Hand Tools'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Hand Tools');

INSERT INTO category (name)
SELECT 'Accessories'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Accessories');
