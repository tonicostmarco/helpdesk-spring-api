-- import.sql (H2). One statement per line.

DELETE FROM tb_ticket_category;
DELETE FROM tb_user_role;
DELETE FROM tb_ticket;
DELETE FROM tb_category;
DELETE FROM tb_user;
DELETE FROM tb_role;

ALTER TABLE tb_user ALTER COLUMN id RESTART WITH 1;
ALTER TABLE tb_category ALTER COLUMN id RESTART WITH 1;
ALTER TABLE tb_ticket ALTER COLUMN id RESTART WITH 1;

INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_SUPPORT');
INSERT INTO tb_role (id, authority) VALUES (3, 'ROLE_CLIENT');

INSERT INTO tb_user (name, email, phone, password) VALUES ('Marco Ricardo', 'marco.ricardo01@gmail.com', '+55 11 98888-1001', 'Senha!A1');
INSERT INTO tb_user (name, email, phone, password) VALUES ('Ana Souza', 'ana.souza02@gmail.com', '+55 11 98888-1002', 'Senha!B2');
INSERT INTO tb_user (name, email, phone, password) VALUES ('Bruno Lima', 'bruno.lima03@gmail.com', '+55 11 98888-1003', 'Senha!C3');
INSERT INTO tb_user (name, email, phone, password) VALUES ('Carla Santos', 'carla.santos04@gmail.com', '+55 11 98888-1004', 'Senha!D4');
INSERT INTO tb_user (name, email, phone, password) VALUES ('Diego Alves', 'diego.alves05@gmail.com', '+55 11 98888-1005', 'Senha!E5');
INSERT INTO tb_user (name, email, phone, password) VALUES ('Eva Martins', 'eva.martins06@gmail.com', '+55 11 98888-1006', 'Senha!F6');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 3);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 3);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (5, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (6, 3);

INSERT INTO tb_category (name) VALUES ('Internet');
INSERT INTO tb_category (name) VALUES ('Wi-Fi');
INSERT INTO tb_category (name) VALUES ('Router');
INSERT INTO tb_category (name) VALUES ('Slowness');
INSERT INTO tb_category (name) VALUES ('Installation');
INSERT INTO tb_category (name) VALUES ('Billing');
INSERT INTO tb_category (name) VALUES ('System');
INSERT INTO tb_category (name) VALUES ('Access');
INSERT INTO tb_category (name) VALUES ('Email');
INSERT INTO tb_category (name) VALUES ('Telephony');

-- Enums as ordinal (INT)
-- Priority: LOW=0, MEDIUM=1, HIGH=2
-- Status:   OPEN=0, IN_PROGRESS=1, RESOLVED=2, CLOSED=3
-- created_at is WITHOUT TZ, updated_at is WITH TZ in your schema
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) VALUES ('No wired internet', 'Desktop without connectivity. Modem OK.', '2026-02-01 10:15:00', '2026-02-01 10:15:00+00', 2, 0, 2);
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) VALUES ('Wi-Fi unstable', 'Connects and drops. Weak signal.', '2026-02-01 11:40:00', '2026-02-01 12:05:00+00', 1, 1, 3);
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) VALUES ('Slow connection at night', 'High ping after 8 PM.', '2026-02-01 20:10:00', '2026-02-01 21:00:00+00', 1, 1, 3);
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) VALUES ('System login error', 'Authentication fails.', '2026-02-02 09:00:00', '2026-02-02 09:30:00+00', 2, 2, 6);
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) VALUES ('Email not sending', 'SMTP failure. Receiving works.', '2026-02-02 14:20:00', '2026-02-02 15:10:00+00', 0, 3, 2);

INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (1, 1);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (3, 1);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (2, 2);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (3, 2);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (4, 3);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (1, 3);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (7, 4);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (8, 4);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (9, 5);
