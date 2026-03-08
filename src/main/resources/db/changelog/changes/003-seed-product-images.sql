--liquibase formatted sql

--changeset Gabr:003-seed-products-images


-- Insert images (2 images لكل منتج: primary + second)
-- primary
INSERT INTO product_images (product_id, url, alt_en, alt_ar, is_primary, sort_order)
SELECT p.id,
       'https://picsum.photos/seed/' || p.sku || '-1/900/900',
       p.name_en || ' - main image',
       p.name_ar || ' - الصورة الرئيسية',
       TRUE, 1
FROM products p
WHERE p.sku LIKE 'SKU-%'
  AND NOT EXISTS (
    SELECT 1 FROM product_images i WHERE i.product_id = p.id AND i.sort_order = 1
);

-- secondary
INSERT INTO product_images (product_id, url, alt_en, alt_ar, is_primary, sort_order)
SELECT p.id,
       'https://picsum.photos/seed/' || p.sku || '-2/900/900',
       p.name_en || ' - image 2',
       p.name_ar || ' - صورة ٢',
       FALSE, 2
FROM products p
WHERE p.sku LIKE 'SKU-%'
  AND NOT EXISTS (
    SELECT 1 FROM product_images i WHERE i.product_id = p.id AND i.sort_order = 2
);