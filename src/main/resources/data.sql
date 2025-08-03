
-- Customers

INSERT INTO customers (id, name, surname, national_id, create_date, update_date, created_by, updated_by)
VALUES (1, 'Murat', 'Koçoğlu', '12345678901', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO customers (id, name, surname, national_id, create_date, update_date, created_by, updated_by)
VALUES (2, 'Alper', 'Yılmaz', '10987654321', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');


--pwd: 123
-- ADMIN
INSERT INTO users (id, username, password, role, customer_id, create_date, update_date, created_by, updated_by)
VALUES (1, 'admin', '$2a$10$bsbvMy6sVAomTaVC1EfDnOmD0Zk0PQ2chMtGA.iCAKtrPNkmqaYJK', 'ADMIN', NULL,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- CUSTOMER USER
INSERT INTO users (id, username, password, role, customer_id, create_date, update_date, created_by, updated_by)
VALUES (2, 'murat.kocoglu', '$2a$10$bsbvMy6sVAomTaVC1EfDnOmD0Zk0PQ2chMtGA.iCAKtrPNkmqaYJK', 'CUSTOMER', 1,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO users (id, username, password, role, customer_id, create_date, update_date, created_by, updated_by)
VALUES (3, 'alper.yilmaz', '$2a$10$bsbvMy6sVAomTaVC1EfDnOmD0Zk0PQ2chMtGA.iCAKtrPNkmqaYJK', 'CUSTOMER', 2,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');
        
INSERT INTO assets (customer_id, name, size, usable_size, create_date, update_date, created_by, updated_by)
VALUES (1, 'TRY', 100000, 100000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO assets (customer_id, name, size, usable_size, create_date, update_date, created_by, updated_by)
VALUES (2, 'TRY', 100000, 100000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');