[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 service

# 🧠 Serviços

Define contratos e serviços de aplicação responsáveis por regras e orquestração.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [cozinha/](cozinha/) | Estados do termômetro visual de preparo dos pedidos. [Abrir guia](cozinha/README.md). |
| [desconto/](desconto/) | Estratégias de desconto e testes correspondentes. [Abrir guia](desconto/README.md). |
| [impl/](impl/) | Implementações JDBC dos contratos DAO, com comandos parametrizados. [Abrir guia](impl/README.md). |
| [pix/](pix/) | Porta, adapters e conciliação da cobrança PIX na vitrine. [Abrir guia](pix/README.md). |
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [AgendaService.java](AgendaService.java) | Contrato de operações e busca da agenda de clientes e fornecedores. |
| [AutenticacaoService.java](AutenticacaoService.java) | Contrato para autenticação única e cadastro público de clientes. |
| [AvaliacaoService.java](AvaliacaoService.java) | Contrato para avaliações, médias, fotos e moderação administrativa. |
| [BarramentoPedido.java](BarramentoPedido.java) | Notifica observadores na mesma transação JDBC do evento de pedido. |
| [ClubeService.java](ClubeService.java) | Contrato de adesão, administração, confirmação de mensalidade e cancelamento. |
| [ComandaService.java](ComandaService.java) | Contrato para abertura, lançamento, rateio e pagamentos de comandas. |
| [ComboBuilder.java](ComboBuilder.java) | Monta o combo com uma variante real da massa e do recheio e acompanhamento opcional. |
| [ConquistasObserver.java](ConquistasObserver.java) | Avalia selos, libera a roleta após pagamento ou entrega e revoga prêmios cancelados. |
| [FotoAvaliacaoStorage.java](FotoAvaliacaoStorage.java) | Valida, redimensiona, salva e remove imagens locais de avaliações. |
| [GamificacaoService.java](GamificacaoService.java) | Contrato de selos, pontos, cupons, giro regular e giro extra. |
| [OperacaoLojaService.java](OperacaoLojaService.java) | Avalia a abertura e valida agendamentos pelo expediente local, inclusive após a meia-noite. |
| [PedidoCancelamentoService.java](PedidoCancelamentoService.java) | Devolve somente o saldo físico baixado e libera o cupom ao cancelar. |
| [PedidoConcluidoObserver.java](PedidoConcluidoObserver.java) | Contrato Observer para finalização, confirmação PIX, entrega e cancelamento. |
| [PedidoVendaService.java](PedidoVendaService.java) | Contrato para simular descontos, finalizar pedidos e alterar status de vendas. |
| [PersonalizacaoService.java](PersonalizacaoService.java) | Sorteia caixas com produtos liberados e valida composições e restrições. |
| [PixPayloadService.java](PixPayloadService.java) | Monta BR Code com CRC16 e gera a imagem Pix em memória com ZXing. |
| [ProdutoEstoqueService.java](ProdutoEstoqueService.java) | Define operações do catálogo, bloqueio de venda e movimentações do estoque físico. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [RelatorioFinanceiroService.java](RelatorioFinanceiroService.java) | Contrato de cálculos de balanço, gráficos e exportação CSV. |
| [WhatsAppLinkService.java](WhatsAppLinkService.java) | Gera e abre links do WhatsApp com o acompanhamento, status e nome do entregador. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/service/`.

```text
src/main/java/com/salgaderia/app/service/  # Define contratos e serviços de aplicação responsáveis por regras e orquestração.
├── cozinha/  # Estados do termômetro visual de preparo dos pedidos.
│   ├── EstadoCozinha.java  # Contrato State de mensagem, progresso e espera.
│   ├── EtapaCozinha.java  # Mapeia os status aos estados visuais de preparo, saída em rota e conclusão.
│   └── README.md  # Guia de navegação e explicações desta pasta.
├── desconto/  # Estratégias de desconto e testes correspondentes.
│   ├── CalculadoraDescontos.java  # Escolhe uma única vantagem entre estratégias de desconto, frete, brindes e pontos.
│   ├── ContextoDesconto.java  # Agrupa itens, taxa de entrega, fidelidade, cupom e benefício do assinante.
│   ├── Cupom.java  # Representa percentual, frete, brinde ou bônus vinculado ao cupom.
│   ├── DescontoAssinante.java  # Calcula o desconto de assinatura ativa como candidato não cumulativo.
│   ├── DescontoCaixaMisteriosa.java  # Oferece 15% sobre o valor de cada caixa como desconto não cumulativo.
│   ├── DescontoCentoSalgados.java  # Estratégia de desconto para cento de salgados.
│   ├── DescontoCupomPercentual.java  # Estratégia percentual por cupom.
│   ├── DescontoFidelidade.java  # Estratégia para clientes fiéis.
│   ├── EstrategiaDesconto.java  # Contrato Strategy para descontos.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── ResultadoDesconto.java  # Retorna valor final, taxa de entrega, desconto escolhido e eventual brinde.
├── impl/  # Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AgendaServiceImpl.java  # Valida e coordena cadastro, busca e alterações de contatos pela camada DAO.
│   ├── AutenticacaoServiceImpl.java  # Orquestra login, verificação de senha e cadastro de cliente e usuário.
│   ├── AvaliacaoServiceImpl.java  # Valida a entrega e coordena avaliações, upload e exclusão de fotos na moderação.
│   ├── ClubeServiceImpl.java  # Valida adesão, vigência, renovação e confirmação manual no Clube da Fome.
│   ├── ComandaServiceImpl.java  # Orquestra estoque, validação do operador, cotação de pagamentos e fechamento atômico.
│   ├── GamificacaoServiceImpl.java  # Controla cooldown, separa sorteios de demonstração e aplica validade e giro extra.
│   ├── PedidoVendaServiceImpl.java  # Valida expediente e disponibilidade e coordena baixa física, PIX e despacho.
│   ├── ProdutoEstoqueServiceImpl.java  # Gerencia disponibilidade, ajustes, lotes e perdas do estoque físico.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── RelatorioFinanceiroServiceImpl.java  # Calcula indicadores financeiros e gera o balanço CSV.
├── pix/  # Porta, adapters e conciliação da cobrança PIX na vitrine.
│   ├── LocalDemoPixGateway.java  # Gera um BR Code de demonstração sem pagamento ou confirmação externa.
│   ├── MercadoPagoPixGateway.java  # Traduz cobranças para JSON e usa HttpClient para chamar a API Payments com idempotência.
│   ├── PixConfiguracao.java  # Lê o token local do Mercado Pago e a taxa de entrega configurável.
│   ├── PixGatewayService.java  # Porta de criação e consulta das cobranças PIX do checkout.
│   ├── PixPagamentoService.java  # Concilia a resposta do gateway com a cobrança local, sem aprovar valores divergentes.
│   └── README.md  # Guia da integração PIX da vitrine.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── AgendaService.java  # Contrato de operações e busca da agenda de clientes e fornecedores.
├── AutenticacaoService.java  # Contrato para autenticação única e cadastro público de clientes.
├── AvaliacaoService.java  # Contrato para avaliações, médias, fotos e moderação administrativa.
├── BarramentoPedido.java  # Notifica observadores na mesma transação JDBC do evento de pedido.
├── ClubeService.java  # Contrato de adesão, administração, confirmação de mensalidade e cancelamento.
├── ComandaService.java  # Contrato para abertura, lançamento, rateio e pagamentos de comandas.
├── ComboBuilder.java  # Monta o combo com uma variante real da massa e do recheio e acompanhamento opcional.
├── ConquistasObserver.java  # Avalia selos, libera a roleta após pagamento ou entrega e revoga prêmios cancelados.
├── FotoAvaliacaoStorage.java  # Valida, redimensiona, salva e remove imagens locais de avaliações.
├── GamificacaoService.java  # Contrato de selos, pontos, cupons, giro regular e giro extra.
├── OperacaoLojaService.java  # Avalia a abertura e valida agendamentos pelo expediente local, inclusive após a meia-noite.
├── PedidoCancelamentoService.java  # Devolve somente o saldo físico baixado e libera o cupom ao cancelar.
├── PedidoConcluidoObserver.java  # Contrato Observer para finalização, confirmação PIX, entrega e cancelamento.
├── PedidoVendaService.java  # Contrato para simular descontos, finalizar pedidos e alterar status de vendas.
├── PersonalizacaoService.java  # Sorteia caixas com produtos liberados e valida composições e restrições.
├── PixPayloadService.java  # Monta BR Code com CRC16 e gera a imagem Pix em memória com ZXing.
├── ProdutoEstoqueService.java  # Define operações do catálogo, bloqueio de venda e movimentações do estoque físico.
├── README.md  # Guia de navegação e explicações desta pasta.
├── RelatorioFinanceiroService.java  # Contrato de cálculos de balanço, gráficos e exportação CSV.
└── WhatsAppLinkService.java  # Gera e abre links do WhatsApp com o acompanhamento, status e nome do entregador.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
