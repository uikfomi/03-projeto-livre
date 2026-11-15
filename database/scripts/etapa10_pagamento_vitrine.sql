-- Executar após etapa9_descontos.sql. Preserve os valores históricos de crédito e débito.
USE salgaderia_db;
ALTER TABLE pedidos MODIFY COLUMN forma_pagamento
    ENUM('DINHEIRO','PIX','CARTAO_CREDITO','CARTAO_DEBITO','CARTAO') NULL;
