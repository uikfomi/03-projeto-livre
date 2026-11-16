[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 dao

# 🗃️ Contratos de persistência

Define operações de acesso a dados independentes da implementação JDBC.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [impl/](impl/) | Implementações JDBC dos contratos DAO, com comandos parametrizados. [Abrir guia](impl/README.md). |
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [AvaliacaoDAO.java](AvaliacaoDAO.java) | Contrato para consultar médias, listar, inserir, responder e excluir avaliações. |
| [CategoriaDAO.java](CategoriaDAO.java) | Contrato para cadastrar, atualizar, buscar, listar e excluir categorias. |
| [ContatoDAO.java](ContatoDAO.java) | Contrato para cadastro, edição, exclusão e busca filtrada de contatos. |
| [CupomDAO.java](CupomDAO.java) | Contrato para consultar cupom ativo por código. |
| [MovimentacaoEstoqueDAO.java](MovimentacaoEstoqueDAO.java) | Contrato para registrar movimentações e calcular perdas por período. |
| [PedidoDAO.java](PedidoDAO.java) | Contrato para pedidos, fila, status, pagamentos, descontos e agregações de vendas. |
| [ProdutoDAO.java](ProdutoDAO.java) | Contrato para cadastro de produtos, consultas e atualização de estoque. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [UsuarioDAO.java](UsuarioDAO.java) | Contrato para inserir usuários, consultar login e atualizar senhas. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/dao/`; cada comentário apresenta o caminho completo relativo à raiz do repositório.

```text
src/main/java/com/salgaderia/app/dao/  # Define operações de acesso a dados independentes da implementação JDBC.
├── impl/  # src/main/java/com/salgaderia/app/dao/impl/ — Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   ├── .gitkeep  # src/main/java/com/salgaderia/app/dao/impl/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AvaliacaoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/AvaliacaoDAOImpl.java — Consultas JDBC de avaliações, médias, respostas e exclusão.
│   ├── CategoriaDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/CategoriaDAOImpl.java — Operações JDBC de cadastro e consulta de categorias.
│   ├── ContatoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/ContatoDAOImpl.java — Operações JDBC de contatos com filtros de clientes e fornecedores.
│   ├── CupomDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/CupomDAOImpl.java — Consulta JDBC parametrizada para localizar cupons ativos.
│   ├── MovimentacaoEstoqueDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/MovimentacaoEstoqueDAOImpl.java — Grava movimentações de estoque e consulta perdas por período via JDBC.
│   ├── PedidoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/PedidoDAOImpl.java — Persiste pedidos, itens, estado, pagamentos e descontos; consulta fila e relatórios.
│   ├── ProdutoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/ProdutoDAOImpl.java — Persiste produtos, consulta categorias e atualiza estoque com JDBC.
│   ├── README.md  # src/main/java/com/salgaderia/app/dao/impl/README.md — Guia de navegação e explicações desta pasta.
│   └── UsuarioDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/UsuarioDAOImpl.java — Persiste usuários, consulta credenciais e atualiza hashes de senha.
├── .gitkeep  # src/main/java/com/salgaderia/app/dao/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── AvaliacaoDAO.java  # src/main/java/com/salgaderia/app/dao/AvaliacaoDAO.java — Contrato para consultar médias, listar, inserir, responder e excluir avaliações.
├── CategoriaDAO.java  # src/main/java/com/salgaderia/app/dao/CategoriaDAO.java — Contrato para cadastrar, atualizar, buscar, listar e excluir categorias.
├── ContatoDAO.java  # src/main/java/com/salgaderia/app/dao/ContatoDAO.java — Contrato para cadastro, edição, exclusão e busca filtrada de contatos.
├── CupomDAO.java  # src/main/java/com/salgaderia/app/dao/CupomDAO.java — Contrato para consultar cupom ativo por código.
├── MovimentacaoEstoqueDAO.java  # src/main/java/com/salgaderia/app/dao/MovimentacaoEstoqueDAO.java — Contrato para registrar movimentações e calcular perdas por período.
├── PedidoDAO.java  # src/main/java/com/salgaderia/app/dao/PedidoDAO.java — Contrato para pedidos, fila, status, pagamentos, descontos e agregações de vendas.
├── ProdutoDAO.java  # src/main/java/com/salgaderia/app/dao/ProdutoDAO.java — Contrato para cadastro de produtos, consultas e atualização de estoque.
├── README.md  # src/main/java/com/salgaderia/app/dao/README.md — Guia de navegação e explicações desta pasta.
└── UsuarioDAO.java  # src/main/java/com/salgaderia/app/dao/UsuarioDAO.java — Contrato para inserir usuários, consultar login e atualizar senhas.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
