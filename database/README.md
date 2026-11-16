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

O ponto inicial é `database/`; cada comentário apresenta o caminho completo relativo à raiz do repositório.

```text
database/  # Reúne o schema, as migrações e os modelos de dados que apoiam a persistência JDBC da Ui K'Fomi.
├── DER/  # database/DER/ — Modelo entidade-relacionamento e seus materiais.
│   ├── .gitkeep  # database/DER/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   └── modelo-entidade-relacionamento.md  # database/DER/modelo-entidade-relacionamento.md — Diagrama entidade-relacionamento Mermaid.
├── DL/  # database/DL/ — Modelo lógico das tabelas e relações.
│   ├── .gitkeep  # database/DL/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   └── modelo-logico.md  # database/DL/modelo-logico.md — Modelo lógico do banco.
├── scripts/  # database/scripts/ — Schema e migrações SQL ordenadas pela evolução do sistema.
│   ├── etapa10_pagamento_vitrine.sql  # database/scripts/etapa10_pagamento_vitrine.sql — Amplia forma de pagamento para cartão genérico.
│   ├── etapa4_migracao.sql  # database/scripts/etapa4_migracao.sql — Migração de contatos, usuários e custos históricos.
│   ├── etapa8_pdv.sql  # database/scripts/etapa8_pdv.sql — Colunas de origem e pagamento do PDV.
│   ├── etapa9_descontos.sql  # database/scripts/etapa9_descontos.sql — Cupons e registro de descontos por pedido.
│   ├── README.md  # database/scripts/README.md — Guia de navegação e explicações desta pasta.
│   └── schema.sql  # database/scripts/schema.sql — Schema inicial MySQL usado pelo respectivo repositório.
└── README.md  # database/README.md — Guia de navegação e explicações desta pasta.
```

[🏠 Voltar ao README principal](../README.md)
