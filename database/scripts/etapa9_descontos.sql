-- Executar uma única vez após etapa8_pdv.sql; faça backup antes de migrar.
USE salgaderia_db;
CREATE TABLE cupons (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(40) NOT NULL UNIQUE,
    percentual DECIMAL(5,2) NOT NULL,
    valor_minimo DECIMAL(10,2) NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (percentual > 0 AND percentual <= 100),
    CHECK (valor_minimo >= 0)
) ENGINE=InnoDB;
INSERT INTO cupons(codigo, percentual, valor_minimo, ativo)
VALUES ('UIKFOMI10', 10.00, 0.00, TRUE), ('FESTA15', 15.00, 80.00, TRUE);
ALTER TABLE pedidos
    ADD COLUMN desconto DECIMAL(10,2) NOT NULL DEFAULT 0,
    ADD COLUMN estrategia_desconto VARCHAR(20) NOT NULL DEFAULT 'NENHUM',
    ADD COLUMN cupom_codigo VARCHAR(40) NULL;
