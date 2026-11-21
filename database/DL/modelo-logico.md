# Modelo lógico do banco

O diagrama principal representa a base após o schema inicial e as migrações das etapas 4 e 8. As extensões da v1.6.2 e da v1.6.3 estão descritas abaixo. `PK` indica chave primária, `FK` chave estrangeira e `UK` unicidade. Os comentários indicam tamanhos, nulidade e valores padrão; todos os IDs são `BIGINT UNSIGNED AUTO_INCREMENT`.

```mermaid
erDiagram
    CONTATOS o|--o| USUARIOS : cliente_id
    USUARIOS o|--o{ PEDIDOS : usuario_id
    CONTATOS o|--o{ PEDIDOS : contato_id
    CATEGORIAS o|--o{ PRODUTOS : categoria_id
    PEDIDOS ||--o{ PEDIDO_ITENS : pedido_id
    PRODUTOS ||--o{ PEDIDO_ITENS : produto_id
    PRODUTOS ||--o{ MOVIMENTACOES_ESTOQUE : produto_id
    CONTATOS o|--o{ MOVIMENTACOES_ESTOQUE : fornecedor_id
    USUARIOS ||--o{ AVALIACOES : usuario_id
    PRODUTOS ||--o{ AVALIACOES : produto_id

    USUARIOS {
        BIGINT id PK "UNSIGNED, NOT NULL, AUTO_INCREMENT"
        VARCHAR login UK "100, NOT NULL"
        VARCHAR senha "255, NOT NULL"
        ENUM perfil "CLIENTE ou ADMIN, NOT NULL"
        TIMESTAMP criado_em "NOT NULL, CURRENT_TIMESTAMP"
        VARCHAR nome "150, NULL"
        BOOLEAN ativo "NOT NULL, DEFAULT TRUE"
        BIGINT cliente_id FK, UK "UNSIGNED, NULL, referencias contatos.id"
    }
    CONTATOS {
        BIGINT id PK "UNSIGNED, NOT NULL, AUTO_INCREMENT"
        ENUM tipo "CLIENTE ou FORNECEDOR, NOT NULL"
        VARCHAR nome "150, NOT NULL"
        VARCHAR telefone "30, NULL"
        VARCHAR email "255, NULL"
        VARCHAR documento "30, NULL"
        VARCHAR endereco "255, NULL"
        TEXT observacoes "NULL"
        VARCHAR whatsapp "30, NULL"
        VARCHAR ponto_referencia "255, NULL"
        VARCHAR nome_empresa "150, NULL"
        ENUM categoria_insumo "NULL"
        INT prazo_entrega_dias "UNSIGNED, NULL"
    }
    CATEGORIAS {
        BIGINT id PK "UNSIGNED, NOT NULL, AUTO_INCREMENT"
        VARCHAR nome UK "100, NOT NULL"
        TEXT descricao "NULL"
    }
    PRODUTOS {
        BIGINT id PK "UNSIGNED, NOT NULL, AUTO_INCREMENT"
        BIGINT categoria_id FK "UNSIGNED, NULL"
        VARCHAR nome "150, NOT NULL"
        TEXT descricao "NULL"
        DECIMAL preco_venda "10,2; NOT NULL; maior ou igual a zero"
        DECIMAL custo_producao "10,2; NOT NULL; DEFAULT 0; maior ou igual a zero"
        DECIMAL estoque_atual "12,3; NOT NULL; DEFAULT 0; maior ou igual a zero"
        DECIMAL estoque_minimo "12,3; NOT NULL; DEFAULT 0; maior ou igual a zero"
        VARCHAR url_imagem "2048, NULL"
    }
    PEDIDOS {
        BIGINT id PK "UNSIGNED, NOT NULL, AUTO_INCREMENT"
        BIGINT usuario_id FK "UNSIGNED, NULL"
        BIGINT contato_id FK "UNSIGNED, NULL"
        ENUM status "NOT NULL, DEFAULT PENDENTE"
        ENUM tipo_entrega "RETIRADA ou ENTREGA, NOT NULL"
        VARCHAR endereco_entrega "255, NULL"
        TIMESTAMP criado_em "NOT NULL, CURRENT_TIMESTAMP"
        ENUM origem "VITRINE ou PDV; NOT NULL; DEFAULT VITRINE"
        ENUM forma_pagamento "NULL"
        DECIMAL valor_recebido "10,2; NULL"
        DECIMAL troco "10,2; NULL"
    }
    PEDIDO_ITENS {
        BIGINT id PK "UNSIGNED, NOT NULL, AUTO_INCREMENT"
        BIGINT pedido_id FK "UNSIGNED, NOT NULL"
        BIGINT produto_id FK "UNSIGNED, NOT NULL"
        DECIMAL quantidade "12,3; NOT NULL; maior que zero"
        DECIMAL preco_unitario "10,2; NOT NULL; maior ou igual a zero"
        DECIMAL custo_unitario "10,2; NULL"
    }
    MOVIMENTACOES_ESTOQUE {
        BIGINT id PK "UNSIGNED, NOT NULL, AUTO_INCREMENT"
        BIGINT produto_id FK "UNSIGNED, NOT NULL"
        ENUM tipo "ENTRADA ou SAIDA, NOT NULL"
        DECIMAL quantidade "12,3; NOT NULL; maior que zero"
        VARCHAR observacao "255, NULL"
        TIMESTAMP criado_em "NOT NULL, CURRENT_TIMESTAMP"
        BIGINT fornecedor_id FK "UNSIGNED, NULL; referencias contatos.id"
        ENUM motivo "NULL"
        DECIMAL custo_unitario "10,2; NULL"
    }
    AVALIACOES {
        BIGINT id PK "UNSIGNED, NOT NULL, AUTO_INCREMENT"
        BIGINT usuario_id FK "UNSIGNED, NOT NULL"
        BIGINT produto_id FK "UNSIGNED, NOT NULL"
        TINYINT nota "UNSIGNED, NOT NULL; entre 1 e 5"
        TEXT comentario "NULL"
        TEXT resposta_admin "NULL"
        TIMESTAMP criado_em "NOT NULL, CURRENT_TIMESTAMP"
    }
```

