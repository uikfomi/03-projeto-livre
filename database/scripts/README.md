[🏠 Início](../../README.md) / [⬆️ database](../README.md) / 📂 scripts

# 📜 Scripts de banco

Contém o schema e os scripts SQL de evolução da persistência.

Revise o conteúdo e a ordem das migrações; instruções ALTER podem não ser idempotentes e devem ser aplicadas após backup.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [etapa10_pagamento_vitrine.sql](etapa10_pagamento_vitrine.sql) | Amplia forma de pagamento para cartão genérico. |
| [etapa11_salao_agendamento.sql](etapa11_salao_agendamento.sql) | Migração de agendamento, mesas, comandas e pagamentos parciais. |
| [etapa12_combo_avaliacoes.sql](etapa12_combo_avaliacoes.sql) | Acrescenta a ficha do catálogo, personalização dos itens e caminhos das fotos de avaliações. |
| [etapa4_migracao.sql](etapa4_migracao.sql) | Migração de contatos, usuários e custos históricos. |
| [etapa8_pdv.sql](etapa8_pdv.sql) | Colunas de origem e pagamento do PDV. |
| [etapa9_descontos.sql](etapa9_descontos.sql) | Cupons e registro de descontos por pedido. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [schema.sql](schema.sql) | Schema inicial MySQL usado pelo respectivo repositório. |

## Árvore estrutural comentada

O ponto inicial é `database/scripts/`.

```text
database/scripts/  # Contém o schema e os scripts SQL de evolução da persistência.
├── etapa10_pagamento_vitrine.sql  # Amplia forma de pagamento para cartão genérico.
├── etapa11_salao_agendamento.sql  # Migração de agendamento, mesas, comandas e pagamentos parciais.
├── etapa12_combo_avaliacoes.sql  # Acrescenta a ficha do catálogo, personalização dos itens e caminhos das fotos de avaliações.
├── etapa4_migracao.sql  # Migração de contatos, usuários e custos históricos.
├── etapa8_pdv.sql  # Colunas de origem e pagamento do PDV.
├── etapa9_descontos.sql  # Cupons e registro de descontos por pedido.
├── README.md  # Guia de navegação e explicações desta pasta.
└── schema.sql  # Schema inicial MySQL usado pelo respectivo repositório.
```

[🏠 Voltar ao README principal](../../README.md)
