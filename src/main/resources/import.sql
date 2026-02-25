INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_NOC');
INSERT INTO tb_role (id, authority) VALUES (3, 'ROLE_SUPPORT');
INSERT INTO tb_role (id, authority) VALUES (4, 'ROLE_CLIENT');

INSERT INTO tb_user (name, email, ddd, phone, password) VALUES ('Marco Admin', 'admin@helpdesk.com', 11, '991731543', '$2b$10$8orZfrgp/uRwNstcqzYmI.jtGSlcpLEugS0xk1wefRW2KUOkEuuf2');
INSERT INTO tb_user (name, email, ddd, phone, password) VALUES ('Bruno NOC', 'noc@helpdesk.com', 11, '900000002', '$2b$10$Mpzz35YK9HNl9jYnCzUfGOTcCO6m9VCZN5LLf0A2h95hHgqmIbVmS');
INSERT INTO tb_user (name, email, ddd, phone, password) VALUES ('Carla Support', 'support@helpdesk.com', 11, '900000003', '$2b$10$TrElmLFoQwvZae9QaLVIyunLhZusMt8SOYzd0OoAHP6GqHtrJAHAO');
INSERT INTO tb_user (name, email, ddd, phone, password) VALUES ('Ana Client', 'ana.client@helpdesk.com', 11, '900000101', '$2b$10$Ty6KX0h6tq61laA6fnfrou2ndQtDTyRdA5gV5nDiXV7dZrJ3xHNVO');
INSERT INTO tb_user (name, email, ddd, phone, password) VALUES ('Diego Client', 'diego.client@helpdesk.com', 11, '900000102', '$2b$10$r3oTgc2eRQ5pLqkCn2nV0Oh0NfitIq/hkrgOwyYcjDmGI06C55y7e');
INSERT INTO tb_user (name, email, ddd, phone, password) VALUES ('Julia Client', 'julia.client@helpdesk.com', 19, '991731543', '$2b$10$Z1jUZ9Ut9gBDOiUvvsBfq..tZQxmdisPEb3zsJgUbDvyzpDP1XWh2');
INSERT INTO tb_user (name, email, ddd, phone, password) VALUES ('Rafael Client', 'rafael.client@helpdesk.com', 11, '900000104', '$2b$10$gUXR3MIJqJ8bzDCvpMdyRu7Lhqu5k9vh5NO3ue9mG247pGhgExpQm');
INSERT INTO tb_user (name, email, ddd, phone, password) VALUES ('Livia Client', 'livia.client@helpdesk.com', 11, '900000105', '$2b$10$BCRIgkfTOhnk2slbeL9AWOINkJkdxHnU7vuChCxV.DjKDcfYuqFgO');

INSERT INTO tb_user_role (user_id, role_id) SELECT u.id, 1 FROM tb_user u WHERE u.email='admin@helpdesk.com';
INSERT INTO tb_user_role (user_id, role_id) SELECT u.id, 2 FROM tb_user u WHERE u.email='noc@helpdesk.com';
INSERT INTO tb_user_role (user_id, role_id) SELECT u.id, 3 FROM tb_user u WHERE u.email='support@helpdesk.com';
INSERT INTO tb_user_role (user_id, role_id) SELECT u.id, 4 FROM tb_user u WHERE u.email='ana.client@helpdesk.com';
INSERT INTO tb_user_role (user_id, role_id) SELECT u.id, 4 FROM tb_user u WHERE u.email='diego.client@helpdesk.com';
INSERT INTO tb_user_role (user_id, role_id) SELECT u.id, 4 FROM tb_user u WHERE u.email='julia.client@helpdesk.com';
INSERT INTO tb_user_role (user_id, role_id) SELECT u.id, 4 FROM tb_user u WHERE u.email='rafael.client@helpdesk.com';
INSERT INTO tb_user_role (user_id, role_id) SELECT u.id, 4 FROM tb_user u WHERE u.email='livia.client@helpdesk.com';

INSERT INTO tb_category (name, description) VALUES ('DNS', 'Sites nao abrem por nome, mas abrem por IP, ou nslookup falha.');
INSERT INTO tb_category (name, description) VALUES ('PPPoE', 'Sem autenticacao PPPoE, usuario e senha incorretos, sessao cai.');
INSERT INTO tb_category (name, description) VALUES ('WiFi', 'Sinal fraco, quedas, canal congestionado, senha errada.');
INSERT INTO tb_category (name, description) VALUES ('Cabo', 'Sem link na porta, conector danificado, cabo torto ou partido.');
INSERT INTO tb_category (name, description) VALUES ('Other', 'Problema fora das categorias, precisa triagem.');

INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) SELECT 'Internet cai do nada','Cai a cada 10 minutos. Modem reinicia sozinho.','2026-02-18 10:10:00','2026-02-18 10:10:00',1,0,u.id FROM tb_user u WHERE u.email='ana.client@helpdesk.com';
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) SELECT 'Nao resolve nomes','Ping em 8.8.8.8 funciona, google.com nao.','2026-02-18 10:20:00','2026-02-18 10:20:00',2,0,u.id FROM tb_user u WHERE u.email='diego.client@helpdesk.com';
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) SELECT 'WiFi lento no quarto','No roteador fica ok, no quarto fica ruim.','2026-02-18 10:30:00','2026-02-18 10:30:00',0,0,u.id FROM tb_user u WHERE u.email='julia.client@helpdesk.com';
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) SELECT 'Sem link na ethernet','WiFi funciona. Cabo na TV nao acende LED.','2026-02-18 10:40:00','2026-02-18 10:40:00',1,0,u.id FROM tb_user u WHERE u.email='rafael.client@helpdesk.com';
INSERT INTO tb_ticket (title, description, created_at, updated_at, priority, status, client_id) SELECT 'PPPoE nao conecta','Erro de autenticacao. Usuario e senha conferidos.','2026-02-18 10:50:00','2026-02-18 10:50:00',2,0,u.id FROM tb_user u WHERE u.email='livia.client@helpdesk.com';

INSERT INTO tb_ticket_category (ticket_id, category_id) SELECT t.id, c.id FROM tb_ticket t, tb_category c WHERE t.title='Internet cai do nada' AND c.name='Other';
INSERT INTO tb_ticket_category (ticket_id, category_id) SELECT t.id, c.id FROM tb_ticket t, tb_category c WHERE t.title='Nao resolve nomes' AND c.name='DNS';
INSERT INTO tb_ticket_category (ticket_id, category_id) SELECT t.id, c.id FROM tb_ticket t, tb_category c WHERE t.title='WiFi lento no quarto' AND c.name='WiFi';
INSERT INTO tb_ticket_category (ticket_id, category_id) SELECT t.id, c.id FROM tb_ticket t, tb_category c WHERE t.title='Sem link na ethernet' AND c.name='Cabo';
INSERT INTO tb_ticket_category (ticket_id, category_id) SELECT t.id, c.id FROM tb_ticket t, tb_category c WHERE t.title='PPPoE nao conecta' AND c.name='PPPoE';