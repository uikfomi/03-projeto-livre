[🏠 Início](../README.md) / 📂 src

# 💻 Código-fonte e testes

Reúne o código da aplicação, os recursos carregados pelo JavaFX e, quando presentes, os testes automatizados.

A implementação respeita a separação entre interface, serviços, modelos e acesso a dados; configurações locais não devem ser versionadas.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [main/](main/) | Aplicação principal e seus recursos. [Abrir guia](main/README.md). |
| [test/](test/) | Testes automatizados. [Abrir guia](test/README.md). |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |

## Árvore estrutural comentada

O ponto inicial é `src/`; cada comentário apresenta o caminho completo relativo à raiz do repositório.

```text
src/  # Reúne o código da aplicação, os recursos carregados pelo JavaFX e, quando presentes, os testes automatizados.
├── main/  # src/main/ — Aplicação principal e seus recursos.
│   ├── java/  # src/main/java/ — Pacotes de código Java.
│   │   ├── com/  # src/main/java/com/ — Raiz de pacotes Java.
│   │   │   ├── salgaderia/  # src/main/java/com/salgaderia/ — Pacote da aplicação Salgaderia.
│   │   │   │   ├── app/  # src/main/java/com/salgaderia/app/ — Componentes da aplicação e suas camadas.
│   │   │   │   │   ├── controller/  # src/main/java/com/salgaderia/app/controller/ — Controllers das telas e testes de interface, conforme a localização.
│   │   │   │   │   │   ├── .gitkeep  # src/main/java/com/salgaderia/app/controller/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── AdminDashboardController.java  # src/main/java/com/salgaderia/app/controller/AdminDashboardController.java — PDV, fila, estoque, financeiro e alertas administrativos.
│   │   │   │   │   │   ├── AgendaController.java  # src/main/java/com/salgaderia/app/controller/AgendaController.java — Interface da agenda de clientes e fornecedores.
│   │   │   │   │   │   ├── ClienteVitrineController.java  # src/main/java/com/salgaderia/app/controller/ClienteVitrineController.java — Vitrine, carrinho, pagamento e histórico do cliente.
│   │   │   │   │   │   ├── LoginController.java  # src/main/java/com/salgaderia/app/controller/LoginController.java — Tela de login e cadastro público.
│   │   │   │   │   │   ├── README.md  # src/main/java/com/salgaderia/app/controller/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   └── SelecaoModulosController.java  # src/main/java/com/salgaderia/app/controller/SelecaoModulosController.java — Seleção de módulos administrativos.
│   │   │   │   │   ├── dao/  # src/main/java/com/salgaderia/app/dao/ — Contratos de acesso a dados ou testes JDBC.
│   │   │   │   │   │   ├── impl/  # src/main/java/com/salgaderia/app/dao/impl/ — Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   │   │   │   │   │   │   ├── .gitkeep  # src/main/java/com/salgaderia/app/dao/impl/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AvaliacaoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/AvaliacaoDAOImpl.java — Consultas JDBC de avaliações, médias, respostas e exclusão.
│   │   │   │   │   │   │   ├── CategoriaDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/CategoriaDAOImpl.java — Operações JDBC de cadastro e consulta de categorias.
│   │   │   │   │   │   │   ├── ContatoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/ContatoDAOImpl.java — Operações JDBC de contatos com filtros de clientes e fornecedores.
│   │   │   │   │   │   │   ├── CupomDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/CupomDAOImpl.java — Consulta JDBC parametrizada para localizar cupons ativos.
│   │   │   │   │   │   │   ├── MovimentacaoEstoqueDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/MovimentacaoEstoqueDAOImpl.java — Grava movimentações de estoque e consulta perdas por período via JDBC.
│   │   │   │   │   │   │   ├── PedidoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/PedidoDAOImpl.java — Persiste pedidos, itens, estado, pagamentos e descontos; consulta fila e relatórios.
│   │   │   │   │   │   │   ├── ProdutoDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/ProdutoDAOImpl.java — Persiste produtos, consulta categorias e atualiza estoque com JDBC.
│   │   │   │   │   │   │   ├── README.md  # src/main/java/com/salgaderia/app/dao/impl/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   └── UsuarioDAOImpl.java  # src/main/java/com/salgaderia/app/dao/impl/UsuarioDAOImpl.java — Persiste usuários, consulta credenciais e atualiza hashes de senha.
│   │   │   │   │   │   ├── .gitkeep  # src/main/java/com/salgaderia/app/dao/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── AvaliacaoDAO.java  # src/main/java/com/salgaderia/app/dao/AvaliacaoDAO.java — Contrato para consultar médias, listar, inserir, responder e excluir avaliações.
│   │   │   │   │   │   ├── CategoriaDAO.java  # src/main/java/com/salgaderia/app/dao/CategoriaDAO.java — Contrato para cadastrar, atualizar, buscar, listar e excluir categorias.
│   │   │   │   │   │   ├── ContatoDAO.java  # src/main/java/com/salgaderia/app/dao/ContatoDAO.java — Contrato para cadastro, edição, exclusão e busca filtrada de contatos.
│   │   │   │   │   │   ├── CupomDAO.java  # src/main/java/com/salgaderia/app/dao/CupomDAO.java — Contrato para consultar cupom ativo por código.
│   │   │   │   │   │   ├── MovimentacaoEstoqueDAO.java  # src/main/java/com/salgaderia/app/dao/MovimentacaoEstoqueDAO.java — Contrato para registrar movimentações e calcular perdas por período.
│   │   │   │   │   │   ├── PedidoDAO.java  # src/main/java/com/salgaderia/app/dao/PedidoDAO.java — Contrato para pedidos, fila, status, pagamentos, descontos e agregações de vendas.
│   │   │   │   │   │   ├── ProdutoDAO.java  # src/main/java/com/salgaderia/app/dao/ProdutoDAO.java — Contrato para cadastro de produtos, consultas e atualização de estoque.
│   │   │   │   │   │   ├── README.md  # src/main/java/com/salgaderia/app/dao/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   └── UsuarioDAO.java  # src/main/java/com/salgaderia/app/dao/UsuarioDAO.java — Contrato para inserir usuários, consultar login e atualizar senhas.
│   │   │   │   │   ├── exception/  # src/main/java/com/salgaderia/app/exception/ — Exceções personalizadas de domínio e persistência.
│   │   │   │   │   │   ├── .gitkeep  # src/main/java/com/salgaderia/app/exception/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── AutenticacaoInvalidaException.java  # src/main/java/com/salgaderia/app/exception/AutenticacaoInvalidaException.java — Falha de autenticação.
│   │   │   │   │   │   ├── AvaliacaoNaoPermitidaException.java  # src/main/java/com/salgaderia/app/exception/AvaliacaoNaoPermitidaException.java — Avaliação sem compra elegível.
│   │   │   │   │   │   ├── EstoqueInsuficienteException.java  # src/main/java/com/salgaderia/app/exception/EstoqueInsuficienteException.java — Erro de quantidade indisponível.
│   │   │   │   │   │   ├── PersistenciaException.java  # src/main/java/com/salgaderia/app/exception/PersistenciaException.java — Encapsula erro de persistência.
│   │   │   │   │   │   └── README.md  # src/main/java/com/salgaderia/app/exception/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   ├── model/  # src/main/java/com/salgaderia/app/model/ — Entidades de domínio ou testes dessas entidades.
│   │   │   │   │   │   ├── enums/  # src/main/java/com/salgaderia/app/model/enums/ — Enumerações com comportamento e respectivos testes.
│   │   │   │   │   │   │   ├── .gitkeep  # src/main/java/com/salgaderia/app/model/enums/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── CategoriaInsumo.java  # src/main/java/com/salgaderia/app/model/enums/CategoriaInsumo.java — Categorias de insumos de fornecedores.
│   │   │   │   │   │   │   ├── FormaPagamento.java  # src/main/java/com/salgaderia/app/model/enums/FormaPagamento.java — Formas de pagamento e regra de troco.
│   │   │   │   │   │   │   ├── PerfilUsuario.java  # src/main/java/com/salgaderia/app/model/enums/PerfilUsuario.java — Perfis de acesso do usuário.
│   │   │   │   │   │   │   ├── README.md  # src/main/java/com/salgaderia/app/model/enums/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── StatusPedido.java  # src/main/java/com/salgaderia/app/model/enums/StatusPedido.java — Estados e transições permitidas do pedido.
│   │   │   │   │   │   │   ├── TipoEntrega.java  # src/main/java/com/salgaderia/app/model/enums/TipoEntrega.java — Modalidades de entrega.
│   │   │   │   │   │   │   └── TipoMovimentacaoEstoque.java  # src/main/java/com/salgaderia/app/model/enums/TipoMovimentacaoEstoque.java — Tipos de movimentação de estoque.
│   │   │   │   │   │   ├── .gitkeep  # src/main/java/com/salgaderia/app/model/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── Avaliacao.java  # src/main/java/com/salgaderia/app/model/Avaliacao.java — Avaliação de produto e validação da nota.
│   │   │   │   │   │   ├── AvaliacaoResumo.java  # src/main/java/com/salgaderia/app/model/AvaliacaoResumo.java — Dados resumidos para moderação.
│   │   │   │   │   │   ├── Categoria.java  # src/main/java/com/salgaderia/app/model/Categoria.java — Categoria de produtos.
│   │   │   │   │   │   ├── Cliente.java  # src/main/java/com/salgaderia/app/model/Cliente.java — Contato especializado em cliente.
│   │   │   │   │   │   ├── Contato.java  # src/main/java/com/salgaderia/app/model/Contato.java — Abstração comum de contatos.
│   │   │   │   │   │   ├── Fornecedor.java  # src/main/java/com/salgaderia/app/model/Fornecedor.java — Contato especializado em fornecedor.
│   │   │   │   │   │   ├── MovimentacaoEstoque.java  # src/main/java/com/salgaderia/app/model/MovimentacaoEstoque.java — Registro de entrada ou saída de estoque.
│   │   │   │   │   │   ├── Pedido.java  # src/main/java/com/salgaderia/app/model/Pedido.java — Agregado do pedido, itens, desconto e pagamento.
│   │   │   │   │   │   ├── PedidoFila.java  # src/main/java/com/salgaderia/app/model/PedidoFila.java — Resumo de pedido para a fila administrativa.
│   │   │   │   │   │   ├── PedidoItem.java  # src/main/java/com/salgaderia/app/model/PedidoItem.java — Quantidade e preço histórico de um item.
│   │   │   │   │   │   ├── Produto.java  # src/main/java/com/salgaderia/app/model/Produto.java — Produto, preços, estoque e comportamento de domínio.
│   │   │   │   │   │   ├── README.md  # src/main/java/com/salgaderia/app/model/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── Usuario.java  # src/main/java/com/salgaderia/app/model/Usuario.java — Abstração de usuário e permissões.
│   │   │   │   │   │   ├── UsuarioAdmin.java  # src/main/java/com/salgaderia/app/model/UsuarioAdmin.java — Usuário administrativo.
│   │   │   │   │   │   ├── UsuarioCliente.java  # src/main/java/com/salgaderia/app/model/UsuarioCliente.java — Usuário vinculado a um cliente.
│   │   │   │   │   │   └── Validacao.java  # src/main/java/com/salgaderia/app/model/Validacao.java — Validações compartilhadas das entidades.
│   │   │   │   │   ├── service/  # src/main/java/com/salgaderia/app/service/ — Contratos, regras de negócio e testes de serviços.
│   │   │   │   │   │   ├── desconto/  # src/main/java/com/salgaderia/app/service/desconto/ — Estratégias de desconto e testes correspondentes.
│   │   │   │   │   │   │   ├── CalculadoraDescontos.java  # src/main/java/com/salgaderia/app/service/desconto/CalculadoraDescontos.java — Seleciona o maior desconto elegível.
│   │   │   │   │   │   │   ├── ContextoDesconto.java  # src/main/java/com/salgaderia/app/service/desconto/ContextoDesconto.java — Dados de entrada para estratégias de desconto.
│   │   │   │   │   │   │   ├── Cupom.java  # src/main/java/com/salgaderia/app/service/desconto/Cupom.java — Valor de domínio de cupom.
│   │   │   │   │   │   │   ├── DescontoCentoSalgados.java  # src/main/java/com/salgaderia/app/service/desconto/DescontoCentoSalgados.java — Estratégia de desconto para cento de salgados.
│   │   │   │   │   │   │   ├── DescontoCupomPercentual.java  # src/main/java/com/salgaderia/app/service/desconto/DescontoCupomPercentual.java — Estratégia percentual por cupom.
│   │   │   │   │   │   │   ├── DescontoFidelidade.java  # src/main/java/com/salgaderia/app/service/desconto/DescontoFidelidade.java — Estratégia para clientes fiéis.
│   │   │   │   │   │   │   ├── EstrategiaDesconto.java  # src/main/java/com/salgaderia/app/service/desconto/EstrategiaDesconto.java — Contrato Strategy para descontos.
│   │   │   │   │   │   │   ├── README.md  # src/main/java/com/salgaderia/app/service/desconto/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   └── ResultadoDesconto.java  # src/main/java/com/salgaderia/app/service/desconto/ResultadoDesconto.java — Resumo do desconto e total calculados.
│   │   │   │   │   │   ├── impl/  # src/main/java/com/salgaderia/app/service/impl/ — Implementações de regras e orquestração dos serviços.
│   │   │   │   │   │   │   ├── .gitkeep  # src/main/java/com/salgaderia/app/service/impl/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AgendaServiceImpl.java  # src/main/java/com/salgaderia/app/service/impl/AgendaServiceImpl.java — Valida e coordena cadastro, busca e alterações de contatos pela camada DAO.
│   │   │   │   │   │   │   ├── AutenticacaoServiceImpl.java  # src/main/java/com/salgaderia/app/service/impl/AutenticacaoServiceImpl.java — Orquestra login, verificação de senha e cadastro de cliente e usuário.
│   │   │   │   │   │   │   ├── AvaliacaoServiceImpl.java  # src/main/java/com/salgaderia/app/service/impl/AvaliacaoServiceImpl.java — Valida avaliações elegíveis e controla operações de moderação.
│   │   │   │   │   │   │   ├── PedidoVendaServiceImpl.java  # src/main/java/com/salgaderia/app/service/impl/PedidoVendaServiceImpl.java — Executa vendas transacionais, descontos, movimentações de estoque e transições.
│   │   │   │   │   │   │   ├── ProdutoEstoqueServiceImpl.java  # src/main/java/com/salgaderia/app/service/impl/ProdutoEstoqueServiceImpl.java — Valida produtos e coordena entradas, perdas e consultas de estoque.
│   │   │   │   │   │   │   ├── README.md  # src/main/java/com/salgaderia/app/service/impl/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   └── RelatorioFinanceiroServiceImpl.java  # src/main/java/com/salgaderia/app/service/impl/RelatorioFinanceiroServiceImpl.java — Calcula indicadores financeiros e gera o balanço CSV.
│   │   │   │   │   │   ├── .gitkeep  # src/main/java/com/salgaderia/app/service/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── AgendaService.java  # src/main/java/com/salgaderia/app/service/AgendaService.java — Contrato de operações e busca da agenda de clientes e fornecedores.
│   │   │   │   │   │   ├── AutenticacaoService.java  # src/main/java/com/salgaderia/app/service/AutenticacaoService.java — Contrato para autenticação única e cadastro público de clientes.
│   │   │   │   │   │   ├── AvaliacaoService.java  # src/main/java/com/salgaderia/app/service/AvaliacaoService.java — Contrato para avaliações, médias por produto e moderação administrativa.
│   │   │   │   │   │   ├── PedidoVendaService.java  # src/main/java/com/salgaderia/app/service/PedidoVendaService.java — Contrato para simular descontos, finalizar pedidos e alterar status de vendas.
│   │   │   │   │   │   ├── ProdutoEstoqueService.java  # src/main/java/com/salgaderia/app/service/ProdutoEstoqueService.java — Contrato para produtos, consultas de estoque, entradas e perdas.
│   │   │   │   │   │   ├── README.md  # src/main/java/com/salgaderia/app/service/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── RelatorioFinanceiroService.java  # src/main/java/com/salgaderia/app/service/RelatorioFinanceiroService.java — Contrato de cálculos de balanço, gráficos e exportação CSV.
│   │   │   │   │   │   └── WhatsAppLinkService.java  # src/main/java/com/salgaderia/app/service/WhatsAppLinkService.java — Constrói e abre links de mensagens WhatsApp.
│   │   │   │   │   ├── util/  # src/main/java/com/salgaderia/app/util/ — Conexão, autenticação auxiliar e navegação.
│   │   │   │   │   │   ├── .gitkeep  # src/main/java/com/salgaderia/app/util/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── ConnectionFactory.java  # src/main/java/com/salgaderia/app/util/ConnectionFactory.java — Pool HikariCP e transações JDBC.
│   │   │   │   │   │   ├── Navegacao.java  # src/main/java/com/salgaderia/app/util/Navegacao.java — Navegação entre telas FXML.
│   │   │   │   │   │   ├── README.md  # src/main/java/com/salgaderia/app/util/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── Senhas.java  # src/main/java/com/salgaderia/app/util/Senhas.java — Geração e verificação de hashes de senha.
│   │   │   │   │   │   ├── TelaAutenticada.java  # src/main/java/com/salgaderia/app/util/TelaAutenticada.java — Contrato para telas com usuário autenticado.
│   │   │   │   │   │   └── WhatsAppUi.java  # src/main/java/com/salgaderia/app/util/WhatsAppUi.java — Feedback visual e alternativa para links WhatsApp.
│   │   │   │   │   ├── Main.java  # src/main/java/com/salgaderia/app/Main.java — Inicialização da aplicação JavaFX.
│   │   │   │   │   └── README.md  # src/main/java/com/salgaderia/app/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   └── README.md  # src/main/java/com/salgaderia/README.md — Guia de navegação e explicações desta pasta.
│   │   │   └── README.md  # src/main/java/com/README.md — Guia de navegação e explicações desta pasta.
│   │   └── README.md  # src/main/java/README.md — Guia de navegação e explicações desta pasta.
│   ├── resources/  # src/main/resources/ — Recursos institucionais ou recursos JavaFX, conforme a localização.
│   │   ├── css/  # src/main/resources/css/ — Folhas de estilo das telas JavaFX.
│   │   │   ├── .gitkeep  # src/main/resources/css/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   ├── admin.css  # src/main/resources/css/admin.css — Estilos do painel administrativo.
│   │   │   ├── agenda.css  # src/main/resources/css/agenda.css — Estilos da agenda.
│   │   │   ├── cliente-vitrine.css  # src/main/resources/css/cliente-vitrine.css — Estilos da vitrine.
│   │   │   ├── global.css  # src/main/resources/css/global.css — Estilos compartilhados da aplicação.
│   │   │   └── README.md  # src/main/resources/css/README.md — Guia de navegação e explicações desta pasta.
│   │   ├── fxml/  # src/main/resources/fxml/ — Descrições de interface JavaFX.
│   │   │   ├── .gitkeep  # src/main/resources/fxml/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   ├── admin-dashboard.fxml  # src/main/resources/fxml/admin-dashboard.fxml — Estrutura visual do painel administrativo.
│   │   │   ├── agenda-contatos.fxml  # src/main/resources/fxml/agenda-contatos.fxml — Estrutura visual da agenda.
│   │   │   ├── cliente-vitrine.fxml  # src/main/resources/fxml/cliente-vitrine.fxml — Estrutura visual da vitrine e checkout.
│   │   │   ├── login.fxml  # src/main/resources/fxml/login.fxml — Estrutura visual de login e cadastro.
│   │   │   ├── README.md  # src/main/resources/fxml/README.md — Guia de navegação e explicações desta pasta.
│   │   │   └── selecao-modulos.fxml  # src/main/resources/fxml/selecao-modulos.fxml — Estrutura visual da seleção de módulos.
│   │   ├── icons/  # src/main/resources/icons/ — Espaço reservado para ícones.
│   │   │   └── .gitkeep  # src/main/resources/icons/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── images/  # src/main/resources/images/ — Espaço reservado para imagens.
│   │   │   └── .gitkeep  # src/main/resources/images/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── database.properties  # src/main/resources/database.properties — Template de configuração JDBC; não contém credenciais locais.
│   │   └── README.md  # src/main/resources/README.md — Guia de navegação e explicações desta pasta.
│   └── README.md  # src/main/README.md — Guia de navegação e explicações desta pasta.
├── test/  # src/test/ — Testes automatizados.
│   ├── java/  # src/test/java/ — Pacotes de código Java.
│   │   ├── com/  # src/test/java/com/ — Raiz de pacotes Java.
│   │   │   ├── salgaderia/  # src/test/java/com/salgaderia/ — Pacote da aplicação Salgaderia.
│   │   │   │   ├── app/  # src/test/java/com/salgaderia/app/ — Componentes da aplicação e suas camadas.
│   │   │   │   │   ├── controller/  # src/test/java/com/salgaderia/app/controller/ — Testes automatizados dos componentes desta camada.
│   │   │   │   │   │   ├── AdminDashboardJavaFXTest.java  # src/test/java/com/salgaderia/app/controller/AdminDashboardJavaFXTest.java — Testes JavaFX do painel e notificações.
│   │   │   │   │   │   ├── AgendaJavaFXTest.java  # src/test/java/com/salgaderia/app/controller/AgendaJavaFXTest.java — Testes JavaFX da agenda.
│   │   │   │   │   │   ├── README.md  # src/test/java/com/salgaderia/app/controller/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── TelasJavaFXTest.java  # src/test/java/com/salgaderia/app/controller/TelasJavaFXTest.java — Testes JavaFX de autenticação e navegação.
│   │   │   │   │   │   └── VitrineJavaFXTest.java  # src/test/java/com/salgaderia/app/controller/VitrineJavaFXTest.java — Testes JavaFX da vitrine, pagamento e atualização.
│   │   │   │   │   ├── dao/  # src/test/java/com/salgaderia/app/dao/ — Testes automatizados dos componentes desta camada.
│   │   │   │   │   │   └── AgendaJDBCTest.java  # src/test/java/com/salgaderia/app/dao/AgendaJDBCTest.java — Teste de integração JDBC da agenda.
│   │   │   │   │   ├── model/  # src/test/java/com/salgaderia/app/model/ — Testes automatizados dos componentes desta camada.
│   │   │   │   │   │   ├── enums/  # src/test/java/com/salgaderia/app/model/enums/ — Enumerações com comportamento e respectivos testes.
│   │   │   │   │   │   │   └── EnumsTest.java  # src/test/java/com/salgaderia/app/model/enums/EnumsTest.java — Testes de comportamento dos enums.
│   │   │   │   │   │   └── DominioTest.java  # src/test/java/com/salgaderia/app/model/DominioTest.java — Testes das entidades e regras de domínio.
│   │   │   │   │   ├── service/  # src/test/java/com/salgaderia/app/service/ — Testes automatizados dos componentes desta camada.
│   │   │   │   │   │   ├── desconto/  # src/test/java/com/salgaderia/app/service/desconto/ — Estratégias de desconto e testes correspondentes.
│   │   │   │   │   │   │   └── EstrategiasDescontoTest.java  # src/test/java/com/salgaderia/app/service/desconto/EstrategiasDescontoTest.java — Testa as estratégias e a escolha do melhor desconto.
│   │   │   │   │   │   ├── DescontoIntegracaoTest.java  # src/test/java/com/salgaderia/app/service/DescontoIntegracaoTest.java — Integração de descontos e pagamentos no MySQL.
│   │   │   │   │   │   ├── Etapa4IntegracaoTest.java  # src/test/java/com/salgaderia/app/service/Etapa4IntegracaoTest.java — Fluxos transacionais no MySQL.
│   │   │   │   │   │   ├── README.md  # src/test/java/com/salgaderia/app/service/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── RelatorioCsvTest.java  # src/test/java/com/salgaderia/app/service/RelatorioCsvTest.java — Verifica o balanço exportado em CSV.
│   │   │   │   │   │   └── WhatsAppLinkServiceTest.java  # src/test/java/com/salgaderia/app/service/WhatsAppLinkServiceTest.java — Testa links, mensagens e configuração WhatsApp.
│   │   │   │   │   └── README.md  # src/test/java/com/salgaderia/app/README.md — Guia de navegação e explicações desta pasta.
│   │   │   │   └── README.md  # src/test/java/com/salgaderia/README.md — Guia de navegação e explicações desta pasta.
│   │   │   └── README.md  # src/test/java/com/README.md — Guia de navegação e explicações desta pasta.
│   │   └── README.md  # src/test/java/README.md — Guia de navegação e explicações desta pasta.
│   └── README.md  # src/test/README.md — Guia de navegação e explicações desta pasta.
└── README.md  # src/README.md — Guia de navegação e explicações desta pasta.
```

## 📌 Status do Conteúdo

As pastas abaixo estão reservadas e aguardam materiais finais; os arquivos `.gitkeep` apenas preservam a estrutura:

- `src/main/resources/icons/` — Espaço reservado para ícones.
- `src/main/resources/images/` — Espaço reservado para imagens.

[🏠 Voltar ao README principal](../README.md)
