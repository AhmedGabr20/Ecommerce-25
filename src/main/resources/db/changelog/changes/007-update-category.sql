-- 1️⃣ إضافة الأعمدة الجديدة بدون قيود
ALTER TABLE category
ADD COLUMN name_ar VARCHAR(255),
ADD COLUMN name_en VARCHAR(255),
ADD COLUMN slug VARCHAR(255),
ADD COLUMN description_ar TEXT,
ADD COLUMN description_en TEXT,
ADD COLUMN image_url VARCHAR(512),
ADD COLUMN banner_url VARCHAR(512),
ADD COLUMN parent_id BIGINT,
ADD COLUMN level INT,
ADD COLUMN active BOOLEAN DEFAULT TRUE,
ADD COLUMN sort_order INT,
ADD COLUMN product_count INT DEFAULT 0,
ADD COLUMN meta_title VARCHAR(255),
ADD COLUMN meta_description VARCHAR(512),
ADD COLUMN meta_keywords VARCHAR(512);

-- 2️⃣ نقل البيانات القديمة
UPDATE category
SET name_ar = name,
    name_en = name,
    slug = LOWER(REPLACE(name, ' ', '-'));

-- 3️⃣ إضافة القيود
ALTER TABLE category
ALTER COLUMN name_ar SET NOT NULL,
ALTER COLUMN name_en SET NOT NULL,
ALTER COLUMN slug SET NOT NULL;

-- 4️⃣ إضافة unique للـ slug
ALTER TABLE category
ADD CONSTRAINT uq_category_slug UNIQUE(slug);

-- 5️⃣ إضافة foreign key للـ parent category
ALTER TABLE category
ADD CONSTRAINT fk_category_parent
FOREIGN KEY (parent_id)
REFERENCES category(id)
ON DELETE SET NULL;

-- 6️⃣ حذف العمود القديم
ALTER TABLE category
DROP COLUMN name;