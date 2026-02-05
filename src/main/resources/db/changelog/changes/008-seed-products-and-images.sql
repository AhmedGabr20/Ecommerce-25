--liquibase formatted sql

--changeset Gabr:008-seed-products-and-images

-- Ensure categories exist
INSERT INTO category (name)
SELECT 'Power Tools' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name='Power Tools');
INSERT INTO category (name)
SELECT 'Hand Tools' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name='Hand Tools');
INSERT INTO category (name)
SELECT 'Accessories' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name='Accessories');

-- Helper: get category ids
-- (We’ll use subqueries inline)

-- Insert 20 products (idempotent by sku)
INSERT INTO products (name_en, name_ar, description_en, description_ar, slug, sku, brand, price, stock, category_id, currency, is_active)
SELECT
    v.name_en, v.name_ar, v.desc_en, v.desc_ar, v.slug, v.sku, v.brand, v.price, v.stock,
    (SELECT id FROM category WHERE name = v.cat),
    'EGP', TRUE
FROM (
         VALUES
             -- Power Tools (8)
             ('Electric Drill 500W','شنيور كهرباء ٥٠٠ وات','Compact drill for home & workshop.','شنيور عملي للاستخدام المنزلي والورشة.','electric-drill-500w','SKU-PT-001','Total', 1200.00, 15, 'Power Tools'),
             ('Impact Drill 750W','شنيور دقاق ٧٥٠ وات','High torque impact drill.','شنيور دقاق بعزم عالي.','impact-drill-750w','SKU-PT-002','Ronix', 1450.00, 10, 'Power Tools'),
             ('Angle Grinder 900W','صاروخ ٩٠٠ وات','Heavy duty grinder with guard.','صاروخ قوي مع واقي.','angle-grinder-900w','SKU-PT-003','Crown', 980.00, 18, 'Power Tools'),
             ('Circular Saw 7.25"','منشار دائري ٧.٢٥','Wood cutting circular saw.','منشار دائري لقطع الخشب.','circular-saw-725','SKU-PT-004','Bosch', 2250.00, 7, 'Power Tools'),
             ('Jigsaw 650W','أركت ٦٥٠ وات','Precision cutting jigsaw.','أركت لقصّ دقيق.','jigsaw-650w','SKU-PT-005','Makita', 1750.00, 9, 'Power Tools'),
             ('Heat Gun 2000W','مسدس حراري ٢٠٠٠ وات','Heat gun for shrink & paint removal.','مسدس حراري للشرنك وإزالة الدهان.','heat-gun-2000w','SKU-PT-006','Ingco', 850.00, 14, 'Power Tools'),
             ('Air Blower 600W','منفاخ هواء ٦٠٠ وات','Air blower for dust cleaning.','منفاخ لتنظيف الأتربة.','air-blower-600w','SKU-PT-007','Crown', 720.00, 16, 'Power Tools'),
             ('Cordless Screwdriver 12V','مفك شحن ١٢ فولت','Cordless screwdriver with charger.','مفك شحن مع شاحن.','cordless-screwdriver-12v','SKU-PT-008','Total', 1100.00, 11, 'Power Tools'),

             -- Hand Tools (7)
             ('Hammer 16oz','شاكوش ١٦ أوقية','Steel hammer with rubber grip.','شاكوش صلب بمقبض مطاط.','hammer-16oz','SKU-HT-001','Harden', 160.00, 50, 'Hand Tools'),
             ('Screwdriver Set 6pcs','طقم مفكات ٦ قطع','Mixed Phillips/Flat screwdrivers.','طقم مفكات صليبة وعادة.','screwdriver-set-6pcs','SKU-HT-002','Harden', 240.00, 40, 'Hand Tools'),
             ('Wrench Set 8pcs','طقم مفاتيح ٨ قطع','Chrome vanadium wrench set.','طقم مفاتيح كروم فاناديوم.','wrench-set-8pcs','SKU-HT-003','Stanley', 420.00, 22, 'Hand Tools'),
             ('Pliers 8"','بنسة ٨ بوصة','Durable pliers for workshop.','بنسة قوية للورشة.','pliers-8','SKU-HT-004','Harden', 190.00, 30, 'Hand Tools'),
             ('Measuring Tape 5m','متر ٥ متر','Locking measuring tape.','متر بقياس ثابت.','measuring-tape-5m','SKU-HT-005','Total', 95.00, 60, 'Hand Tools'),
             ('Allen Key Set','طقم ألن','Hex keys set for maintenance.','طقم مفاتيح ألن للصيانة.','allen-key-set','SKU-HT-006','Ingco', 170.00, 33, 'Hand Tools'),
             ('Adjustable Wrench 10"','مفتاح إنجليزي ١٠','Adjustable wrench heavy duty.','مفتاح إنجليزي قوي.','adjustable-wrench-10','SKU-HT-007','Stanley', 210.00, 25, 'Hand Tools'),

             -- Accessories (5)
             ('Drill Bits Set 13pcs','طقم بنط ١٣ قطعة','Metal drill bits set.','طقم بنط معدن.','drill-bits-set-13','SKU-AC-001','Total', 320.00, 35, 'Accessories'),
             ('Cutting Disc 4.5"','ديسك قطع ٤.٥','Cutting disc for grinder.','ديسك قطع للصاروخ.','cutting-disc-45','SKU-AC-002','Bosch', 65.00, 100, 'Accessories'),
             ('Safety Gloves','جوانتي أمان','Industrial safety gloves.','جوانتي أمان صناعي.','safety-gloves','SKU-AC-003','Harden', 130.00, 45, 'Accessories'),
             ('Safety Glasses','نظارة أمان','Eye protection glasses.','نظارة لحماية العين.','safety-glasses','SKU-AC-004','Ingco', 99.00, 55, 'Accessories'),
             ('Extension Cable 10m','وصلة كهرباء ١٠م','10m power extension cable.','وصلة كهرباء ١٠ متر.','extension-cable-10m','SKU-AC-005','Total', 290.00, 20, 'Accessories')
     ) AS v(name_en, name_ar, desc_en, desc_ar, slug, sku, brand, price, stock, cat)
WHERE NOT EXISTS (SELECT 1 FROM products p WHERE p.sku = v.sku);

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
