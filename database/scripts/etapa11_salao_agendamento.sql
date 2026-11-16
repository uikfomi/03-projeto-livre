-- Migração após etapa10_pagamento_vitrine.sql. Faça backup antes de aplicar.
USE salgaderia_db;
ALTER TABLE pedidos
    MODIFY COLUMN tipo_entrega ENUM('RETIRADA','ENTREGA','LOCAL') NOT NULL,
    ADD COLUMN agendado_para DATETIME NULL,
    MODIFY COLUMN origem ENUM('VITRINE','PDV','COMANDA') NOT NULL DEFAULT 'VITRINE';

CREATE TABLE comandas_mesa (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    nome VARCHAR(30) NOT NULL,
    status ENUM('LIVRE','OCUPADA') NOT NULL DEFAULT 'LIVRE'
) ENGINE=InnoDB;
INSERT INTO comandas_mesa(numero,nome) VALUES
(1,'Mesa 01'),(2,'Mesa 02'),(3,'Mesa 03'),(4,'Mesa 04'),(5,'Mesa 05'),
(6,'Mesa 06'),(7,'Mesa 07'),(8,'Mesa 08'),(9,'Mesa 09'),(10,'Mesa 10');
CREATE TABLE comanda_atendimentos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    mesa_id BIGINT UNSIGNED NOT NULL,
    aberto_em DATETIME NOT NULL,
    fechado_em DATETIME NULL,
    pedido_id BIGINT UNSIGNED NULL,
    FOREIGN KEY (mesa_id) REFERENCES comandas_mesa(id),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
) ENGINE=InnoDB;
CREATE TABLE comanda_itens (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    atendimento_id BIGINT UNSIGNED NOT NULL,
    produto_id BIGINT UNSIGNED NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL CHECK (quantidade > 0),
    preco_unitario DECIMAL(10,2) NOT NULL CHECK (preco_unitario >= 0),
    custo_unitario DECIMAL(10,2) NOT NULL CHECK (custo_unitario >= 0),
    FOREIGN KEY (atendimento_id) REFERENCES comanda_atendimentos(id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
) ENGINE=InnoDB;
CREATE TABLE comanda_pagamentos_parciais (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    atendimento_id BIGINT UNSIGNED NOT NULL,
    item_id BIGINT UNSIGNED NULL,
    quantidade DECIMAL(12,3) NULL,
    pessoa VARCHAR(100) NOT NULL,
    forma_pagamento ENUM('DINHEIRO','PIX','CARTAO','CARTAO_CREDITO','CARTAO_DEBITO') NOT NULL,
    valor DECIMAL(10,2) NOT NULL CHECK (valor > 0),
    criado_em DATETIME NOT NULL,
    FOREIGN KEY (atendimento_id) REFERENCES comanda_atendimentos(id),
    FOREIGN KEY (item_id) REFERENCES comanda_itens(id)
) ENGINE=InnoDB;
