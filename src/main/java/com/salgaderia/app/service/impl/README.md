[🏠 Início](../../../../../../../../README.md) / [⬆️ service](../README.md) / 📂 impl

# ⚙️ Implementações

Reúne implementações dos contratos da camada correspondente; DAOs usam JDBC e serviços concentram orquestração e regras.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [AgendaServiceImpl.java](AgendaServiceImpl.java) | Valida e coordena cadastro, busca e alterações de contatos pela camada DAO. |
| [AutenticacaoServiceImpl.java](AutenticacaoServiceImpl.java) | Orquestra login, verificação de senha e cadastro de cliente e usuário. |
| [AvaliacaoServiceImpl.java](AvaliacaoServiceImpl.java) | Valida avaliações elegíveis e controla operações de moderação. |
| [PedidoVendaServiceImpl.java](PedidoVendaServiceImpl.java) | Executa vendas transacionais, descontos, movimentações de estoque e transições. |
| [ProdutoEstoqueServiceImpl.java](ProdutoEstoqueServiceImpl.java) | Valida produtos e coordena entradas, perdas e consultas de estoque. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [RelatorioFinanceiroServiceImpl.java](RelatorioFinanceiroServiceImpl.java) | Calcula indicadores financeiros e gera o balanço CSV. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/service/impl/`.

```text
src/main/java/com/salgaderia/app/service/impl/  # Reúne implementações dos contratos da camada correspondente; DAOs usam JDBC e serviços concentram orquestração e regras.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── AgendaServiceImpl.java  # Valida e coordena cadastro, busca e alterações de contatos pela camada DAO.
├── AutenticacaoServiceImpl.java  # Orquestra login, verificação de senha e cadastro de cliente e usuário.
├── AvaliacaoServiceImpl.java  # Valida avaliações elegíveis e controla operações de moderação.
├── PedidoVendaServiceImpl.java  # Executa vendas transacionais, descontos, movimentações de estoque e transições.
├── ProdutoEstoqueServiceImpl.java  # Valida produtos e coordena entradas, perdas e consultas de estoque.
├── README.md  # Guia de navegação e explicações desta pasta.
└── RelatorioFinanceiroServiceImpl.java  # Calcula indicadores financeiros e gera o balanço CSV.
```

[🏠 Voltar ao README principal](../../../../../../../../README.md)
