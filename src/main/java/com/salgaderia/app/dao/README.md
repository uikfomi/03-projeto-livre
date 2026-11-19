[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 dao

# 🗃️ Contratos de persistência

Define operações de acesso a dados independentes da implementação JDBC.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [impl/](impl/) | Implementações JDBC dos contratos DAO, com comandos parametrizados. [Abrir guia](impl/README.md). |
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [AvaliacaoDAO.java](AvaliacaoDAO.java) | Contrato JDBC para avaliações, médias, galeria de fotos e moderação. |
| [CategoriaDAO.java](CategoriaDAO.java) | Contrato para cadastrar, atualizar, buscar, listar e excluir categorias. |
| [ClubeDAO.java](ClubeDAO.java) | Contrato de persistência de planos, assinaturas e mensalidades. |
| [ComandaDAO.java](ComandaDAO.java) | Contrato JDBC para mesas, itens, pagamentos e fechamento transacional. |
| [ContatoDAO.java](ContatoDAO.java) | Contrato para cadastro, edição, exclusão e busca filtrada de contatos. |
| [CupomDAO.java](CupomDAO.java) | Contrato de consulta, reserva e devolução de cupons com uso limitado. |
| [GamificacaoDAO.java](GamificacaoDAO.java) | Contrato para selos, elegibilidade por pagamento, prêmios, prazos e pontos. |
| [MovimentacaoEstoqueDAO.java](MovimentacaoEstoqueDAO.java) | Contrato para registrar movimentações e calcular perdas por período. |
| [PedidoDAO.java](PedidoDAO.java) | Contrato JDBC para pedidos, estados de pagamento, histórico, fila e balanço. |
| [PixPagamentoDAO.java](PixPagamentoDAO.java) | Contrato de persistência de cobranças, conciliação e status de pagamento. |
| [ProdutoDAO.java](ProdutoDAO.java) | Contrato para cadastro de produtos, consultas e atualização de estoque. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [UsuarioDAO.java](UsuarioDAO.java) | Contrato de acesso a usuários, credenciais e bloqueio transacional da conta do cliente. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/dao/`.

```text
src/main/java/com/salgaderia/app/dao/  # Define operações de acesso a dados independentes da implementação JDBC.
├── impl/  # Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AvaliacaoDAOImpl.java  # Persiste avaliações e fotos e consulta a galeria e dados de moderação.
│   ├── CategoriaDAOImpl.java  # Operações JDBC de cadastro e consulta de categorias.
│   ├── ClubeDAOImpl.java  # Executa consultas e atualizações JDBC para adesões, vigência e mensalidades.
│   ├── ComandaDAOImpl.java  # Implementa operações JDBC de mesas e comandas com bloqueio de linha.
│   ├── ContatoDAOImpl.java  # Operações JDBC de contatos com filtros de clientes e fornecedores.
│   ├── CupomDAOImpl.java  # Consulta elegibilidade, reserva cupons exclusivos e os devolve no cancelamento.
│   ├── GamificacaoDAOImpl.java  # Persiste selos, giros, resgates e trava de 24 horas usando o relógio do banco.
│   ├── MovimentacaoEstoqueDAOImpl.java  # Grava movimentações de estoque e consulta perdas por período via JDBC.
│   ├── PedidoDAOImpl.java  # Persiste pedidos, frete e brindes e só inclui PIX aprovado na fila.
│   ├── PixPagamentoDAOImpl.java  # Consulta pagamentos e vincula tentativas, IDs remotos e aprovação ao pedido em JDBC.
│   ├── ProdutoDAOImpl.java  # Persiste produtos, estoque e fichas de personalização com JDBC.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── UsuarioDAOImpl.java  # Executa consultas e alterações de usuário e valida a conta ativa sob bloqueio.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── AvaliacaoDAO.java  # Contrato JDBC para avaliações, médias, galeria de fotos e moderação.
├── CategoriaDAO.java  # Contrato para cadastrar, atualizar, buscar, listar e excluir categorias.
├── ClubeDAO.java  # Contrato de persistência de planos, assinaturas e mensalidades.
├── ComandaDAO.java  # Contrato JDBC para mesas, itens, pagamentos e fechamento transacional.
├── ContatoDAO.java  # Contrato para cadastro, edição, exclusão e busca filtrada de contatos.
├── CupomDAO.java  # Contrato de consulta, reserva e devolução de cupons com uso limitado.
├── GamificacaoDAO.java  # Contrato para selos, elegibilidade por pagamento, prêmios, prazos e pontos.
├── MovimentacaoEstoqueDAO.java  # Contrato para registrar movimentações e calcular perdas por período.
├── PedidoDAO.java  # Contrato JDBC para pedidos, estados de pagamento, histórico, fila e balanço.
├── PixPagamentoDAO.java  # Contrato de persistência de cobranças, conciliação e status de pagamento.
├── ProdutoDAO.java  # Contrato para cadastro de produtos, consultas e atualização de estoque.
├── README.md  # Guia de navegação e explicações desta pasta.
└── UsuarioDAO.java  # Contrato de acesso a usuários, credenciais e bloqueio transacional da conta do cliente.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
