
delete from role_permission;
delete from  user_role;
delete from  permissions;
delete from  roles;
delete from  users;

delete from  order_items;
delete from  orders;
delete from  payments;
delete from  customers;
delete from  addresses;
delete from  products;
delete from  categories;

INSERT INTO permissions (id, name, created_on, modified_on) VALUES
  (1, 'MANAGE_CATEGORIES', NOW(), NOW()),
  (2, 'MANAGE_PRODUCTS', NOW(), NOW()),
  (3, 'MANAGE_ORDERS', NOW(), NOW()),
  (4, 'MANAGE_CUSTOMERS', NOW(), NOW()),
  (5, 'MANAGE_PAYMENT_SYSTEMS', NOW(), NOW()),
  (6, 'MANAGE_USERS', NOW(), NOW()),
  (7, 'MANAGE_ROLES', NOW(), NOW()),
  (8, 'MANAGE_PERMISSIONS', NOW(), NOW()),
  (9, 'MANAGE_SETTINGS', NOW(), NOW())
;

INSERT INTO roles (id, name, created_on, modified_on) VALUES
  (1, 'ROLE_SUPER_ADMIN', NOW(), NOW()),
  (2, 'ROLE_ADMIN', NOW(), NOW()),
  (3, 'ROLE_CMS_ADMIN', NOW(), NOW()),
  (4, 'ROLE_USER', NOW(), NOW())
;

INSERT INTO users (id, email, password, name, created_on, modified_on) VALUES
  (1, 'john@gmail.com', '$2a$10$vacuqbDw9I7rr6RRH8sByuktOzqTheQMfnK3XCT2WlaL7vt/3AMby', 'JohnGreen', NOW(), NOW()),
  (2, 'visperboy@gmail.com', '$2a$10$vacuqbDw9I7rr6RRH8sByuktOzqTheQMfnK3XCT2WlaL7vt/3AMby', 'NiceGuy', NOW(), NOW()),
  (3, 'jack@gmail.com', '$2a$10$vacuqbDw9I7rr6RRH8sByuktOzqTheQMfnK3XCT2WlaL7vt/3AMby', 'Jack Smith', NOW(), NOW())

;

insert into role_permission(role_id, perm_id) values
  (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),
  (1,7),(1,8),(1,9),
  (2,1),(2,2),(2,3),(2,4),(2,5),(2,9),
  (3,1),(3,2)
;

insert into user_role(user_id, role_id) values
  (1,1),
  (2,1),
  (3,3)
;

insert into categories(id, name, disp_order,disabled, created_on, modified_on) values
  (1,'Flowers',1,false, NOW(), NOW()),
  (2,'Toys',2,false, NOW(), NOW()),
  (3,'Birds',3,false, NOW(), NOW())
;

INSERT INTO products (id,cat_id,sku,name,description,price,disabled,created_on, modified_on) VALUES
  (1,1,'P1001','Quilling Toy 1','Quilling Toy 1','430.00',false,now(),now()),
  (2,2,'P1002','Quilling Toy 2','Quilling Toy 2','490.00',false,now(),now()),
  (3,3,'P1003','Quilling Toy 3','Quilling Toy 3','400.00',false,now(),now()),
  (4,1,'P1004','Quilling Toy 4','Quilling Toy 4','430.00',false,now(),now()),
  (5,2,'P1005','Quilling Toy 5','Quilling Toy 5','750.00',false,now(),now())
;

INSERT INTO customers (id,firstname,lastname,email,phone,password, created_on, modified_on)
VALUES
  (1,'Anna','Smith','anna@gmail.com','999999999','$2a$10$vacuqbDw9I7rr6RRH8sByuktOzqTheQMfnK3XCT2WlaL7vt/3AMby',now(),now()),
  (2,'Jack','Porter','jack@gmail.com','8888888888','$2a$10$vacuqbDw9I7rr6RRH8sByuktOzqTheQMfnK3XCT2WlaL7vt/3AMby',now(),now())
;

insert into addresses (id, address_line1, address_line2, city, state, zip_code, country, created_on, modified_on) values
  (1, 'Hitech City', 'Cyberabad', 'Hyderabad', 'TS', '500088', 'IN',now(),now());

insert into payments (id, cc_number, cvv, amount, created_on, modified_on) values
  (1, '1111111111111111', '123', 430.00,now(),now());

insert into orders (id, order_number, cust_id, billing_addr_id, delivery_addr_id, payment_id, status, created_on, modified_on) values
  (1, '1447737431927', 1, 1, 1, 1, 'NEW', now(),now());

insert into order_items (id, order_id, price, product_id, quantity, created_on, modified_on) values
  (1,1, 430.00, 1, 1,now(),now());