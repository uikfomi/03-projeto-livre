[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 model

# 🧱 Modelo de domínio

Agrupa entidades, tipos de domínio e comportamentos encapsulados do sistema.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [enums/](enums/) | Enumerações com comportamento e respectivos testes. [Abrir guia](enums/README.md). |
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [Avaliacao.java](Avaliacao.java) | Avaliação de produto e validação da nota. |
| [AvaliacaoResumo.java](AvaliacaoResumo.java) | Dados resumidos para moderação. |
| [Categoria.java](Categoria.java) | Categoria de produtos. |
| [Cliente.java](Cliente.java) | Contato especializado em cliente. |
| [Contato.java](Contato.java) | Abstração comum de contatos. |
| [Fornecedor.java](Fornecedor.java) | Contato especializado em fornecedor. |
| [MovimentacaoEstoque.java](MovimentacaoEstoque.java) | Registro de entrada ou saída de estoque. |
| [Pedido.java](Pedido.java) | Agregado do pedido, itens, desconto e pagamento. |
| [PedidoFila.java](PedidoFila.java) | Resumo de pedido para a fila administrativa. |
| [PedidoItem.java](PedidoItem.java) | Quantidade e preço histórico de um item. |
| [Produto.java](Produto.java) | Produto, preços, estoque e comportamento de domínio. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [Usuario.java](Usuario.java) | Abstração de usuário e permissões. |
| [UsuarioAdmin.java](UsuarioAdmin.java) | Usuário administrativo. |
| [UsuarioCliente.java](UsuarioCliente.java) | Usuário vinculado a um cliente. |
| [Validacao.java](Validacao.java) | Validações compartilhadas das entidades. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/model/`; cada comentário apresenta o caminho completo relativo à raiz do repositório.

```text
src/main/java/com/salgaderia/app/model/  # Agrupa entidades, tipos de domínio e comportamentos encapsulados do sistema.
├── enums/  # src/main/java/com/salgaderia/app/model/enums/ — Enumerações com comportamento e respectivos testes.
│   ├── .gitkeep  # src/main/java/com/salgaderia/app/model/enums/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── CategoriaInsumo.java  # src/main/java/com/salgaderia/app/model/enums/CategoriaInsumo.java — Categorias de insumos de fornecedores.
│   ├── FormaPagamento.java  # src/main/java/com/salgaderia/app/model/enums/FormaPagamento.java — Formas de pagamento e regra de troco.
│   ├── PerfilUsuario.java  # src/main/java/com/salgaderia/app/model/enums/PerfilUsuario.java — Perfis de acesso do usuário.
│   ├── README.md  # src/main/java/com/salgaderia/app/model/enums/README.md — Guia de navegação e explicações desta pasta.
│   ├── StatusPedido.java  # src/main/java/com/salgaderia/app/model/enums/StatusPedido.java — Estados e transições permitidas do pedido.
│   ├── TipoEntrega.java  # src/main/java/com/salgaderia/app/model/enums/TipoEntrega.java — Modalidades de entrega.
│   └── TipoMovimentacaoEstoque.java  # src/main/java/com/salgaderia/app/model/enums/TipoMovimentacaoEstoque.java — Tipos de movimentação de estoque.
├── .gitkeep  # src/main/java/com/salgaderia/app/model/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── Avaliacao.java  # src/main/java/com/salgaderia/app/model/Avaliacao.java — Avaliação de produto e validação da nota.
├── AvaliacaoResumo.java  # src/main/java/com/salgaderia/app/model/AvaliacaoResumo.java — Dados resumidos para moderação.
├── Categoria.java  # src/main/java/com/salgaderia/app/model/Categoria.java — Categoria de produtos.
├── Cliente.java  # src/main/java/com/salgaderia/app/model/Cliente.java — Contato especializado em cliente.
├── Contato.java  # src/main/java/com/salgaderia/app/model/Contato.java — Abstração comum de contatos.
├── Fornecedor.java  # src/main/java/com/salgaderia/app/model/Fornecedor.java — Contato especializado em fornecedor.
├── MovimentacaoEstoque.java  # src/main/java/com/salgaderia/app/model/MovimentacaoEstoque.java — Registro de entrada ou saída de estoque.
├── Pedido.java  # src/main/java/com/salgaderia/app/model/Pedido.java — Agregado do pedido, itens, desconto e pagamento.
├── PedidoFila.java  # src/main/java/com/salgaderia/app/model/PedidoFila.java — Resumo de pedido para a fila administrativa.
├── PedidoItem.java  # src/main/java/com/salgaderia/app/model/PedidoItem.java — Quantidade e preço histórico de um item.
├── Produto.java  # src/main/java/com/salgaderia/app/model/Produto.java — Produto, preços, estoque e comportamento de domínio.
├── README.md  # src/main/java/com/salgaderia/app/model/README.md — Guia de navegação e explicações desta pasta.
├── Usuario.java  # src/main/java/com/salgaderia/app/model/Usuario.java — Abstração de usuário e permissões.
├── UsuarioAdmin.java  # src/main/java/com/salgaderia/app/model/UsuarioAdmin.java — Usuário administrativo.
├── UsuarioCliente.java  # src/main/java/com/salgaderia/app/model/UsuarioCliente.java — Usuário vinculado a um cliente.
└── Validacao.java  # src/main/java/com/salgaderia/app/model/Validacao.java — Validações compartilhadas das entidades.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
