INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_NOC');
INSERT INTO tb_role (id, authority) VALUES (3, 'ROLE_SUPPORT');
INSERT INTO tb_role (id, authority) VALUES (4, 'ROLE_CLIENT');

INSERT INTO tb_user (id, name, email, phone, password) VALUES (1, 'Marco Admin', 'admin@helpdesk.com', '+55 19 90000-0001', '$2b$10$8orZfrgp/uRwNstcqzYmI.jtGSlcpLEugS0xk1wefRW2KUOkEuuf2');
INSERT INTO tb_user (id, name, email, phone, password) VALUES (2, 'Bruno NOC', 'noc@helpdesk.com', '+55 19 90000-0002', '$2b$10$Mpzz35YK9HNl9jYnCzUfGOTcCO6m9VCZN5LLf0A2h95hHgqmIbVmS');
INSERT INTO tb_user (id, name, email, phone, password) VALUES (3, 'Carla Support', 'support@helpdesk.com', '+55 19 90000-0003', '$2b$10$TrElmLFoQwvZae9QaLVIyunLhZusMt8SOYzd0OoAHP6GqHtrJAHAO');

INSERT INTO tb_user (id, name, email, phone, password) VALUES (4, 'Ana Client', 'ana.client@helpdesk.com', '+55 19 90000-0101', '$2b$10$Ty6KX0h6tq61laA6fnfrou2ndQtDTyRdA5gV5nDiXV7dZrJ3xHNVO');
INSERT INTO tb_user (id, name, email, phone, password) VALUES (5, 'Diego Client', 'diego.client@helpdesk.com', '+55 19 90000-0102', '$2b$10$r3oTgc2eRQ5pLqkCn2nV0Oh0NfitIq/hkrgOwyYcjDmGI06C55y7e');
INSERT INTO tb_user (id, name, email, phone, password) VALUES (6, 'Julia Client', 'julia.client@helpdesk.com', '+55 19 90000-0103', '$2b$10$Z1jUZ9Ut9gBDOiUvvsBfq..tZQxmdisPEb3zsJgUbDvyzpDP1XWh2');
INSERT INTO tb_user (id, name, email, phone, password) VALUES (7, 'Rafael Client', 'rafael.client@helpdesk.com', '+55 19 90000-0104', '$2b$10$gUXR3MIJqJ8bzDCvpMdyRu7Lhqu5k9vh5NO3ue9mG247pGhgExpQm');
INSERT INTO tb_user (id, name, email, phone, password) VALUES (8, 'Livia Client', 'livia.client@helpdesk.com', '+55 19 90000-0105', '$2b$10$BCRIgkfTOhnk2slbeL9AWOINkJkdxHnU7vuChCxV.DjKDcfYuqFgO');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 3);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4, 4);
INSERT INTO tb_user_role (user_id, role_id) VALUES (5, 4);
INSERT INTO tb_user_role (user_id, role_id) VALUES (6, 4);
INSERT INTO tb_user_role (user_id, role_id) VALUES (7, 4);
INSERT INTO tb_user_role (user_id, role_id) VALUES (8, 4);

INSERT INTO tb_category (id, name, description) VALUES (1, 'DNS', 'Sites nao abrem por nome, mas abrem por IP, ou nslookup falha.');
INSERT INTO tb_category (id, name, description) VALUES (2, 'PPPoE', 'Sem autenticacao PPPoE, usuario e senha incorretos, sessao cai.');
INSERT INTO tb_category (id, name, description) VALUES (3, 'WiFi', 'Sinal fraco, quedas, canal congestionado, senha errada.');
INSERT INTO tb_category (id, name, description) VALUES (4, 'Cabo', 'Sem link na porta, conector danificado, cabo torto ou partido.');
INSERT INTO tb_category (id, name, description) VALUES (5, 'Other', 'Problema fora das categorias, precisa triagem.');

INSERT INTO tb_ticket (id, title, description, created_at, updated_at, priority, status, client_id) VALUES (1, 'Internet cai do nada', 'Cai a cada 10 minutos. Modem reinicia sozinho.', '2026-02-18 10:10:00', '2026-02-18 10:10:00', 1, 0, 4);
INSERT INTO tb_ticket (id, title, description, created_at, updated_at, priority, status, client_id) VALUES (2, 'Nao resolve nomes', 'Ping em 8.8.8.8 funciona, google.com nao.', '2026-02-18 10:20:00', '2026-02-18 10:20:00', 2, 0, 5);
INSERT INTO tb_ticket (id, title, description, created_at, updated_at, priority, status, client_id) VALUES (3, 'WiFi lento no quarto', 'No roteador fica ok, no quarto fica ruim.', '2026-02-18 10:30:00', '2026-02-18 10:30:00', 0, 0, 6);
INSERT INTO tb_ticket (id, title, description, created_at, updated_at, priority, status, client_id) VALUES (4, 'Sem link na ethernet', 'WiFi funciona. Cabo na TV nao acende LED.', '2026-02-18 10:40:00', '2026-02-18 10:40:00', 1, 0, 7);
INSERT INTO tb_ticket (id, title, description, created_at, updated_at, priority, status, client_id) VALUES (5, 'PPPoE nao conecta', 'Erro de autenticacao. Usuario e senha conferidos.', '2026-02-18 10:50:00', '2026-02-18 10:50:00', 2, 0, 8);

INSERT INTO tb_ticket_category (ticket_id, category_id) VALUES (1, 5);
INSERT INTO tb_ticket_category (ticket_id, category_id) VALUES (2, 1);
INSERT INTO tb_ticket_category (ticket_id, category_id) VALUES (3, 3);
INSERT INTO tb_ticket_category (ticket_id, category_id) VALUES (4, 4);
INSERT INTO tb_ticket_category (ticket_id, category_id) VALUES (5, 2);
