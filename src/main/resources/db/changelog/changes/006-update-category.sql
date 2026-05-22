-- إضافة الأعمدة الجديدة
ALTER TABLE category
    ADD COLUMN name_ar VARCHAR(255) NOT NULL,
    ADD COLUMN name_en VARCHAR(255) NOT NULL,
    ADD COLUMN slug VARCHAR(255) NOT NULL UNIQUE,
    ADD COLUMN description_ar TEXT,
    ADD COLUMN description_en TEXT,
    ADD COLUMN image_url VARCHAR(512),
    ADD COLUMN banner_url VARCHAR(512),
    ADD COLUMN parent_id BIGINT,
    ADD COLUMN level INT,
    ADD COLUMN active BOOLEAN DEFAULT TRUE NOT NULL,
    ADD COLUMN sort_order INT,
    ADD COLUMN product_count INT DEFAULT 0 NOT NULL,
    ADD COLUMN meta_title VARCHAR(255),
    ADD COLUMN meta_description VARCHAR(512),
    ADD COLUMN meta_keywords VARCHAR(512);

-- إضافة الـ foreign key للعلاقة مع نفس الجدول
ALTER TABLE category
    ADD CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id)
            REFERENCES category(id)
            ON DELETE SET NULL;