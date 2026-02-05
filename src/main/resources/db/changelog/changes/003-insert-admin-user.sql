-- Insert Admin User (ahmedaligabr.20@gmail.com)
-- Password: admin123 (BCrypt)

INSERT INTO app_user (username, password, role)
SELECT
    'ahmedaligabr.20@gmail.com',
    '$2a$10$7QYxZl9YuqbWtaXDpUe1IuP9b5J1nJ3H9xkKj2l9Zk9Z0Yz9y9y9y',
    'ADMIN'
    WHERE NOT EXISTS (
    SELECT 1 FROM app_user WHERE username = 'ahmedaligabr.20@gmail.com'
);
