-- import.sql

-- ROLES (id manual, porque seu Role não tem @GeneratedValue)
INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_TECNICO');
INSERT INTO tb_role (id, authority) VALUES (3, 'ROLE_CLIENTE');

-- USERS (id auto, porque User tem @GeneratedValue)
INSERT INTO tb_user (email, name, password, phone) VALUES ('marcos.souza932@gmail.com', 'marcos_souza932', 'Vento#7391Aa', '+55 11 99876-4321');
INSERT INTO tb_user (email, name, password, phone) VALUES ('ana.pereira441@gmail.com', 'ana_pereira441', 'Ferro!5820Bb', '+55 21 98765-1234');
INSERT INTO tb_user (email, name, password, phone) VALUES ('joao.silva120@gmail.com', 'joao_silva120', 'Cafe@9132Cc', '+55 31 97654-7788');

-- RELACIONAMENTO (tb_user_role)
-- Assumindo que os users acima ficaram com id 1, 2, 3 nessa ordem
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1); -- marcos -> admin
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 2); -- marcos -> tecnico

INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 3); -- ana -> cliente

INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 2); -- joao -> tecnico
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 3); -- joao -> cliente
