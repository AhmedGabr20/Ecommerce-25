--liquibase formatted sql

--changeset Gabr:005-insert-products

-- Power Tools
INSERT INTO products (name, description, price, stock, category_id)
SELECT 'Electric Drill 500W', 'Powerful electric drill 500W', 1200, 15,
       (SELECT id FROM category WHERE name = 'Power Tools')
    WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Electric Drill 500W');

INSERT INTO products VALUES
    (DEFAULT, 'Angle Grinder 900W', 'Heavy duty grinder', 950, 20,
     (SELECT id FROM category WHERE name = 'Power Tools'));

INSERT INTO products VALUES
    (DEFAULT, 'Impact Drill 750W', 'High torque impact drill', 1350, 10,
     (SELECT id FROM category WHERE name = 'Power Tools'));

INSERT INTO products VALUES
    (DEFAULT, 'Circular Saw', 'Wood cutting circular saw', 1800, 8,
     (SELECT id FROM category WHERE name = 'Power Tools'));

INSERT INTO products VALUES
    (DEFAULT, 'Jigsaw Machine', 'Precision jigsaw cutter', 1100, 12,
     (SELECT id FROM category WHERE name = 'Power Tools'));

-- Hand Tools
INSERT INTO products VALUES
    (DEFAULT, 'Hammer', 'Steel hammer', 150, 50,
     (SELECT id FROM category WHERE name = 'Hand Tools'));

INSERT INTO products VALUES
    (DEFAULT, 'Screwdriver Set', 'Multi-size screwdriver set', 220, 40,
     (SELECT id FROM category WHERE name = 'Hand Tools'));

INSERT INTO products VALUES
    (DEFAULT, 'Wrench Set', 'Chrome wrench set', 350, 25,
     (SELECT id FROM category WHERE name = 'Hand Tools'));

INSERT INTO products VALUES
    (DEFAULT, 'Pliers', 'Durable hand pliers', 180, 30,
     (SELECT id FROM category WHERE name = 'Hand Tools'));

INSERT INTO products VALUES
    (DEFAULT, 'Measuring Tape', '5 meter measuring tape', 90, 60,
     (SELECT id FROM category WHERE name = 'Hand Tools'));

-- Accessories
INSERT INTO products VALUES
    (DEFAULT, 'Drill Bits Set', 'Metal drill bits set', 300, 35,
     (SELECT id FROM category WHERE name = 'Accessories'));

INSERT INTO products VALUES
    (DEFAULT, 'Cutting Disc', 'Angle grinder cutting disc', 60, 100,
     (SELECT id FROM category WHERE name = 'Accessories'));

INSERT INTO products VALUES
    (DEFAULT, 'Safety Gloves', 'Industrial safety gloves', 120, 45,
     (SELECT id FROM category WHERE name = 'Accessories'));

INSERT INTO products VALUES
    (DEFAULT, 'Safety Glasses', 'Eye protection glasses', 95, 55,
     (SELECT id FROM category WHERE name = 'Accessories'));

INSERT INTO products VALUES
    (DEFAULT, 'Extension Cable', '10m power extension cable', 280, 20,
     (SELECT id FROM category WHERE name = 'Accessories'));

-- Extra Products
INSERT INTO products VALUES
    (DEFAULT, 'Heat Gun', 'Hot air heat gun', 700, 14,
     (SELECT id FROM category WHERE name = 'Power Tools'));

INSERT INTO products VALUES
    (DEFAULT, 'Air Blower', 'Electric air blower', 650, 18,
     (SELECT id FROM category WHERE name = 'Power Tools'));

INSERT INTO products VALUES
    (DEFAULT, 'Tool Box', 'Plastic toolbox', 400, 22,
     (SELECT id FROM category WHERE name = 'Accessories'));

INSERT INTO products VALUES
    (DEFAULT, 'Allen Key Set', 'Hex allen keys', 160, 33,
     (SELECT id FROM category WHERE name = 'Hand Tools'));

INSERT INTO products VALUES
    (DEFAULT, 'Paint Roller', 'Wall paint roller', 140, 27,
     (SELECT id FROM category WHERE name = 'Hand Tools'));
