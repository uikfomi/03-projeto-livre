[🏠 Início](../../../../../../../../README.md) / [⬆️ model](../README.md) / 📂 enums

# 🔀 Tipos e estados

Agrupa enumerações e os comportamentos associados a estados ou opções fixas.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [CategoriaInsumo.java](CategoriaInsumo.java) | Categorias de insumos de fornecedores. |
| [FormaPagamento.java](FormaPagamento.java) | Formas de pagamento e regra de troco. |
| [PerfilUsuario.java](PerfilUsuario.java) | Perfis de acesso do usuário. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [StatusPagamento.java](StatusPagamento.java) | Distingue PIX pendente, pagamento confirmado e cobrança na entrega. |
| [StatusPedido.java](StatusPedido.java) | Define as transições por modalidade, incluindo saída exclusiva para entrega. |
| [TipoEntrega.java](TipoEntrega.java) | Modalidades de entrega. |
| [TipoMovimentacaoEstoque.java](TipoMovimentacaoEstoque.java) | Tipos de movimentação de estoque. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/model/enums/`.

```text
src/main/java/com/salgaderia/app/model/enums/  # Agrupa enumerações e os comportamentos associados a estados ou opções fixas.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── CategoriaInsumo.java  # Categorias de insumos de fornecedores.
├── FormaPagamento.java  # Formas de pagamento e regra de troco.
├── PerfilUsuario.java  # Perfis de acesso do usuário.
├── README.md  # Guia de navegação e explicações desta pasta.
├── StatusPagamento.java  # Distingue PIX pendente, pagamento confirmado e cobrança na entrega.
├── StatusPedido.java  # Define as transições por modalidade, incluindo saída exclusiva para entrega.
├── TipoEntrega.java  # Modalidades de entrega.
└── TipoMovimentacaoEstoque.java  # Tipos de movimentação de estoque.
```

[🏠 Voltar ao README principal](../../../../../../../../README.md)
