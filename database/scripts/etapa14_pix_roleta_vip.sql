-- Migração após etapa13_clube_gamificacao.sql. Faça backup antes de aplicar.
USE salgaderia_db;

-- valor_minimo já existe desde a etapa 9.
ALTER TABLE cupons
    MODIFY COLUMN valor_minimo DECIMAL(10,2) NOT NULL DEFAULT 0,
    ADD COLUMN valido_ate DATETIME NULL,
    ADD COLUMN brinde_descricao VARCHAR(150) NULL,
    ADD COLUMN beneficio ENUM('PERCENTUAL','FRETE','BRINDE','PONTOS_DOBRO') NOT NULL DEFAULT 'PERCENTUAL',
    ADD COLUMN valor_beneficio DECIMAL(10,2) NOT NULL DEFAULT 0,
    ADD COLUMN usado_pedido_id BIGINT UNSIGNED NULL,
    ADD INDEX (usado_pedido_id);

ALTER TABLE cupons
    DROP CHECK cupons_chk_1,
    ADD CONSTRAINT chk_cupom_percentual CHECK (
        (beneficio='PERCENTUAL' AND percentual > 0 AND percentual <= 100)
        OR (beneficio<>'PERCENTUAL' AND percentual=0)
    );

ALTER TABLE pedidos
    ADD COLUMN status_pagamento ENUM('AGUARDANDO_PIX','PAGO','NA_ENTREGA') NOT NULL DEFAULT 'NA_ENTREGA',
    ADD COLUMN transacao_pix_id VARCHAR(100) CHARACTER SET ascii COLLATE ascii_bin NULL UNIQUE,
    ADD COLUMN pix_provedor ENUM('MERCADO_PAGO','DEMO') NULL,
    ADD COLUMN pix_referencia CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NULL UNIQUE,
    ADD COLUMN pix_pagador_email VARCHAR(254) NULL,
    ADD COLUMN pix_pagador_documento VARCHAR(14) NULL,
    ADD COLUMN pix_primeira_tentativa DATETIME NULL,
    ADD COLUMN pix_payload VARCHAR(1024) NULL,
    ADD COLUMN taxa_entrega DECIMAL(10,2) NOT NULL DEFAULT 0,
    ADD COLUMN brinde_descricao VARCHAR(150) NULL,
    ADD INDEX (status_pagamento,pix_provedor,id);
UPDATE pedidos SET status_pagamento='PAGO' WHERE origem IN ('PDV','COMANDA') AND status='ENTREGUE';

ALTER TABLE roleta_premios
    MODIFY COLUMN premio ENUM('PONTOS_20','CUPOM_7','PONTOS_50','CUPOM_10','BRINDE_MINI','FRETE_GRATIS',
        'PONTOS_DOBRO','GIRO_EXTRA','SEM_PREMIO','CUPOM_15','COMBO_BRINDE') NOT NULL,
    ADD COLUMN vip BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN premio_extra ENUM('CUPOM_10','BRINDE_MINI','FRETE_GRATIS','PONTOS_DOBRO',
        'SEM_PREMIO','CUPOM_15','COMBO_BRINDE') NULL,
    ADD INDEX (usuario_id,criado_em);

UPDATE cupons c JOIN roleta_premios r ON r.cupom_codigo=c.codigo
SET c.valido_ate=DATE_ADD(r.criado_em,INTERVAL 5 DAY),c.valor_minimo=GREATEST(c.valor_minimo,25.00)
WHERE c.usuario_exclusivo_id IS NOT NULL;
