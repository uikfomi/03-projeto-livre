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
| [AssinaturaCliente.java](AssinaturaCliente.java) | Representa o plano do cliente, o desconto vigente e o estado da assinatura. |
| [Avaliacao.java](Avaliacao.java) | Registra nota, comentário, resposta oficial e referência opcional da foto. |
| [AvaliacaoFoto.java](AvaliacaoFoto.java) | Projeção pública de fotos de avaliações que não expõe dados da conta. |
| [AvaliacaoResumo.java](AvaliacaoResumo.java) | Projeção de avaliação com comentário, resposta oficial e foto para moderação. |
| [Categoria.java](Categoria.java) | Categoria de produtos. |
| [Cliente.java](Cliente.java) | Contato especializado em cliente. |
| [ConfiguracaoLoja.java](ConfiguracaoLoja.java) | Representa o expediente, o modo automático e a chave manual da loja. |
| [ConquistaCliente.java](ConquistaCliente.java) | Representa um selo e a data em que foi desbloqueado. |
| [Contato.java](Contato.java) | Abstração comum de contatos. |
| [CupomRoleta.java](CupomRoleta.java) | Projeção de código de resgate com benefício, mínimo e data de expiração. |
| [FichaVitrine.java](FichaVitrine.java) | Dados confirmados no catálogo: tipo de combo, recheio, ingredientes e preparo assado. |
| [Fornecedor.java](Fornecedor.java) | Contato especializado em fornecedor. |
| [MensalidadeClube.java](MensalidadeClube.java) | Cotação imutável de valor e desconto de uma mensalidade pendente. |
| [MovimentacaoEstoque.java](MovimentacaoEstoque.java) | Registro de entrada ou saída de estoque. |
| [Pedido.java](Pedido.java) | Mantém itens, total, frete, pagamento, status e liberação da roleta. |
| [PedidoFila.java](PedidoFila.java) | Projeção de preparo com pagamento, modalidade, itens, entrega e agendamento. |
| [PedidoItem.java](PedidoItem.java) | Registra produto, quantidade, preço histórico e eventual personalização do item. |
| [PersonalizacaoItem.java](PersonalizacaoItem.java) | Identifica grupos de combo ou caixa, tamanho e restrições de ingredientes. |
| [PlanoAssinatura.java](PlanoAssinatura.java) | Valida nome, descrição, preço mensal e percentual do plano. |
| [PremioRoleta.java](PremioRoleta.java) | Define fatias comuns/VIP e registra resultado, prazo e giro extra. |
| [Produto.java](Produto.java) | Representa preço, custos, saldo físico, disponibilidade de venda e ficha do catálogo. |
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
│   ├── Comanda.java  # Sessão da mesa e cálculo monetário de total, pagamentos e saldo.
│   ├── ItemComanda.java  # Item lançado na comanda, com quantidade disponível e cotação por unidade.
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
│   ├── StatusPagamento.java  # Distingue PIX pendente, pagamento confirmado e cobrança na entrega.
│   ├── StatusPedido.java  # Define as transições por modalidade, incluindo saída exclusiva para entrega.
│   ├── TipoEntrega.java  # Modalidades de entrega.
│   └── TipoMovimentacaoEstoque.java  # Tipos de movimentação de estoque.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── AssinaturaCliente.java  # Representa o plano do cliente, o desconto vigente e o estado da assinatura.
├── Avaliacao.java  # Registra nota, comentário, resposta oficial e referência opcional da foto.
├── AvaliacaoFoto.java  # Projeção pública de fotos de avaliações que não expõe dados da conta.
├── AvaliacaoResumo.java  # Projeção de avaliação com comentário, resposta oficial e foto para moderação.
├── Categoria.java  # Categoria de produtos.
├── Cliente.java  # Contato especializado em cliente.
├── ConfiguracaoLoja.java  # Representa o expediente, o modo automático e a chave manual da loja.
├── ConquistaCliente.java  # Representa um selo e a data em que foi desbloqueado.
├── Contato.java  # Abstração comum de contatos.
├── CupomRoleta.java  # Projeção de código de resgate com benefício, mínimo e data de expiração.
├── FichaVitrine.java  # Dados confirmados no catálogo: tipo de combo, recheio, ingredientes e preparo assado.
├── Fornecedor.java  # Contato especializado em fornecedor.
├── MensalidadeClube.java  # Cotação imutável de valor e desconto de uma mensalidade pendente.
├── MovimentacaoEstoque.java  # Registro de entrada ou saída de estoque.
├── Pedido.java  # Mantém itens, total, frete, pagamento, status e liberação da roleta.
├── PedidoFila.java  # Projeção de preparo com pagamento, modalidade, itens, entrega e agendamento.
├── PedidoItem.java  # Registra produto, quantidade, preço histórico e eventual personalização do item.
├── PersonalizacaoItem.java  # Identifica grupos de combo ou caixa, tamanho e restrições de ingredientes.
├── PlanoAssinatura.java  # Valida nome, descrição, preço mensal e percentual do plano.
├── PremioRoleta.java  # Define fatias comuns/VIP e registra resultado, prazo e giro extra.
├── Produto.java  # Representa preço, custos, saldo físico, disponibilidade de venda e ficha do catálogo.
├── README.md  # Guia de navegação e explicações desta pasta.
├── Usuario.java  # Abstração de usuário e permissões.
├── UsuarioAdmin.java  # Usuário administrativo.
├── UsuarioCliente.java  # Usuário vinculado a um cliente.
└── Validacao.java  # Validações compartilhadas das entidades.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
