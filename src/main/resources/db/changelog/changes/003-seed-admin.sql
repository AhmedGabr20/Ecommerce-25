--liquibase formatted sql

--changeset gabr:003-seed-admin

INSERT INTO app_user (
    email,
    password,
    enabled,
    created_at,
    updated_at
)
VALUES (
           'ahmedaligabr.20@gmail.com',
           '$2a$10$7QYxZl9YuqbWtaXDpUe1IuP9b5J1nJ3H9xkKj2l9Zk9Z0Yz9y9y9y',
           TRUE,
           NOW(),
           NOW()
       );

INSERT INTO user_role (user_id, role_id)
SELECT
    1,
    id
FROM roles
WHERE name = 'ADMIN';