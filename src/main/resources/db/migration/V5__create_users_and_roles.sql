-- Tabela de usuários para autenticação
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    member_id BIGINT,
    CONSTRAINT fk_user_member FOREIGN KEY (member_id) REFERENCES members(id)
);

-- Tabela de roles (papéis) para autorização
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Tabela de associação entre usuários e roles
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Inserir roles padrão
INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_MANAGER'),
('ROLE_USER');

-- Inserir usuários de exemplo (senha: "password" criptografada com BCrypt)
INSERT INTO users (username, password, enabled, member_id) VALUES
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuDk2y4ajC3CJZ6I5wql7MSvZqNB2ZyO', true, 1),
('manager', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuDk2y4ajC3CJZ6I5wql7MSvZqNB2ZyO', true, 4),
('user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuDk2y4ajC3CJZ6I5wql7MSvZqNB2ZyO', true, 2);

-- Atribuir roles aos usuários
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin tem ROLE_ADMIN
(2, 2), -- manager tem ROLE_MANAGER
(3, 3); -- user tem ROLE_USER