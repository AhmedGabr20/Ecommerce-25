--changeset gabr:004-seed-product-categories

INSERT INTO category
(
    name_ar,
    name_en,
    slug,
    active,
    level,
    product_count
)
VALUES
    (
        'أدوات كهربائية',
        'Power Tools',
        'power-tools',
        TRUE,
        1,
        0
    ),
    (
        'أدوات يدوية',
        'Hand Tools',
        'hand-tools',
        TRUE,
        1,
        0
    ),
    (
        'إكسسوارات',
        'Accessories',
        'accessories',
        TRUE,
        1,
        0
    );

------------------------------------------------------------
-- products
------------------------------------------------------------

INSERT INTO products
(name_en,name_ar,description_en,description_ar,slug,sku,brand,price,stock,category_id)
VALUES

    ('Electric Drill 500W','شنيور كهرباء','Compact drill','شنيور عملي','electric-drill-500w','SKU-PT-001','Total',1200,15,1),

    ('Impact Drill 750W','شنيور دقاق','High torque impact drill','شنيور دقاق','impact-drill-750w','SKU-PT-002','Ronix',1450,10,1),

    ('Angle Grinder 900W','صاروخ','Heavy duty grinder','صاروخ قوي','angle-grinder-900w','SKU-PT-003','Crown',980,18,1),

    ('Circular Saw','منشار دائري','Wood cutting saw','منشار خشب','circular-saw','SKU-PT-004','Bosch',2250,7,1),

    ('Jigsaw 650W','أركت','Precision cutting','أركت دقيق','jigsaw','SKU-PT-005','Makita',1750,9,1),

    ('Heat Gun','مسدس حراري','Heat gun','مسدس حراري','heat-gun','SKU-PT-006','Ingco',850,14,1),

    ('Air Blower','منفاخ','Dust cleaning blower','منفاخ تنظيف','air-blower','SKU-PT-007','Crown',720,16,1),

    ('Cordless Screwdriver','مفك شحن','Cordless screwdriver','مفك شحن','cordless-screwdriver','SKU-PT-008','Total',1100,11,1),

    ('Hammer','شاكوش','Steel hammer','شاكوش صلب','hammer','SKU-HT-001','Harden',160,50,2),

    ('Screwdriver Set','طقم مفكات','Multi screwdrivers','طقم مفكات','screwdriver-set','SKU-HT-002','Harden',240,40,2),

    ('Wrench Set','طقم مفاتيح','Wrench set','طقم مفاتيح','wrench-set','SKU-HT-003','Stanley',420,22,2),

    ('Pliers','بنسة','Durable pliers','بنسة','pliers','SKU-HT-004','Harden',190,30,2),

    ('Measuring Tape','متر','Tape measure','متر قياس','measuring-tape','SKU-HT-005','Total',95,60,2),

    ('Allen Key Set','طقم ألن','Hex keys','مفاتيح ألن','allen-keys','SKU-HT-006','Ingco',170,33,2),

    ('Adjustable Wrench','مفتاح انجليزي','Adjustable wrench','مفتاح انجليزي','adjustable-wrench','SKU-HT-007','Stanley',210,25,2),

    ('Drill Bits Set','بنط','Metal drill bits','بنط معدن','drill-bits','SKU-AC-001','Total',320,35,3),

    ('Cutting Disc','ديسك','Cutting disc','ديسك قطع','cutting-disc','SKU-AC-002','Bosch',65,100,3),

    ('Safety Gloves','جوانتي','Safety gloves','جوانتي أمان','safety-gloves','SKU-AC-003','Harden',130,45,3),

    ('Safety Glasses','نظارة أمان','Eye protection','نظارة أمان','safety-glasses','SKU-AC-004','Ingco',99,55,3),

    ('Extension Cable','وصلة كهرباء','Extension cable','وصلة كهرباء','extension-cable','SKU-AC-005','Total',290,20,3);

