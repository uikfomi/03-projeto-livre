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
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [AgendaService.java](AgendaService.java) | Contrato de operações e busca da agenda de clientes e fornecedores. |
| [AutenticacaoService.java](AutenticacaoService.java) | Contrato para autenticação única e cadastro público de clientes. |
| [AvaliacaoService.java](AvaliacaoService.java) | Contrato para avaliações, médias, fotos e moderação administrativa. |
| [BarramentoPedido.java](BarramentoPedido.java) | Notifica observadores na mesma transação JDBC do evento de pedido. |
| [ClubeService.java](ClubeService.java) | Contrato de adesão, administração, confirmação de mensalidade e cancelamento. |
| [ComandaService.java](ComandaService.java) | Contrato para abertura, lançamento, rateio e pagamentos de comandas. |
| [ComboBuilder.java](ComboBuilder.java) | Monta o combo com uma variante real da massa e do recheio e acompanhamento opcional. |
| [ConquistasObserver.java](ConquistasObserver.java) | Avalia selos, libera a roleta e revoga cupons de pedidos cancelados. |
| [FotoAvaliacaoStorage.java](FotoAvaliacaoStorage.java) | Valida, redimensiona, salva e remove imagens locais de avaliações. |
| [GamificacaoService.java](GamificacaoService.java) | Contrato para consultar selos, pontos e cupons e sortear um prêmio por pedido. |
| [PedidoConcluidoObserver.java](PedidoConcluidoObserver.java) | Contrato Observer para eventos de finalização, entrega e cancelamento. |
| [PedidoVendaService.java](PedidoVendaService.java) | Contrato para simular descontos, finalizar pedidos e alterar status de vendas. |
| [PersonalizacaoService.java](PersonalizacaoService.java) | Sorteia caixas, agrega estoque reservado no carrinho e valida composições e restrições. |
| [PixPayloadService.java](PixPayloadService.java) | Monta BR Code com CRC16 e gera a imagem Pix em memória com ZXing. |
| [ProdutoEstoqueService.java](ProdutoEstoqueService.java) | Contrato para produtos, consultas de estoque, entradas e perdas. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [RelatorioFinanceiroService.java](RelatorioFinanceiroService.java) | Contrato de cálculos de balanço, gráficos e exportação CSV. |
| [WhatsAppLinkService.java](WhatsAppLinkService.java) | Constrói e abre links de mensagens WhatsApp. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/service/`.

```text
src/main/java/com/salgaderia/app/service/  # Define contratos e serviços de aplicação responsáveis por regras e orquestração.
├── cozinha/  # Estados do termômetro visual de preparo dos pedidos.
│   ├── EstadoCozinha.java  # Contrato State de mensagem, progresso e espera.
│   ├── EtapaCozinha.java  # Mapeia os status persistidos aos estados visuais da cozinha.
│   └── README.md  # Guia de navegação e explicações desta pasta.
├── desconto/  # Estratégias de desconto e testes correspondentes.
│   ├── CalculadoraDescontos.java  # Seleciona o maior desconto elegível.
│   ├── ContextoDesconto.java  # Agrupa itens, fidelidade, cupom e benefício mensal para as estratégias.
│   ├── Cupom.java  # Valor de domínio de cupom.
│   ├── DescontoAssinante.java  # Calcula o desconto de assinatura ativa como candidato não cumulativo.
│   ├── DescontoCaixaMisteriosa.java  # Oferece 15% sobre o valor de cada caixa como desconto não cumulativo.
│   ├── DescontoCentoSalgados.java  # Estratégia de desconto para cento de salgados.
│   ├── DescontoCupomPercentual.java  # Estratégia percentual por cupom.
│   ├── DescontoFidelidade.java  # Estratégia para clientes fiéis.
│   ├── EstrategiaDesconto.java  # Contrato Strategy para descontos.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── ResultadoDesconto.java  # Resumo do desconto e total calculados.
├── impl/  # Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AgendaServiceImpl.java  # Valida e coordena cadastro, busca e alterações de contatos pela camada DAO.
│   ├── AutenticacaoServiceImpl.java  # Orquestra login, verificação de senha e cadastro de cliente e usuário.
│   ├── AvaliacaoServiceImpl.java  # Valida a entrega e coordena avaliações, upload e exclusão de fotos na moderação.
│   ├── ClubeServiceImpl.java  # Valida adesão, vigência, renovação e confirmação manual no Clube da Fome.
│   ├── ComandaServiceImpl.java  # Orquestra estoque, validação do operador, cotação de pagamentos e fechamento atômico.
│   ├── GamificacaoServiceImpl.java  # Autoriza o sorteio, persiste o prêmio uma única vez e exibe o perfil do cliente.
│   ├── PedidoVendaServiceImpl.java  # Finaliza vendas transacionais, revalida estoque e descontos e publica eventos dos pedidos.
│   ├── ProdutoEstoqueServiceImpl.java  # Valida produtos e coordena entradas, perdas e consultas de estoque.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── RelatorioFinanceiroServiceImpl.java  # Calcula indicadores financeiros e gera o balanço CSV.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── AgendaService.java  # Contrato de operações e busca da agenda de clientes e fornecedores.
├── AutenticacaoService.java  # Contrato para autenticação única e cadastro público de clientes.
├── AvaliacaoService.java  # Contrato para avaliações, médias, fotos e moderação administrativa.
├── BarramentoPedido.java  # Notifica observadores na mesma transação JDBC do evento de pedido.
├── ClubeService.java  # Contrato de adesão, administração, confirmação de mensalidade e cancelamento.
├── ComandaService.java  # Contrato para abertura, lançamento, rateio e pagamentos de comandas.
├── ComboBuilder.java  # Monta o combo com uma variante real da massa e do recheio e acompanhamento opcional.
├── ConquistasObserver.java  # Avalia selos, libera a roleta e revoga cupons de pedidos cancelados.
├── FotoAvaliacaoStorage.java  # Valida, redimensiona, salva e remove imagens locais de avaliações.
├── GamificacaoService.java  # Contrato para consultar selos, pontos e cupons e sortear um prêmio por pedido.
├── PedidoConcluidoObserver.java  # Contrato Observer para eventos de finalização, entrega e cancelamento.
├── PedidoVendaService.java  # Contrato para simular descontos, finalizar pedidos e alterar status de vendas.
├── PersonalizacaoService.java  # Sorteia caixas, agrega estoque reservado no carrinho e valida composições e restrições.
├── PixPayloadService.java  # Monta BR Code com CRC16 e gera a imagem Pix em memória com ZXing.
├── ProdutoEstoqueService.java  # Contrato para produtos, consultas de estoque, entradas e perdas.
├── README.md  # Guia de navegação e explicações desta pasta.
├── RelatorioFinanceiroService.java  # Contrato de cálculos de balanço, gráficos e exportação CSV.
└── WhatsAppLinkService.java  # Constrói e abre links de mensagens WhatsApp.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
