[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 model

# 🧱 Modelo de domínio

Agrupa entidades, tipos de domínio e comportamentos encapsulados do sistema.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [comanda/](comanda/) | Entidades de mesa, comanda, itens e pagamentos parciais. [Abrir guia](comanda/README.md). |
| [enums/](enums/) | Enumerações com comportamento e respectivos testes. [Abrir guia](enums/README.md). |
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [Avaliacao.java](Avaliacao.java) | Registra nota, comentário, resposta oficial e referência opcional da foto. |
| [AvaliacaoFoto.java](AvaliacaoFoto.java) | Projeção pública de fotos de avaliações que não expõe dados da conta. |
| [AvaliacaoResumo.java](AvaliacaoResumo.java) | Projeção de avaliação com comentário, resposta oficial e foto para moderação. |
| [Categoria.java](Categoria.java) | Categoria de produtos. |
| [Cliente.java](Cliente.java) | Contato especializado em cliente. |
| [Contato.java](Contato.java) | Abstração comum de contatos. |
| [FichaVitrine.java](FichaVitrine.java) | Dados confirmados no catálogo: tipo de combo, recheio, ingredientes e preparo assado. |
| [Fornecedor.java](Fornecedor.java) | Contato especializado em fornecedor. |
| [MovimentacaoEstoque.java](MovimentacaoEstoque.java) | Registro de entrada ou saída de estoque. |
| [Pedido.java](Pedido.java) | Agregado do pedido, itens, desconto e pagamento. |
| [PedidoFila.java](PedidoFila.java) | Resumo de pedido para a fila administrativa. |
| [PedidoItem.java](PedidoItem.java) | Registra produto, quantidade, preço histórico e eventual personalização do item. |
| [PersonalizacaoItem.java](PersonalizacaoItem.java) | Identifica grupos de combo ou caixa, tamanho e restrições de ingredientes. |
| [Produto.java](Produto.java) | Representa custos, preços, estoque e ficha de personalização do catálogo. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [Usuario.java](Usuario.java) | Abstração de usuário e permissões. |
| [UsuarioAdmin.java](UsuarioAdmin.java) | Usuário administrativo. |
| [UsuarioCliente.java](UsuarioCliente.java) | Usuário vinculado a um cliente. |
| [Validacao.java](Validacao.java) | Validações compartilhadas das entidades. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/model/`.

```text
src/main/java/com/salgaderia/app/model/  # Agrupa entidades, tipos de domínio e comportamentos encapsulados do sistema.
├── comanda/  # Entidades de mesa, comanda, itens e pagamentos parciais.
│   ├── Comanda.java  # Resumo imutável com itens, pagamentos, total e saldo.
│   ├── ItemComanda.java  # Item lançado com preço e quantidade paga.
│   ├── Mesa.java  # Identificação, número e status da mesa.
│   ├── PagamentoComanda.java  # Dados de pagamento parcial por valor ou item.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── StatusMesa.java  # Estados livre e ocupada e suas permissões.
├── enums/  # Enumerações com comportamento e respectivos testes.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── CategoriaInsumo.java  # Categorias de insumos de fornecedores.
│   ├── FormaPagamento.java  # Formas de pagamento e regra de troco.
│   ├── PerfilUsuario.java  # Perfis de acesso do usuário.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   ├── StatusPedido.java  # Estados e transições permitidas do pedido.
│   ├── TipoEntrega.java  # Modalidades de entrega.
│   └── TipoMovimentacaoEstoque.java  # Tipos de movimentação de estoque.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── Avaliacao.java  # Registra nota, comentário, resposta oficial e referência opcional da foto.
├── AvaliacaoFoto.java  # Projeção pública de fotos de avaliações que não expõe dados da conta.
├── AvaliacaoResumo.java  # Projeção de avaliação com comentário, resposta oficial e foto para moderação.
├── Categoria.java  # Categoria de produtos.
├── Cliente.java  # Contato especializado em cliente.
├── Contato.java  # Abstração comum de contatos.
├── FichaVitrine.java  # Dados confirmados no catálogo: tipo de combo, recheio, ingredientes e preparo assado.
├── Fornecedor.java  # Contato especializado em fornecedor.
├── MovimentacaoEstoque.java  # Registro de entrada ou saída de estoque.
├── Pedido.java  # Agregado do pedido, itens, desconto e pagamento.
├── PedidoFila.java  # Resumo de pedido para a fila administrativa.
├── PedidoItem.java  # Registra produto, quantidade, preço histórico e eventual personalização do item.
├── PersonalizacaoItem.java  # Identifica grupos de combo ou caixa, tamanho e restrições de ingredientes.
├── Produto.java  # Representa custos, preços, estoque e ficha de personalização do catálogo.
├── README.md  # Guia de navegação e explicações desta pasta.
├── Usuario.java  # Abstração de usuário e permissões.
├── UsuarioAdmin.java  # Usuário administrativo.
├── UsuarioCliente.java  # Usuário vinculado a um cliente.
└── Validacao.java  # Validações compartilhadas das entidades.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
