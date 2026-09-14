-- changeset create-permissions-tables
CREATE TABLE permissions (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(50) NOT NULL UNIQUE,
                             description VARCHAR(255),
                             created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role_permissions (
                                  role_id BIGINT NOT NULL,
                                  permission_id BIGINT NOT NULL,
                                  PRIMARY KEY (role_id, permission_id),
                                  CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
                                  CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

-- changeset update-app-users-table
ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20),
    ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE NOT NULL,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- changeset update-user-address-table
ALTER TABLE user_address
    ADD COLUMN IF NOT EXISTS address_line1 VARCHAR(255);

ALTER TABLE user_address
    ADD COLUMN IF NOT EXISTS address_line2 VARCHAR(255);

ALTER TABLE user_address
    ADD COLUMN IF NOT EXISTS state VARCHAR(100);

ALTER TABLE user_address
    ADD COLUMN IF NOT EXISTS postal_code VARCHAR(20);

ALTER TABLE user_address
    ADD COLUMN IF NOT EXISTS country VARCHAR(100);

ALTER TABLE user_address
    ADD COLUMN IF NOT EXISTS is_default BOOLEAN NOT NULL DEFAULT FALSE;

-- changeset insert-initial-permissions
INSERT INTO permissions (name, description) VALUES
                                                ('USER_READ', 'Can view user details'),
                                                ('USER_WRITE', 'Can create or edit users'),
                                                ('USER_DELETE', 'Can delete users'),
                                                ('PRODUCT_READ', 'Can view products'),
                                                ('PRODUCT_WRITE', 'Can create or edit products'),
                                                ('PRODUCT_DELETE', 'Can delete products'),
                                                ('ORDER_READ', 'Can view orders'),
                                                ('ORDER_WRITE', 'Can manage orders'),
                                                ('INVENTORY_READ', 'Can view stock'),
                                                ('INVENTORY_WRITE', 'Can manage stock');