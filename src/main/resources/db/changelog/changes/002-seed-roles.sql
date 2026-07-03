--liquibase formatted sql

--changeset gabr:002-seed-roles

INSERT INTO roles (name)
VALUES
    ('ADMIN'),
    ('USER'),
    ('CUSTOMER');