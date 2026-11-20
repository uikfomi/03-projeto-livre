USE salgaderia_db;

ALTER TABLE pedidos
    MODIFY COLUMN status ENUM('PENDENTE', 'PREPARANDO', 'PRONTO', 'ENTREGUE', 'CANCELADO', 'SAIU_ENTREGA') NOT NULL DEFAULT 'PENDENTE',
    ADD COLUMN nome_entregador VARCHAR(100) NULL;

ALTER TABLE produtos ADD COLUMN disponivel_venda BOOLEAN NOT NULL DEFAULT TRUE;

-- NULL identifica itens históricos, cuja baixa correspondia à quantidade integral.
ALTER TABLE pedido_itens ADD COLUMN quantidade_baixada DECIMAL(12,3) NULL;

CREATE TABLE configuracao_loja (
    id TINYINT NOT NULL PRIMARY KEY CHECK (id = 1),
    aberta_manualmente BOOLEAN NOT NULL DEFAULT TRUE,
    horario_abertura TIME NOT NULL DEFAULT '08:00:00',
    horario_fechamento TIME NOT NULL DEFAULT '23:00:00',
    respeitar_horario_automatico BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (horario_abertura >= '00:00:00' AND horario_abertura < '24:00:00' AND
           horario_fechamento >= '00:00:00' AND horario_fechamento < '24:00:00' AND
           horario_abertura <> horario_fechamento)
) ENGINE=InnoDB;

INSERT INTO configuracao_loja (id) VALUES (1);
