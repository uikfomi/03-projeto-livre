[🏠 Início](../../../../../../../../README.md) / [⬆️ dao](../README.md) / 📂 impl

# ⚙️ Implementações

Reúne implementações dos contratos da camada correspondente; DAOs usam JDBC e serviços concentram orquestração e regras.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [AvaliacaoDAOImpl.java](AvaliacaoDAOImpl.java) | Consultas JDBC de avaliações, médias, respostas e exclusão. |
| [CategoriaDAOImpl.java](CategoriaDAOImpl.java) | Operações JDBC de cadastro e consulta de categorias. |
| [ContatoDAOImpl.java](ContatoDAOImpl.java) | Operações JDBC de contatos com filtros de clientes e fornecedores. |
| [CupomDAOImpl.java](CupomDAOImpl.java) | Consulta JDBC parametrizada para localizar cupons ativos. |
| [MovimentacaoEstoqueDAOImpl.java](MovimentacaoEstoqueDAOImpl.java) | Grava movimentações de estoque e consulta perdas por período via JDBC. |
| [PedidoDAOImpl.java](PedidoDAOImpl.java) | Persiste pedidos, itens, estado, pagamentos e descontos; consulta fila e relatórios. |
| [ProdutoDAOImpl.java](ProdutoDAOImpl.java) | Persiste produtos, consulta categorias e atualiza estoque com JDBC. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [UsuarioDAOImpl.java](UsuarioDAOImpl.java) | Persiste usuários, consulta credenciais e atualiza hashes de senha. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/dao/impl/`; cada comentário apresenta o caminho completo relativo à raiz do repositório.

```text
src/main/java/com/salgaderia/app/dao/impl/  # Reúne implementações dos contratos da camada correspondente; DAOs usam JDBC e serviços concentram orquestração e regras.
├── .gitkeep  # src/main/java/com/salgaderia/app/dao/impl/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── AvaliacaoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/AvaliacaoDAOImpl.java — Consultas JDBC de avaliações, médias, respostas e exclusão.
├── CategoriaDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/CategoriaDAOImpl.java — Operações JDBC de cadastro e consulta de categorias.
├── ContatoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/ContatoDAOImpl.java — Operações JDBC de contatos com filtros de clientes e fornecedores.
├── CupomDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/CupomDAOImpl.java — Consulta JDBC parametrizada para localizar cupons ativos.
├── MovimentacaoEstoqueDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/MovimentacaoEstoqueDAOImpl.java — Grava movimentações de estoque e consulta perdas por período via JDBC.
├── PedidoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/PedidoDAOImpl.java — Persiste pedidos, itens, estado, pagamentos e descontos; consulta fila e relatórios.
├── ProdutoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/ProdutoDAOImpl.java — Persiste produtos, consulta categorias e atualiza estoque com JDBC.
├── README.md  # src/main/java/com/salgaderia/app/dao/impl/README.md — Guia de navegação e explicações desta pasta.
└── UsuarioDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/UsuarioDAOImpl.java — Persiste usuários, consulta credenciais e atualiza hashes de senha.
```

[🏠 Voltar ao README principal](../../../../../../../../README.md)
