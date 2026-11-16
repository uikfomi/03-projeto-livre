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

O ponto inicial é `src/`.

```text
src/  # Reúne o código da aplicação, os recursos carregados pelo JavaFX e, quando presentes, os testes automatizados.
├── main/  # Aplicação principal e seus recursos.
│   ├── java/  # Pacotes de código Java.
│   │   ├── com/  # Raiz de pacotes Java.
│   │   │   ├── salgaderia/  # Pacote da aplicação Salgaderia.
│   │   │   │   ├── app/  # Componentes da aplicação e suas camadas.
│   │   │   │   │   ├── controller/  # Controllers das telas e testes de interface, conforme a localização.
│   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── AdminDashboardController.java  # PDV, fila, estoque, financeiro e alertas administrativos.
│   │   │   │   │   │   ├── AgendaController.java  # Interface da agenda de clientes e fornecedores.
│   │   │   │   │   │   ├── ClienteVitrineController.java  # Vitrine, carrinho, pagamento e histórico do cliente.
│   │   │   │   │   │   ├── LoginController.java  # Tela de login e cadastro público.
│   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   └── SelecaoModulosController.java  # Seleção de módulos administrativos.
│   │   │   │   │   ├── dao/  # Contratos de acesso a dados ou testes JDBC.
│   │   │   │   │   │   ├── impl/  # Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AvaliacaoDAOImpl.java  # Consultas JDBC de avaliações, médias, respostas e exclusão.
│   │   │   │   │   │   │   ├── CategoriaDAOImpl.java  # Operações JDBC de cadastro e consulta de categorias.
│   │   │   │   │   │   │   ├── ComandaDAOImpl.java  # Implementa operações JDBC de mesas e comandas com bloqueio de linha.
│   │   │   │   │   │   │   ├── ContatoDAOImpl.java  # Operações JDBC de contatos com filtros de clientes e fornecedores.
│   │   │   │   │   │   │   ├── CupomDAOImpl.java  # Consulta JDBC parametrizada para localizar cupons ativos.
│   │   │   │   │   │   │   ├── MovimentacaoEstoqueDAOImpl.java  # Grava movimentações de estoque e consulta perdas por período via JDBC.
│   │   │   │   │   │   │   ├── PedidoDAOImpl.java  # Persiste pedidos, itens, estado, pagamentos e descontos; consulta fila e relatórios.
│   │   │   │   │   │   │   ├── ProdutoDAOImpl.java  # Persiste produtos, consulta categorias e atualiza estoque com JDBC.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   └── UsuarioDAOImpl.java  # Persiste usuários, consulta credenciais e atualiza hashes de senha.
│   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── AvaliacaoDAO.java  # Contrato para consultar médias, listar, inserir, responder e excluir avaliações.
│   │   │   │   │   │   ├── CategoriaDAO.java  # Contrato para cadastrar, atualizar, buscar, listar e excluir categorias.
│   │   │   │   │   │   ├── ComandaDAO.java  # Contrato JDBC para mesas, itens, pagamentos e fechamento transacional.
│   │   │   │   │   │   ├── ContatoDAO.java  # Contrato para cadastro, edição, exclusão e busca filtrada de contatos.
│   │   │   │   │   │   ├── CupomDAO.java  # Contrato para consultar cupom ativo por código.
│   │   │   │   │   │   ├── MovimentacaoEstoqueDAO.java  # Contrato para registrar movimentações e calcular perdas por período.
│   │   │   │   │   │   ├── PedidoDAO.java  # Contrato para pedidos, fila, status, pagamentos, descontos e agregações de vendas.
│   │   │   │   │   │   ├── ProdutoDAO.java  # Contrato para cadastro de produtos, consultas e atualização de estoque.
│   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   └── UsuarioDAO.java  # Contrato para inserir usuários, consultar login e atualizar senhas.
│   │   │   │   │   ├── exception/  # Exceções personalizadas de domínio e persistência.
│   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── AutenticacaoInvalidaException.java  # Falha de autenticação.
│   │   │   │   │   │   ├── AvaliacaoNaoPermitidaException.java  # Avaliação sem compra elegível.
│   │   │   │   │   │   ├── EstoqueInsuficienteException.java  # Erro de quantidade indisponível.
│   │   │   │   │   │   ├── PersistenciaException.java  # Encapsula erro de persistência.
│   │   │   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   ├── model/  # Entidades de domínio ou testes dessas entidades.
│   │   │   │   │   │   ├── comanda/  # Entidades de mesa, comanda, itens e pagamentos parciais.
│   │   │   │   │   │   │   ├── Comanda.java  # Resumo imutável com itens, pagamentos, total e saldo.
│   │   │   │   │   │   │   ├── ItemComanda.java  # Item lançado com preço e quantidade paga.
│   │   │   │   │   │   │   ├── Mesa.java  # Identificação, número e status da mesa.
│   │   │   │   │   │   │   ├── PagamentoComanda.java  # Dados de pagamento parcial por valor ou item.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   └── StatusMesa.java  # Estados livre e ocupada e suas permissões.
│   │   │   │   │   │   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── CategoriaInsumo.java  # Categorias de insumos de fornecedores.
│   │   │   │   │   │   │   ├── FormaPagamento.java  # Formas de pagamento e regra de troco.
│   │   │   │   │   │   │   ├── PerfilUsuario.java  # Perfis de acesso do usuário.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── StatusPedido.java  # Estados e transições permitidas do pedido.
│   │   │   │   │   │   │   ├── TipoEntrega.java  # Modalidades de entrega.
│   │   │   │   │   │   │   └── TipoMovimentacaoEstoque.java  # Tipos de movimentação de estoque.
│   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── Avaliacao.java  # Avaliação de produto e validação da nota.
│   │   │   │   │   │   ├── AvaliacaoResumo.java  # Dados resumidos para moderação.
│   │   │   │   │   │   ├── Categoria.java  # Categoria de produtos.
│   │   │   │   │   │   ├── Cliente.java  # Contato especializado em cliente.
│   │   │   │   │   │   ├── Contato.java  # Abstração comum de contatos.
│   │   │   │   │   │   ├── Fornecedor.java  # Contato especializado em fornecedor.
│   │   │   │   │   │   ├── MovimentacaoEstoque.java  # Registro de entrada ou saída de estoque.
│   │   │   │   │   │   ├── Pedido.java  # Agregado do pedido, itens, desconto e pagamento.
│   │   │   │   │   │   ├── PedidoFila.java  # Resumo de pedido para a fila administrativa.
│   │   │   │   │   │   ├── PedidoItem.java  # Quantidade e preço histórico de um item.
│   │   │   │   │   │   ├── Produto.java  # Produto, preços, estoque e comportamento de domínio.
│   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── Usuario.java  # Abstração de usuário e permissões.
│   │   │   │   │   │   ├── UsuarioAdmin.java  # Usuário administrativo.
│   │   │   │   │   │   ├── UsuarioCliente.java  # Usuário vinculado a um cliente.
│   │   │   │   │   │   └── Validacao.java  # Validações compartilhadas das entidades.
│   │   │   │   │   ├── service/  # Contratos, regras de negócio e testes de serviços.
│   │   │   │   │   │   ├── cozinha/  # Estados do termômetro visual de preparo dos pedidos.
│   │   │   │   │   │   │   ├── EstadoCozinha.java  # Contrato State de mensagem, progresso e espera.
│   │   │   │   │   │   │   ├── EtapaCozinha.java  # Mapeia os status persistidos aos estados visuais da cozinha.
│   │   │   │   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   │   │   │   │   │   ├── CalculadoraDescontos.java  # Seleciona o maior desconto elegível.
│   │   │   │   │   │   │   ├── ContextoDesconto.java  # Dados de entrada para estratégias de desconto.
│   │   │   │   │   │   │   ├── Cupom.java  # Valor de domínio de cupom.
│   │   │   │   │   │   │   ├── DescontoCentoSalgados.java  # Estratégia de desconto para cento de salgados.
│   │   │   │   │   │   │   ├── DescontoCupomPercentual.java  # Estratégia percentual por cupom.
│   │   │   │   │   │   │   ├── DescontoFidelidade.java  # Estratégia para clientes fiéis.
│   │   │   │   │   │   │   ├── EstrategiaDesconto.java  # Contrato Strategy para descontos.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   └── ResultadoDesconto.java  # Resumo do desconto e total calculados.
│   │   │   │   │   │   ├── impl/  # Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AgendaServiceImpl.java  # Valida e coordena cadastro, busca e alterações de contatos pela camada DAO.
│   │   │   │   │   │   │   ├── AutenticacaoServiceImpl.java  # Orquestra login, verificação de senha e cadastro de cliente e usuário.
│   │   │   │   │   │   │   ├── AvaliacaoServiceImpl.java  # Valida avaliações elegíveis e controla operações de moderação.
│   │   │   │   │   │   │   ├── ComandaServiceImpl.java  # Orquestra estoque, validação de operador, pagamentos e fechamento atômico.
│   │   │   │   │   │   │   ├── PedidoVendaServiceImpl.java  # Executa vendas transacionais, descontos, movimentações de estoque e transições.
│   │   │   │   │   │   │   ├── ProdutoEstoqueServiceImpl.java  # Valida produtos e coordena entradas, perdas e consultas de estoque.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   └── RelatorioFinanceiroServiceImpl.java  # Calcula indicadores financeiros e gera o balanço CSV.
│   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── AgendaService.java  # Contrato de operações e busca da agenda de clientes e fornecedores.
│   │   │   │   │   │   ├── AutenticacaoService.java  # Contrato para autenticação única e cadastro público de clientes.
│   │   │   │   │   │   ├── AvaliacaoService.java  # Contrato para avaliações, médias por produto e moderação administrativa.
│   │   │   │   │   │   ├── ComandaService.java  # Contrato para abertura, lançamento, rateio e pagamentos de comandas.
│   │   │   │   │   │   ├── PedidoVendaService.java  # Contrato para simular descontos, finalizar pedidos e alterar status de vendas.
│   │   │   │   │   │   ├── ProdutoEstoqueService.java  # Contrato para produtos, consultas de estoque, entradas e perdas.
│   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── RelatorioFinanceiroService.java  # Contrato de cálculos de balanço, gráficos e exportação CSV.
│   │   │   │   │   │   └── WhatsAppLinkService.java  # Constrói e abre links de mensagens WhatsApp.
│   │   │   │   │   ├── util/  # Conexão, autenticação auxiliar e navegação.
│   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   ├── ConnectionFactory.java  # Pool HikariCP e transações JDBC.
│   │   │   │   │   │   ├── Navegacao.java  # Navegação entre telas FXML.
│   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── Senhas.java  # Geração e verificação de hashes de senha.
│   │   │   │   │   │   ├── TelaAutenticada.java  # Contrato para telas com usuário autenticado.
│   │   │   │   │   │   └── WhatsAppUi.java  # Feedback visual e alternativa para links WhatsApp.
│   │   │   │   │   ├── Main.java  # Inicialização da aplicação JavaFX.
│   │   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   ├── resources/  # Recursos institucionais ou recursos JavaFX, conforme a localização.
│   │   ├── css/  # Folhas de estilo das telas JavaFX.
│   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   ├── admin.css  # Estilos do painel administrativo.
│   │   │   ├── agenda.css  # Estilos da agenda.
│   │   │   ├── cliente-vitrine.css  # Estilos da vitrine.
│   │   │   ├── global.css  # Estilos compartilhados da aplicação.
│   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   ├── fxml/  # Descrições de interface JavaFX.
│   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   ├── admin-dashboard.fxml  # Estrutura visual do painel administrativo.
│   │   │   ├── agenda-contatos.fxml  # Estrutura visual da agenda.
│   │   │   ├── cliente-vitrine.fxml  # Estrutura visual da vitrine e checkout.
│   │   │   ├── login.fxml  # Estrutura visual de login e cadastro.
│   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   └── selecao-modulos.fxml  # Estrutura visual da seleção de módulos.
│   │   ├── icons/  # Espaço reservado para ícones.
│   │   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── images/  # Espaço reservado para imagens.
│   │   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── database.properties  # Template de configuração JDBC; não contém credenciais locais.
│   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   └── README.md  # Guia de navegação e explicações desta pasta.
├── test/  # Testes automatizados.
│   ├── java/  # Pacotes de código Java.
│   │   ├── com/  # Raiz de pacotes Java.
│   │   │   ├── salgaderia/  # Pacote da aplicação Salgaderia.
│   │   │   │   ├── app/  # Componentes da aplicação e suas camadas.
│   │   │   │   │   ├── controller/  # Controllers das telas e testes de interface, conforme a localização.
│   │   │   │   │   │   ├── AdminDashboardJavaFXTest.java  # Testes JavaFX do painel e notificações.
│   │   │   │   │   │   ├── AgendaJavaFXTest.java  # Testes JavaFX da agenda.
│   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── TelasJavaFXTest.java  # Testes JavaFX de autenticação e navegação.
│   │   │   │   │   │   └── VitrineJavaFXTest.java  # Testes JavaFX da vitrine, pagamento e atualização.
│   │   │   │   │   ├── dao/  # Contratos de acesso a dados ou testes JDBC.
│   │   │   │   │   │   └── AgendaJDBCTest.java  # Teste de integração JDBC da agenda.
│   │   │   │   │   ├── model/  # Entidades de domínio ou testes dessas entidades.
│   │   │   │   │   │   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   │   │   │   │   │   └── EnumsTest.java  # Testes de comportamento dos enums.
│   │   │   │   │   │   └── DominioTest.java  # Testes das entidades e regras de domínio.
│   │   │   │   │   ├── service/  # Contratos, regras de negócio e testes de serviços.
│   │   │   │   │   │   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   │   │   │   │   │   └── EstrategiasDescontoTest.java  # Testa as estratégias e a escolha do melhor desconto.
│   │   │   │   │   │   ├── ComandaIntegracaoTest.java  # Valida operações de mesa, pagamentos concorrentes, baixa e fechamento no MySQL.
│   │   │   │   │   │   ├── CozinhaStateTest.java  # Verifica mensagens e progresso dos estados da cozinha.
│   │   │   │   │   │   ├── DescontoIntegracaoTest.java  # Integração de descontos e pagamentos no MySQL.
│   │   │   │   │   │   ├── Etapa4IntegracaoTest.java  # Fluxos transacionais no MySQL.
│   │   │   │   │   │   ├── PedidoAgendamentoTest.java  # Verifica rejeição de agendamento passado.
│   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── RelatorioCsvTest.java  # Verifica o balanço exportado em CSV.
│   │   │   │   │   │   └── WhatsAppLinkServiceTest.java  # Testa links, mensagens e configuração WhatsApp.
│   │   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   └── README.md  # Guia de navegação e explicações desta pasta.
└── README.md  # Guia de navegação e explicações desta pasta.
```

## 📌 Status do Conteúdo

As pastas abaixo estão reservadas e aguardam materiais finais; os arquivos `.gitkeep` apenas preservam a estrutura:

- `src/main/resources/icons/` — Espaço reservado para ícones.
- `src/main/resources/images/` — Espaço reservado para imagens.

[🏠 Voltar ao README principal](../README.md)
