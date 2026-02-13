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

-- Categories (name + description). Includes "Other" for unmatched cases.
INSERT INTO tb_category (name, description) VALUES ('Internet', 'No internet access on wired connection or browsing is down even though the link seems up.');
INSERT INTO tb_category (name, description) VALUES ('Wi-Fi', 'Wireless connection drops, signal is weak, cannot connect to the SSID, or the connection is unstable.');
INSERT INTO tb_category (name, description) VALUES ('Router', 'Router is misconfigured, rebooting, showing abnormal LEDs, or LAN ports have no link / do not work.');
INSERT INTO tb_category (name, description) VALUES ('Slowness', 'Slow speed, high latency, packet loss, or performance is worse at specific times like nighttime.');
INSERT INTO tb_category (name, description) VALUES ('Installation', 'New installation, activation, relocation, cabling issues, ONT/ONU setup, or technician visit needed.');
INSERT INTO tb_category (name, description) VALUES ('Billing', 'Invoice issues such as wrong charges, overdue bill, mismatched amounts, payment not recognized, or second copy needed.');
INSERT INTO tb_category (name, description) VALUES ('System', 'System errors, crashes, service failures, integration problems, or inconsistent data in the application.');
INSERT INTO tb_category (name, description) VALUES ('Access', 'Account access problems such as login failure, blocked account, password reset, or missing permissions.');
INSERT INTO tb_category (name, description) VALUES ('Email', 'Email issues like cannot send/receive, SMTP/IMAP errors, client configuration problems, or authentication failures.');
INSERT INTO tb_category (name, description) VALUES ('Telephony', 'Phone service issues such as no dial tone, noise, no audio, dropped calls, SIP/ATA problems, or caller ID issues.');
INSERT INTO tb_category (name, description) VALUES ('Other', 'None of the above');

-- Enums as ordinal (INT)
-- Priority: LOW=0, MEDIUM=1, HIGH=2
-- Status:   OPEN=0, IN_PROGRESS=1, RESOLVED=2, CLOSED=3
-- created_at is Instant -> Hibernate uses TIMESTAMP WITHOUT TIME ZONE (as in your mapping)
-- updated_at is Instant (with TZ depends on dialect). Keep consistent with your schema.

INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) VALUES ('No wired internet', 'Desktop has no connectivity. Modem lights look normal.', '2026-02-01 10:15:00', '2026-02-01 10:15:00+00', 2, 0, 2);
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) VALUES ('Wi-Fi unstable', 'Device connects and disconnects frequently. Signal is weak in the bedroom.', '2026-02-01 11:40:00', '2026-02-01 12:05:00+00', 1, 1, 3);
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) VALUES ('Slow connection at night', 'Latency increases after 8 PM. Streaming buffers a lot.', '2026-02-01 20:10:00', '2026-02-01 21:00:00+00', 1, 1, 3);
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) VALUES ('System login error', 'User cannot authenticate. Error shown after submitting credentials.', '2026-02-02 09:00:00', '2026-02-02 09:30:00+00', 2, 2, 6);
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) VALUES ('Email not sending', 'SMTP error when sending. Receiving still works.', '2026-02-02 14:20:00', '2026-02-02 15:10:00+00', 0, 3, 2);

-- Ticket <-> Category (many-to-many)
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (1, 1);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (3, 1);

INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (2, 2);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (3, 2);

INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (4, 3);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (1, 3);

INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (7, 4);
INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (8, 4);

INSERT INTO tb_ticket_category (category_id, ticket_id) VALUES (9, 5);
