--liquibase formatted sql

--changeset Gabr:006-products-enhance

-- 1) add columns (safe additions)
ALTER TABLE products ADD COLUMN IF NOT EXISTS name_en VARCHAR(255);
ALTER TABLE products ADD COLUMN IF NOT EXISTS name_ar VARCHAR(255);
ALTER TABLE products ADD COLUMN IF NOT EXISTS description_en TEXT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS description_ar TEXT;

ALTER TABLE products ADD COLUMN IF NOT EXISTS slug VARCHAR(255);
ALTER TABLE products ADD COLUMN IF NOT EXISTS sku VARCHAR(64);
ALTER TABLE products ADD COLUMN IF NOT EXISTS brand VARCHAR(120);

ALTER TABLE products ADD COLUMN IF NOT EXISTS currency VARCHAR(10) DEFAULT 'EGP';
ALTER TABLE products ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

ALTER TABLE products ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();
ALTER TABLE products ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();


-- 3) (optional) migrate old columns into new ones if you had name/description previously
-- If you still have products.name and products.description:
UPDATE products
SET name_en = COALESCE(name_en, name),
    description_en = COALESCE(description_en, description)
WHERE (name_en IS NULL OR description_en IS NULL);


-- 5) (optional) upgrade price to NUMERIC(19,2) (recommended for money)
-- WARNING: do this only if you're ready to change entity type to BigDecimal
-- ALTER TABLE products ALTER COLUMN price TYPE NUMERIC(19,2) USING ROUND(price::numeric, 2);
