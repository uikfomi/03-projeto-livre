CREATE DATABASE IF NOT EXISTS salgaderia_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE salgaderia_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('CLIENTE', 'ADMIN') NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Credenciais iniciais exigidas, somente para desenvolvimento; não usar em produção.
INSERT INTO usuarios (login, senha, perfil) VALUES
    ('root', 'toor', 'CLIENTE'),
    ('toor', 'root', 'ADMIN')
ON DUPLICATE KEY UPDATE login = login;

CREATE TABLE IF NOT EXISTS contatos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tipo ENUM('CLIENTE', 'FORNECEDOR') NOT NULL,
    nome VARCHAR(150) NOT NULL,
    telefone VARCHAR(30), email VARCHAR(255), documento VARCHAR(30), endereco VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS produtos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    categoria_id BIGINT UNSIGNED,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    preco_venda DECIMAL(10,2) NOT NULL CHECK (preco_venda >= 0),
    custo_producao DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (custo_producao >= 0),
    estoque_atual DECIMAL(12,3) NOT NULL DEFAULT 0 CHECK (estoque_atual >= 0),
    estoque_minimo DECIMAL(12,3) NOT NULL DEFAULT 0 CHECK (estoque_minimo >= 0),
    url_imagem VARCHAR(2048),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pedidos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNSIGNED,
    contato_id BIGINT UNSIGNED,
    status ENUM('PENDENTE', 'PREPARANDO', 'PRONTO', 'ENTREGUE', 'CANCELADO') NOT NULL DEFAULT 'PENDENTE',
    tipo_entrega ENUM('RETIRADA', 'ENTREGA') NOT NULL,
    endereco_entrega VARCHAR(255),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (contato_id) REFERENCES contatos(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pedido_itens (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT UNSIGNED NOT NULL,
    produto_id BIGINT UNSIGNED NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL CHECK (quantidade > 0),
    preco_unitario DECIMAL(10,2) NOT NULL CHECK (preco_unitario >= 0),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS movimentacoes_estoque (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    produto_id BIGINT UNSIGNED NOT NULL,
    tipo ENUM('ENTRADA', 'SAIDA') NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL CHECK (quantidade > 0),
    observacao VARCHAR(255),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS avaliacoes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNSIGNED NOT NULL,
    produto_id BIGINT UNSIGNED NOT NULL,
    nota TINYINT UNSIGNED NOT NULL CHECK (nota BETWEEN 1 AND 5),
    comentario TEXT,
    resposta_admin TEXT,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
) ENGINE=InnoDB;
