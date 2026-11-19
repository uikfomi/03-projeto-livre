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
│   ├── AdminDashboardController.java  # Apresenta vendas, estoque, comandas, clube, moderação e conciliação e notificações PIX.
│   ├── AgendaController.java  # Interface da agenda de clientes e fornecedores.
│   ├── ClienteVitrineController.java  # Coordena catálogo, checkout PIX, urgência de cupons, pedidos, clube e roleta VIP.
│   ├── LoginController.java  # Tela de login e cadastro público.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── SelecaoModulosController.java  # Seleção de módulos administrativos.
├── dao/  # Contratos de acesso a dados ou testes JDBC.
│   ├── impl/  # Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── AvaliacaoDAOImpl.java  # Persiste avaliações e fotos e consulta a galeria e dados de moderação.
│   │   ├── CategoriaDAOImpl.java  # Operações JDBC de cadastro e consulta de categorias.
│   │   ├── ClubeDAOImpl.java  # Executa consultas e atualizações JDBC para adesões, vigência e mensalidades.
│   │   ├── ComandaDAOImpl.java  # Implementa operações JDBC de mesas e comandas com bloqueio de linha.
│   │   ├── ContatoDAOImpl.java  # Operações JDBC de contatos com filtros de clientes e fornecedores.
│   │   ├── CupomDAOImpl.java  # Consulta elegibilidade, reserva cupons exclusivos e os devolve no cancelamento.
│   │   ├── GamificacaoDAOImpl.java  # Persiste selos, giros, resgates e trava de 24 horas usando o relógio do banco.
│   │   ├── MovimentacaoEstoqueDAOImpl.java  # Grava movimentações de estoque e consulta perdas por período via JDBC.
│   │   ├── PedidoDAOImpl.java  # Persiste pedidos, frete e brindes e só inclui PIX aprovado na fila.
│   │   ├── PixPagamentoDAOImpl.java  # Consulta pagamentos e vincula tentativas, IDs remotos e aprovação ao pedido em JDBC.
│   │   ├── ProdutoDAOImpl.java  # Persiste produtos, estoque e fichas de personalização com JDBC.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── UsuarioDAOImpl.java  # Executa consultas e alterações de usuário e valida a conta ativa sob bloqueio.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AvaliacaoDAO.java  # Contrato JDBC para avaliações, médias, galeria de fotos e moderação.
│   ├── CategoriaDAO.java  # Contrato para cadastrar, atualizar, buscar, listar e excluir categorias.
│   ├── ClubeDAO.java  # Contrato de persistência de planos, assinaturas e mensalidades.
│   ├── ComandaDAO.java  # Contrato JDBC para mesas, itens, pagamentos e fechamento transacional.
│   ├── ContatoDAO.java  # Contrato para cadastro, edição, exclusão e busca filtrada de contatos.
│   ├── CupomDAO.java  # Contrato de consulta, reserva e devolução de cupons com uso limitado.
│   ├── GamificacaoDAO.java  # Contrato para selos, elegibilidade por pagamento, prêmios, prazos e pontos.
│   ├── MovimentacaoEstoqueDAO.java  # Contrato para registrar movimentações e calcular perdas por período.
│   ├── PedidoDAO.java  # Contrato JDBC para pedidos, estados de pagamento, histórico, fila e balanço.
│   ├── PixPagamentoDAO.java  # Contrato de persistência de cobranças, conciliação e status de pagamento.
│   ├── ProdutoDAO.java  # Contrato para cadastro de produtos, consultas e atualização de estoque.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── UsuarioDAO.java  # Contrato de acesso a usuários, credenciais e bloqueio transacional da conta do cliente.
├── exception/  # Exceções personalizadas de domínio e persistência.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AutenticacaoInvalidaException.java  # Falha de autenticação.
│   ├── AvaliacaoNaoPermitidaException.java  # Avaliação sem compra elegível.
│   ├── EstoqueInsuficienteException.java  # Erro de quantidade indisponível.
│   ├── PersistenciaException.java  # Encapsula erro de persistência.
│   └── README.md  # Guia de navegação e explicações desta pasta.
├── model/  # Entidades de domínio ou testes dessas entidades.
│   ├── comanda/  # Entidades de mesa, comanda, itens e pagamentos parciais.
│   │   ├── Comanda.java  # Sessão da mesa e cálculo monetário de total, pagamentos e saldo.
│   │   ├── ItemComanda.java  # Item lançado na comanda, com quantidade disponível e cotação por unidade.
│   │   ├── Mesa.java  # Identificação, número e status da mesa.
│   │   ├── PagamentoComanda.java  # Dados de pagamento parcial por valor ou item.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── StatusMesa.java  # Estados livre e ocupada e suas permissões.
│   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── CategoriaInsumo.java  # Categorias de insumos de fornecedores.
│   │   ├── FormaPagamento.java  # Formas de pagamento e regra de troco.
│   │   ├── PerfilUsuario.java  # Perfis de acesso do usuário.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   ├── StatusPagamento.java  # Distingue PIX pendente, pagamento confirmado e cobrança na entrega.
│   │   ├── StatusPedido.java  # Estados e transições permitidas do pedido.
│   │   ├── TipoEntrega.java  # Modalidades de entrega.
│   │   └── TipoMovimentacaoEstoque.java  # Tipos de movimentação de estoque.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AssinaturaCliente.java  # Representa o plano do cliente, o desconto vigente e o estado da assinatura.
│   ├── Avaliacao.java  # Registra nota, comentário, resposta oficial e referência opcional da foto.
│   ├── AvaliacaoFoto.java  # Projeção pública de fotos de avaliações que não expõe dados da conta.
│   ├── AvaliacaoResumo.java  # Projeção de avaliação com comentário, resposta oficial e foto para moderação.
│   ├── Categoria.java  # Categoria de produtos.
│   ├── Cliente.java  # Contato especializado em cliente.
│   ├── ConquistaCliente.java  # Representa um selo e a data em que foi desbloqueado.
│   ├── Contato.java  # Abstração comum de contatos.
│   ├── CupomRoleta.java  # Projeção de código de resgate com benefício, mínimo e data de expiração.
│   ├── FichaVitrine.java  # Dados confirmados no catálogo: tipo de combo, recheio, ingredientes e preparo assado.
│   ├── Fornecedor.java  # Contato especializado em fornecedor.
│   ├── MensalidadeClube.java  # Cotação imutável de valor e desconto de uma mensalidade pendente.
│   ├── MovimentacaoEstoque.java  # Registro de entrada ou saída de estoque.
│   ├── Pedido.java  # Mantém itens, total, frete, pagamento, status e liberação da roleta.
│   ├── PedidoFila.java  # Projeção de preparo com pagamento, modalidade, itens, entrega e agendamento.
│   ├── PedidoItem.java  # Registra produto, quantidade, preço histórico e eventual personalização do item.
│   ├── PersonalizacaoItem.java  # Identifica grupos de combo ou caixa, tamanho e restrições de ingredientes.
│   ├── PlanoAssinatura.java  # Valida nome, descrição, preço mensal e percentual do plano.
│   ├── PremioRoleta.java  # Define fatias comuns/VIP e registra resultado, prazo e giro extra.
│   ├── Produto.java  # Representa custos, preços, estoque e ficha de personalização do catálogo.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   ├── Usuario.java  # Abstração de usuário e permissões.
│   ├── UsuarioAdmin.java  # Usuário administrativo.
│   ├── UsuarioCliente.java  # Usuário vinculado a um cliente.
│   └── Validacao.java  # Validações compartilhadas das entidades.
├── service/  # Contratos, regras de negócio e testes de serviços.
│   ├── cozinha/  # Estados do termômetro visual de preparo dos pedidos.
│   │   ├── EstadoCozinha.java  # Contrato State de mensagem, progresso e espera.
│   │   ├── EtapaCozinha.java  # Mapeia os status persistidos aos estados visuais da cozinha.
│   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   ├── CalculadoraDescontos.java  # Escolhe uma única vantagem entre estratégias de desconto, frete, brindes e pontos.
│   │   ├── ContextoDesconto.java  # Agrupa itens, taxa de entrega, fidelidade, cupom e benefício do assinante.
│   │   ├── Cupom.java  # Representa percentual, frete, brinde ou bônus vinculado ao cupom.
│   │   ├── DescontoAssinante.java  # Calcula o desconto de assinatura ativa como candidato não cumulativo.
│   │   ├── DescontoCaixaMisteriosa.java  # Oferece 15% sobre o valor de cada caixa como desconto não cumulativo.
│   │   ├── DescontoCentoSalgados.java  # Estratégia de desconto para cento de salgados.
│   │   ├── DescontoCupomPercentual.java  # Estratégia percentual por cupom.
│   │   ├── DescontoFidelidade.java  # Estratégia para clientes fiéis.
│   │   ├── EstrategiaDesconto.java  # Contrato Strategy para descontos.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── ResultadoDesconto.java  # Retorna valor final, taxa de entrega, desconto escolhido e eventual brinde.
│   ├── impl/  # Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── AgendaServiceImpl.java  # Valida e coordena cadastro, busca e alterações de contatos pela camada DAO.
│   │   ├── AutenticacaoServiceImpl.java  # Orquestra login, verificação de senha e cadastro de cliente e usuário.
│   │   ├── AvaliacaoServiceImpl.java  # Valida a entrega e coordena avaliações, upload e exclusão de fotos na moderação.
│   │   ├── ClubeServiceImpl.java  # Valida adesão, vigência, renovação e confirmação manual no Clube da Fome.
│   │   ├── ComandaServiceImpl.java  # Orquestra estoque, validação do operador, cotação de pagamentos e fechamento atômico.
│   │   ├── GamificacaoServiceImpl.java  # Controla cooldown, separa sorteios de demonstração e aplica validade e giro extra.
│   │   ├── PedidoVendaServiceImpl.java  # Finaliza pedidos com proteção de estoque, promoções e fila dependente do PIX.
│   │   ├── ProdutoEstoqueServiceImpl.java  # Valida produtos e coordena entradas, perdas e consultas de estoque.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── RelatorioFinanceiroServiceImpl.java  # Calcula indicadores financeiros e gera o balanço CSV.
│   ├── pix/  # Porta, adapters e conciliação da cobrança PIX na vitrine.
│   │   ├── LocalDemoPixGateway.java  # Gera um BR Code de demonstração sem pagamento ou confirmação externa.
│   │   ├── MercadoPagoPixGateway.java  # Traduz cobranças para JSON e usa HttpClient para chamar a API Payments com idempotência.
│   │   ├── PixConfiguracao.java  # Lê o token local do Mercado Pago e a taxa de entrega configurável.
│   │   ├── PixGatewayService.java  # Porta de criação e consulta das cobranças PIX do checkout.
│   │   ├── PixPagamentoService.java  # Concilia a resposta do gateway com a cobrança local, sem aprovar valores divergentes.
│   │   └── README.md  # Guia da integração PIX da vitrine.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AgendaService.java  # Contrato de operações e busca da agenda de clientes e fornecedores.
│   ├── AutenticacaoService.java  # Contrato para autenticação única e cadastro público de clientes.
│   ├── AvaliacaoService.java  # Contrato para avaliações, médias, fotos e moderação administrativa.
│   ├── BarramentoPedido.java  # Notifica observadores na mesma transação JDBC do evento de pedido.
│   ├── ClubeService.java  # Contrato de adesão, administração, confirmação de mensalidade e cancelamento.
│   ├── ComandaService.java  # Contrato para abertura, lançamento, rateio e pagamentos de comandas.
│   ├── ComboBuilder.java  # Monta o combo com uma variante real da massa e do recheio e acompanhamento opcional.
│   ├── ConquistasObserver.java  # Avalia selos, libera a roleta após pagamento ou entrega e revoga prêmios cancelados.
│   ├── FotoAvaliacaoStorage.java  # Valida, redimensiona, salva e remove imagens locais de avaliações.
│   ├── GamificacaoService.java  # Contrato de selos, pontos, cupons, giro regular e giro extra.
│   ├── PedidoCancelamentoService.java  # Libera estoque e cupom ao cancelar a reserva de um pedido.
│   ├── PedidoConcluidoObserver.java  # Contrato Observer para finalização, confirmação PIX, entrega e cancelamento.
│   ├── PedidoVendaService.java  # Contrato para simular descontos, finalizar pedidos e alterar status de vendas.
│   ├── PersonalizacaoService.java  # Sorteia caixas, agrega estoque reservado no carrinho e valida composições e restrições.
│   ├── PixPayloadService.java  # Monta BR Code com CRC16 e gera a imagem Pix em memória com ZXing.
│   ├── ProdutoEstoqueService.java  # Contrato para produtos, consultas de estoque, entradas e perdas.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   ├── RelatorioFinanceiroService.java  # Contrato de cálculos de balanço, gráficos e exportação CSV.
│   └── WhatsAppLinkService.java  # Constrói e abre links de mensagens WhatsApp.
├── util/  # Conexão, autenticação auxiliar e navegação.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── ConnectionFactory.java  # Pool HikariCP e transações JDBC.
│   ├── FotoAvaliacaoUi.java  # Carrega miniaturas JavaFX de fotos de avaliações guardadas localmente.
│   ├── Navegacao.java  # Navegação entre telas FXML.
│   ├── PagamentoPixUi.java  # Mostra QR Code e consulta o pagamento com Timeline, sem bloquear a interface.
│   ├── PixPainel.java  # Desenha o QR local ou do gateway e disponibiliza o código PIX para cópia.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   ├── RoletaUi.java  # Anima o prêmio transacional em seis fatias regulares ou VIP, com giro extra quando disponível.
│   ├── Senhas.java  # Geração e verificação de hashes de senha.
│   ├── TelaAutenticada.java  # Contrato para telas com usuário autenticado.
│   └── WhatsAppUi.java  # Feedback visual e alternativa para links WhatsApp.
├── Main.java  # Inicialização da aplicação JavaFX.
└── README.md  # Guia de navegação e explicações desta pasta.
```

[🏠 Voltar ao README principal](../../../../../../README.md)
