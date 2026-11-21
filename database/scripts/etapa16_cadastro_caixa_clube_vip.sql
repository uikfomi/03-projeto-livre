-- Migração da v1.6.3. Faça backup e aplique depois da etapa 15.
USE salgaderia_db;

-- Logins legados são mantidos; contas novas espelham o endereço normalizado.
ALTER TABLE usuarios
    MODIFY COLUMN login VARCHAR(254) NOT NULL,
    ADD COLUMN email VARCHAR(254) NULL UNIQUE,
    ADD CONSTRAINT chk_usuarios_login_email CHECK (
        email IS NULL OR (perfil = 'CLIENTE' AND CAST(login AS BINARY) = CAST(email AS BINARY)));

ALTER TABLE pedido_itens
    MODIFY COLUMN tipo_personalizacao ENUM('COMBO','FOME_LEVE','FOME_DE_LEAO','COMBO_SIMPLES','CAIXA_25','CAIXA_40','CAIXA_60') NULL,
    ADD COLUMN filtros_caixa JSON NULL;

START TRANSACTION;
UPDATE planos_assinatura SET ativo = FALSE;
INSERT INTO planos_assinatura (nome, descricao, mensalidade, desconto_percentual, ativo)
VALUES ('Clube VIP Ui K''Fomi', 'Plano pré-pago mensal com frete grátis e 15% OFF nos itens elegíveis.', 99.90, 15.00, TRUE)
ON DUPLICATE KEY UPDATE
    nome = 'Clube VIP Ui K''Fomi', descricao = 'Plano pré-pago mensal com frete grátis e 15% OFF nos itens elegíveis.',
    mensalidade = 99.90, desconto_percentual = 15.00, ativo = TRUE;

-- Preserva vigências e mensalidades já emitidas com seus preços e referências originais.
UPDATE assinaturas_clientes a JOIN planos_assinatura p ON p.ativo = TRUE
SET a.plano_id = p.id,
    a.desconto_vigente = IF(NOT a.cancelada AND a.valida_ate > CURRENT_DATE, p.desconto_percentual, 0);
COMMIT;

ALTER TABLE planos_assinatura ADD CONSTRAINT chk_plano_ativo_vip CHECK (
    NOT ativo OR (nome = 'Clube VIP Ui K''Fomi' AND mensalidade = 99.90 AND desconto_percentual = 15.00));
