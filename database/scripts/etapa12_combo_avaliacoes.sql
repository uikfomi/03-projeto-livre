-- Migração após etapa11_salao_agendamento.sql. Faça backup antes de aplicar.
USE salgaderia_db;

ALTER TABLE avaliacoes ADD COLUMN foto_url VARCHAR(255) NULL;

-- No schema atual, a tabela de itens do pedido se chama pedido_itens.
ALTER TABLE pedido_itens
    ADD COLUMN observacao_personalizada VARCHAR(512) NULL,
    ADD COLUMN grupo_personalizado CHAR(36) NULL,
    ADD COLUMN tipo_personalizacao ENUM('COMBO','FOME_LEVE','FOME_DE_LEAO') NULL,
    ADD COLUMN restricoes_personalizadas VARCHAR(64) NULL;

-- NULL em ingredientes significa ficha não conferida, não ausência de ingrediente.
ALTER TABLE produtos
    ADD COLUMN tipo_combo ENUM('COXINHA','PASTEL','EMPADA','ESFIHA','ACOMPANHAMENTO') NULL,
    ADD COLUMN recheio_combo VARCHAR(100) NULL,
    ADD COLUMN contem_queijo BOOLEAN NULL,
    ADD COLUMN contem_calabresa BOOLEAN NULL,
    ADD COLUMN assado_caixa BOOLEAN NOT NULL DEFAULT FALSE;