## Domínios e restrições

- `pedidos.status`: `PENDENTE`, `PREPARANDO`, `PRONTO`, `ENTREGUE`, `CANCELADO`.
- `pedidos.forma_pagamento`: `DINHEIRO`, `PIX`, `CARTAO_CREDITO`, `CARTAO_DEBITO` ou `NULL`.
- `contatos.categoria_insumo`: `FARINHA`, `OLEO`, `FRANGO`, `QUEIJO`, `EMBALAGENS`, `BEBIDAS`, `OUTROS` ou `NULL`.
- `movimentacoes_estoque.motivo`: `VENDA`, `LOTE`, `PERDA`, `AJUSTE` ou `NULL`.
- Os checks SQL cobrem preços e estoque não negativos, quantidades positivas e notas de 1 a 5. Outras regras, como o tipo de contato vinculado, endereço de entrega e elegibilidade da avaliação, são aplicadas pelo código.
- A chave `usuarios.cliente_id` é única quando preenchida. Não há `ON DELETE CASCADE` declarado nos scripts.
- Os custos unitários opcionais preservam o desconhecimento de dados históricos anteriores à migração; não devem ser interpretados automaticamente como zero.

## Extensão operacional da v1.6.2

| Tabela | Campos adicionados ou alterados |
|---|---|
| `pedidos` | `status` passa a incluir `SAIU_ENTREGA`; `nome_entregador` é `VARCHAR(100)` opcional. A aplicação permite despachar somente `ENTREGA`, a partir de `PRONTO`. |
| `produtos` | `disponivel_venda BOOLEAN NOT NULL DEFAULT TRUE`, independente do estoque interno. |
| `pedido_itens` | `quantidade_baixada DECIMAL(12,3)` indica a baixa física efetiva. `NULL` identifica itens antigos que baixavam a quantidade integral. |
| `configuracao_loja` | Chave primária `TINYINT` restrita a `1`; `aberta_manualmente` e `respeitar_horario_automatico` padrão `TRUE`; `horario_abertura` e `horario_fechamento` são `TIME` distintos, com padrão `08:00:00` e `23:00:00`. |

## Extensão de cadastro, personalização e VIP da v1.6.3

| Tabela | Campos e regras |
|---|---|
| `usuarios` | `login` passa a `VARCHAR(254)` e permanece único. O novo `email` é único quando preenchido; a restrição exige perfil `CLIENTE` e igualdade exata entre `login` e `email`. Logins legados continuam válidos. |
| `pedido_itens` | `tipo_personalizacao` inclui `COMBO_SIMPLES`, `CAIXA_25`, `CAIXA_40` e `CAIXA_60`. `filtros_caixa` guarda as listas de IDs excluídos em JSON. Os tipos antigos permanecem legíveis. |
| `planos_assinatura` | O único plano ativo permitido é `Clube VIP Ui K'Fomi`, a R$ 99,90 por mês e 15% de desconto. |
| `assinaturas_clientes` e `mensalidades_clube` | A assinatura passa a referenciar o VIP sem reiniciar a vigência. Faturas anteriores continuam ligadas ao plano original, com valor e referência preservados. |
