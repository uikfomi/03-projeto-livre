-- Migração após etapa12_combo_avaliacoes.sql. Faça backup antes de aplicar.
USE salgaderia_db;

CREATE TABLE planos_assinatura (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(500) NOT NULL,
    mensalidade DECIMAL(10,2) NOT NULL CHECK (mensalidade > 0),
    desconto_percentual DECIMAL(5,2) NOT NULL CHECK (desconto_percentual > 0 AND desconto_percentual <= 100),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE assinaturas_clientes (
    usuario_id BIGINT UNSIGNED PRIMARY KEY,
    plano_id BIGINT UNSIGNED NOT NULL,
    valida_ate DATE NULL,
    desconto_vigente DECIMAL(5,2) NOT NULL DEFAULT 0,
    cancelada BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (plano_id) REFERENCES planos_assinatura(id)
) ENGINE=InnoDB;

CREATE TABLE mensalidades_clube (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNSIGNED NOT NULL,
    plano_id BIGINT UNSIGNED NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    desconto_percentual DECIMAL(5,2) NOT NULL,
    status ENUM('PENDENTE','CONFIRMADA','CANCELADA') NOT NULL DEFAULT 'PENDENTE',
    solicitada_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmada_em DATETIME NULL,
    confirmada_por BIGINT UNSIGNED NULL,
    periodo_ate DATE NULL,
    FOREIGN KEY (usuario_id) REFERENCES assinaturas_clientes(usuario_id) ON DELETE CASCADE,
    FOREIGN KEY (plano_id) REFERENCES planos_assinatura(id),
    FOREIGN KEY (confirmada_por) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX (usuario_id,status)
) ENGINE=InnoDB;

INSERT INTO planos_assinatura(nome,descricao,mensalidade,desconto_percentual) VALUES
('Fome Semanal','Desconto nas compras da semana, com vigência e pagamento mensais.',19.90,5.00),
('Festa Mensal','Desconto maior nas compras, com vigência e pagamento mensais.',39.90,10.00);

CREATE TABLE conquistas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(32) NOT NULL UNIQUE,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255) NOT NULL
) ENGINE=InnoDB;
INSERT INTO conquistas(codigo,nome,descricao) VALUES
('PRIMEIRO_PEDIDO','Primeiro Pedido','Concluiu o primeiro pedido.'),
('REI_DO_CENTO','Rei do Cento','Recebeu um pedido com pelo menos 100 salgados elegíveis.'),
('CLIENTE_FIEL','Cliente Fiel','Recebeu pelo menos três pedidos.');

CREATE TABLE cliente_conquistas (
    usuario_id BIGINT UNSIGNED NOT NULL,
    conquista_id BIGINT UNSIGNED NOT NULL,
    desbloqueada_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id,conquista_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (conquista_id) REFERENCES conquistas(id)
) ENGINE=InnoDB;

ALTER TABLE cupons
    ADD COLUMN usuario_exclusivo_id BIGINT UNSIGNED NULL,
    ADD FOREIGN KEY (usuario_exclusivo_id) REFERENCES usuarios(id) ON DELETE CASCADE;

ALTER TABLE pedidos ADD COLUMN roleta_liberada BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE roleta_premios (
    pedido_id BIGINT UNSIGNED PRIMARY KEY,
    usuario_id BIGINT UNSIGNED NOT NULL,
    premio ENUM('PONTOS_20','CUPOM_7','PONTOS_50','CUPOM_10') NOT NULL,
    cupom_codigo VARCHAR(40) NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX (usuario_id)
) ENGINE=InnoDB;
