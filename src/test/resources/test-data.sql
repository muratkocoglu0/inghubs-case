DELETE FROM users;
DELETE FROM assets;
DELETE FROM customers;


-- Customers

INSERT INTO customers (id, name, surname, national_id, create_date, update_date, created_by, updated_by)
VALUES (100, 'Murat', 'Koçoğlu', '12345678901', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO customers (id, name, surname, national_id, create_date, update_date, created_by, updated_by)
VALUES (200, 'Alper', 'Yılmaz', '10987654321', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');


--pwd: 123
-- ADMIN
INSERT INTO users (username, password, role, customer_id, create_date, update_date, created_by, updated_by)
VALUES ('admin', '$2a$10$.2kPPFI7dIpKh354llYFmOyAzXcEoF.x6F06wZWLfHG3AClXJ23pW', 'ADMIN', NULL,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- CUSTOMER USER
INSERT INTO users (username, password, role, customer_id, create_date, update_date, created_by, updated_by)
VALUES ('murat.kocoglu', '$2a$10$.2kPPFI7dIpKh354llYFmOyAzXcEoF.x6F06wZWLfHG3AClXJ23pW', 'CUSTOMER', 100,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO users (username, password, role, customer_id, create_date, update_date, created_by, updated_by)
VALUES ('alper.yilmaz', '$2a$10$.2kPPFI7dIpKh354llYFmOyAzXcEoF.x6F06wZWLfHG3AClXJ23pW', 'CUSTOMER', 200,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');
        
        
INSERT INTO assets (customer_id, name, size, usable_size, create_date, update_date, created_by, updated_by)
VALUES (100, 'TRY', 100000, 100000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO assets (customer_id, name, size, usable_size, create_date, update_date, created_by, updated_by)
VALUES (200, 'TRY', 100000, 100000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');