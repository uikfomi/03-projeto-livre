-- Migração única da Etapa 4. Execute após schema.sql; faça backup antes em bancos com dados reais.
-- Contatos de fornecedores preexistentes precisam dos campos adicionais preenchidos antes de serem carregados.
-- Custos históricos antigos permanecem NULL; relatórios não inferem valores inexistentes.
USE salgaderia_db;
ALTER TABLE contatos
    ADD COLUMN observacoes TEXT,
    ADD COLUMN whatsapp VARCHAR(30),
    ADD COLUMN ponto_referencia VARCHAR(255),
    ADD COLUMN nome_empresa VARCHAR(150),
    ADD COLUMN categoria_insumo ENUM('FARINHA','OLEO','FRANGO','QUEIJO','EMBALAGENS','BEBIDAS','OUTROS'),
    ADD COLUMN prazo_entrega_dias INT UNSIGNED;
ALTER TABLE usuarios
    ADD COLUMN nome VARCHAR(150),
    ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN cliente_id BIGINT UNSIGNED,
    ADD CONSTRAINT fk_usuario_cliente FOREIGN KEY (cliente_id) REFERENCES contatos(id),
    ADD CONSTRAINT uq_usuario_cliente UNIQUE (cliente_id);
UPDATE usuarios SET nome = login WHERE nome IS NULL;
-- O usuário inicial cliente precisa de um contato para preservar o vínculo obrigatório do domínio.
INSERT INTO contatos (tipo, nome) SELECT 'CLIENTE', login FROM usuarios WHERE login = 'root' AND perfil = 'CLIENTE' AND cliente_id IS NULL;
UPDATE usuarios SET cliente_id = LAST_INSERT_ID()
    WHERE login = 'root' AND perfil = 'CLIENTE' AND cliente_id IS NULL AND LAST_INSERT_ID() <> 0;
ALTER TABLE pedido_itens ADD COLUMN custo_unitario DECIMAL(10,2) NULL;
ALTER TABLE movimentacoes_estoque
    ADD COLUMN fornecedor_id BIGINT UNSIGNED,
    ADD COLUMN motivo ENUM('VENDA','LOTE','PERDA','AJUSTE'),
    ADD COLUMN custo_unitario DECIMAL(10,2) NULL,
    ADD CONSTRAINT fk_mov_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES contatos(id);
