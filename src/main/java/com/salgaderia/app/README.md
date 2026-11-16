[🏠 Início](../../../../../../README.md) / [⬆️ salgaderia](../README.md) / 📂 app

# 🏗️ Aplicação Ui K'Fomi

Pacote principal da aplicação e ponto de encontro das camadas MVC, serviços e persistência.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [controller/](controller/) | Controllers das telas e testes de interface, conforme a localização. [Abrir guia](controller/README.md). |
| [dao/](dao/) | Contratos de acesso a dados ou testes JDBC. [Abrir guia](dao/README.md). |
| [exception/](exception/) | Exceções personalizadas de domínio e persistência. [Abrir guia](exception/README.md). |
| [model/](model/) | Entidades de domínio ou testes dessas entidades. [Abrir guia](model/README.md). |
| [service/](service/) | Contratos, regras de negócio e testes de serviços. [Abrir guia](service/README.md). |
| [util/](util/) | Conexão, autenticação auxiliar e navegação. [Abrir guia](util/README.md). |
| [Main.java](Main.java) | Inicialização da aplicação JavaFX. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/`.

```text
src/main/java/com/salgaderia/app/  # Pacote principal da aplicação e ponto de encontro das camadas MVC, serviços e persistência.
├── controller/  # Controllers das telas e testes de interface, conforme a localização.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AdminDashboardController.java  # PDV, fila, estoque, financeiro e alertas administrativos.
│   ├── AgendaController.java  # Interface da agenda de clientes e fornecedores.
│   ├── ClienteVitrineController.java  # Vitrine, carrinho, pagamento e histórico do cliente.
│   ├── LoginController.java  # Tela de login e cadastro público.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── SelecaoModulosController.java  # Seleção de módulos administrativos.
├── dao/  # Contratos de acesso a dados ou testes JDBC.
│   ├── impl/  # Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── AvaliacaoDAOImpl.java  # Consultas JDBC de avaliações, médias, respostas e exclusão.
│   │   ├── CategoriaDAOImpl.java  # Operações JDBC de cadastro e consulta de categorias.
│   │   ├── ContatoDAOImpl.java  # Operações JDBC de contatos com filtros de clientes e fornecedores.
│   │   ├── CupomDAOImpl.java  # Consulta JDBC parametrizada para localizar cupons ativos.
│   │   ├── MovimentacaoEstoqueDAOImpl.java  # Grava movimentações de estoque e consulta perdas por período via JDBC.
│   │   ├── PedidoDAOImpl.java  # Persiste pedidos, itens, estado, pagamentos e descontos; consulta fila e relatórios.
│   │   ├── ProdutoDAOImpl.java  # Persiste produtos, consulta categorias e atualiza estoque com JDBC.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── UsuarioDAOImpl.java  # Persiste usuários, consulta credenciais e atualiza hashes de senha.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AvaliacaoDAO.java  # Contrato para consultar médias, listar, inserir, responder e excluir avaliações.
│   ├── CategoriaDAO.java  # Contrato para cadastrar, atualizar, buscar, listar e excluir categorias.
│   ├── ContatoDAO.java  # Contrato para cadastro, edição, exclusão e busca filtrada de contatos.
│   ├── CupomDAO.java  # Contrato para consultar cupom ativo por código.
│   ├── MovimentacaoEstoqueDAO.java  # Contrato para registrar movimentações e calcular perdas por período.
│   ├── PedidoDAO.java  # Contrato para pedidos, fila, status, pagamentos, descontos e agregações de vendas.
│   ├── ProdutoDAO.java  # Contrato para cadastro de produtos, consultas e atualização de estoque.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── UsuarioDAO.java  # Contrato para inserir usuários, consultar login e atualizar senhas.
├── exception/  # Exceções personalizadas de domínio e persistência.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AutenticacaoInvalidaException.java  # Falha de autenticação.
│   ├── AvaliacaoNaoPermitidaException.java  # Avaliação sem compra elegível.
│   ├── EstoqueInsuficienteException.java  # Erro de quantidade indisponível.
│   ├── PersistenciaException.java  # Encapsula erro de persistência.
│   └── README.md  # Guia de navegação e explicações desta pasta.
├── model/  # Entidades de domínio ou testes dessas entidades.
│   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── CategoriaInsumo.java  # Categorias de insumos de fornecedores.
│   │   ├── FormaPagamento.java  # Formas de pagamento e regra de troco.
│   │   ├── PerfilUsuario.java  # Perfis de acesso do usuário.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   ├── StatusPedido.java  # Estados e transições permitidas do pedido.
│   │   ├── TipoEntrega.java  # Modalidades de entrega.
│   │   └── TipoMovimentacaoEstoque.java  # Tipos de movimentação de estoque.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── Avaliacao.java  # Avaliação de produto e validação da nota.
│   ├── AvaliacaoResumo.java  # Dados resumidos para moderação.
│   ├── Categoria.java  # Categoria de produtos.
│   ├── Cliente.java  # Contato especializado em cliente.
│   ├── Contato.java  # Abstração comum de contatos.
│   ├── Fornecedor.java  # Contato especializado em fornecedor.
│   ├── MovimentacaoEstoque.java  # Registro de entrada ou saída de estoque.
│   ├── Pedido.java  # Agregado do pedido, itens, desconto e pagamento.
│   ├── PedidoFila.java  # Resumo de pedido para a fila administrativa.
│   ├── PedidoItem.java  # Quantidade e preço histórico de um item.
│   ├── Produto.java  # Produto, preços, estoque e comportamento de domínio.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   ├── Usuario.java  # Abstração de usuário e permissões.
│   ├── UsuarioAdmin.java  # Usuário administrativo.
│   ├── UsuarioCliente.java  # Usuário vinculado a um cliente.
│   └── Validacao.java  # Validações compartilhadas das entidades.
├── service/  # Contratos, regras de negócio e testes de serviços.
│   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   ├── CalculadoraDescontos.java  # Seleciona o maior desconto elegível.
│   │   ├── ContextoDesconto.java  # Dados de entrada para estratégias de desconto.
│   │   ├── Cupom.java  # Valor de domínio de cupom.
│   │   ├── DescontoCentoSalgados.java  # Estratégia de desconto para cento de salgados.
│   │   ├── DescontoCupomPercentual.java  # Estratégia percentual por cupom.
│   │   ├── DescontoFidelidade.java  # Estratégia para clientes fiéis.
│   │   ├── EstrategiaDesconto.java  # Contrato Strategy para descontos.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── ResultadoDesconto.java  # Resumo do desconto e total calculados.
│   ├── impl/  # Implementações de regras e orquestração dos serviços.
│   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── AgendaServiceImpl.java  # Valida e coordena cadastro, busca e alterações de contatos pela camada DAO.
│   │   ├── AutenticacaoServiceImpl.java  # Orquestra login, verificação de senha e cadastro de cliente e usuário.
│   │   ├── AvaliacaoServiceImpl.java  # Valida avaliações elegíveis e controla operações de moderação.
│   │   ├── PedidoVendaServiceImpl.java  # Executa vendas transacionais, descontos, movimentações de estoque e transições.
│   │   ├── ProdutoEstoqueServiceImpl.java  # Valida produtos e coordena entradas, perdas e consultas de estoque.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── RelatorioFinanceiroServiceImpl.java  # Calcula indicadores financeiros e gera o balanço CSV.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AgendaService.java  # Contrato de operações e busca da agenda de clientes e fornecedores.
│   ├── AutenticacaoService.java  # Contrato para autenticação única e cadastro público de clientes.
│   ├── AvaliacaoService.java  # Contrato para avaliações, médias por produto e moderação administrativa.
│   ├── PedidoVendaService.java  # Contrato para simular descontos, finalizar pedidos e alterar status de vendas.
│   ├── ProdutoEstoqueService.java  # Contrato para produtos, consultas de estoque, entradas e perdas.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   ├── RelatorioFinanceiroService.java  # Contrato de cálculos de balanço, gráficos e exportação CSV.
│   └── WhatsAppLinkService.java  # Constrói e abre links de mensagens WhatsApp.
├── util/  # Conexão, autenticação auxiliar e navegação.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── ConnectionFactory.java  # Pool HikariCP e transações JDBC.
│   ├── Navegacao.java  # Navegação entre telas FXML.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   ├── Senhas.java  # Geração e verificação de hashes de senha.
│   ├── TelaAutenticada.java  # Contrato para telas com usuário autenticado.
│   └── WhatsAppUi.java  # Feedback visual e alternativa para links WhatsApp.
├── Main.java  # Inicialização da aplicação JavaFX.
└── README.md  # Guia de navegação e explicações desta pasta.
```

[🏠 Voltar ao README principal](../../../../../../README.md)
