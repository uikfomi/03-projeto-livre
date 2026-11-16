[🏠 Início](../README.md) / 📂 database

# 🗄️ Banco de dados

Reúne o schema, as migrações e os modelos de dados que apoiam a persistência JDBC da Ui K'Fomi.

Aplique os scripts na ordem descrita no README principal, em um banco de desenvolvimento, e faça backup antes de alterações em dados existentes. Senhas de seed nunca são apropriadas para produção.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [DER/](DER/) | Modelo entidade-relacionamento e seus materiais. |
| [DL/](DL/) | Modelo lógico das tabelas e relações. |
| [scripts/](scripts/) | Schema e migrações SQL ordenadas pela evolução do sistema. [Abrir guia](scripts/README.md). |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |

## Árvore estrutural comentada

O ponto inicial é `database/`.

```text
database/  # Reúne o schema, as migrações e os modelos de dados que apoiam a persistência JDBC da Ui K'Fomi.
├── DER/  # Modelo entidade-relacionamento e seus materiais.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   └── modelo-entidade-relacionamento.md  # Diagrama entidade-relacionamento Mermaid.
├── DL/  # Modelo lógico das tabelas e relações.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   └── modelo-logico.md  # Modelo lógico do banco.
├── scripts/  # Schema e migrações SQL ordenadas pela evolução do sistema.
│   ├── etapa10_pagamento_vitrine.sql  # Amplia forma de pagamento para cartão genérico.
│   ├── etapa11_salao_agendamento.sql  # Migração de agendamento, mesas, comandas e pagamentos parciais.
│   ├── etapa4_migracao.sql  # Migração de contatos, usuários e custos históricos.
│   ├── etapa8_pdv.sql  # Colunas de origem e pagamento do PDV.
│   ├── etapa9_descontos.sql  # Cupons e registro de descontos por pedido.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── schema.sql  # Schema inicial MySQL usado pelo respectivo repositório.
└── README.md  # Guia de navegação e explicações desta pasta.
```

[🏠 Voltar ao README principal](../README.md)
