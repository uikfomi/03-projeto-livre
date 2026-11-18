[🏠 Início](../../../../../../../../README.md) / [⬆️ dao](../README.md) / 📂 impl

# ⚙️ Implementações

Reúne implementações dos contratos da camada correspondente; DAOs usam JDBC e serviços concentram orquestração e regras.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [AvaliacaoDAOImpl.java](AvaliacaoDAOImpl.java) | Persiste avaliações e fotos e consulta a galeria e dados de moderação. |
| [CategoriaDAOImpl.java](CategoriaDAOImpl.java) | Operações JDBC de cadastro e consulta de categorias. |
| [ClubeDAOImpl.java](ClubeDAOImpl.java) | Executa consultas e atualizações JDBC para adesões, vigência e mensalidades. |
| [ComandaDAOImpl.java](ComandaDAOImpl.java) | Implementa operações JDBC de mesas e comandas com bloqueio de linha. |
| [ContatoDAOImpl.java](ContatoDAOImpl.java) | Operações JDBC de contatos com filtros de clientes e fornecedores. |
| [CupomDAOImpl.java](CupomDAOImpl.java) | Valida disponibilidade e titular de cupons e encerra os exclusivos na compra seguinte. |
| [GamificacaoDAOImpl.java](GamificacaoDAOImpl.java) | Grava selos e prêmios e consulta os fatos de pedido usados pelo Observer. |
| [MovimentacaoEstoqueDAOImpl.java](MovimentacaoEstoqueDAOImpl.java) | Grava movimentações de estoque e consulta perdas por período via JDBC. |
| [PedidoDAOImpl.java](PedidoDAOImpl.java) | Persiste pedidos e personalizações e consulta histórico, fila, roleta e indicadores. |
| [ProdutoDAOImpl.java](ProdutoDAOImpl.java) | Persiste produtos, estoque e fichas de personalização com JDBC. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [UsuarioDAOImpl.java](UsuarioDAOImpl.java) | Executa consultas e alterações de usuário e valida a conta ativa sob bloqueio. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/dao/impl/`.

```text
src/main/java/com/salgaderia/app/dao/impl/  # Reúne implementações dos contratos da camada correspondente; DAOs usam JDBC e serviços concentram orquestração e regras.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── AvaliacaoDAOImpl.java  # Persiste avaliações e fotos e consulta a galeria e dados de moderação.
├── CategoriaDAOImpl.java  # Operações JDBC de cadastro e consulta de categorias.
├── ClubeDAOImpl.java  # Executa consultas e atualizações JDBC para adesões, vigência e mensalidades.
├── ComandaDAOImpl.java  # Implementa operações JDBC de mesas e comandas com bloqueio de linha.
├── ContatoDAOImpl.java  # Operações JDBC de contatos com filtros de clientes e fornecedores.
├── CupomDAOImpl.java  # Valida disponibilidade e titular de cupons e encerra os exclusivos na compra seguinte.
├── GamificacaoDAOImpl.java  # Grava selos e prêmios e consulta os fatos de pedido usados pelo Observer.
├── MovimentacaoEstoqueDAOImpl.java  # Grava movimentações de estoque e consulta perdas por período via JDBC.
├── PedidoDAOImpl.java  # Persiste pedidos e personalizações e consulta histórico, fila, roleta e indicadores.
├── ProdutoDAOImpl.java  # Persiste produtos, estoque e fichas de personalização com JDBC.
├── README.md  # Guia de navegação e explicações desta pasta.
└── UsuarioDAOImpl.java  # Executa consultas e alterações de usuário e valida a conta ativa sob bloqueio.
```

[🏠 Voltar ao README principal](../../../../../../../../README.md)
