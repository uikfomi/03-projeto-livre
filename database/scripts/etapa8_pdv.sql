-- Migração única após etapa4_migracao.sql. Faça backup antes de aplicar em banco existente.
USE salgaderia_db;
ALTER TABLE pedidos
    ADD COLUMN origem ENUM('VITRINE','PDV') NOT NULL DEFAULT 'VITRINE',
    ADD COLUMN forma_pagamento ENUM('DINHEIRO','PIX','CARTAO_CREDITO','CARTAO_DEBITO') NULL,
    ADD COLUMN valor_recebido DECIMAL(10,2) NULL,
    ADD COLUMN troco DECIMAL(10,2) NULL;
