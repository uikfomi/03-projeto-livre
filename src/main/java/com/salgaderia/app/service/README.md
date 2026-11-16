[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 service

# 🧠 Serviços

Define contratos e serviços de aplicação responsáveis por regras e orquestração.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [desconto/](desconto/) | Estratégias de desconto e testes correspondentes. [Abrir guia](desconto/README.md). |
| [impl/](impl/) | Implementações de regras e orquestração dos serviços. [Abrir guia](impl/README.md). |
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [AgendaService.java](AgendaService.java) | Contrato de operações e busca da agenda de clientes e fornecedores. |
| [AutenticacaoService.java](AutenticacaoService.java) | Contrato para autenticação única e cadastro público de clientes. |
| [AvaliacaoService.java](AvaliacaoService.java) | Contrato para avaliações, médias por produto e moderação administrativa. |
| [PedidoVendaService.java](PedidoVendaService.java) | Contrato para simular descontos, finalizar pedidos e alterar status de vendas. |
| [ProdutoEstoqueService.java](ProdutoEstoqueService.java) | Contrato para produtos, consultas de estoque, entradas e perdas. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [RelatorioFinanceiroService.java](RelatorioFinanceiroService.java) | Contrato de cálculos de balanço, gráficos e exportação CSV. |
| [WhatsAppLinkService.java](WhatsAppLinkService.java) | Constrói e abre links de mensagens WhatsApp. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/service/`.

```text
src/main/java/com/salgaderia/app/service/  # Define contratos e serviços de aplicação responsáveis por regras e orquestração.
├── desconto/  # Estratégias de desconto e testes correspondentes.
│   ├── CalculadoraDescontos.java  # Seleciona o maior desconto elegível.
│   ├── ContextoDesconto.java  # Dados de entrada para estratégias de desconto.
│   ├── Cupom.java  # Valor de domínio de cupom.
│   ├── DescontoCentoSalgados.java  # Estratégia de desconto para cento de salgados.
│   ├── DescontoCupomPercentual.java  # Estratégia percentual por cupom.
│   ├── DescontoFidelidade.java  # Estratégia para clientes fiéis.
│   ├── EstrategiaDesconto.java  # Contrato Strategy para descontos.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── ResultadoDesconto.java  # Resumo do desconto e total calculados.
├── impl/  # Implementações de regras e orquestração dos serviços.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── AgendaServiceImpl.java  # Valida e coordena cadastro, busca e alterações de contatos pela camada DAO.
│   ├── AutenticacaoServiceImpl.java  # Orquestra login, verificação de senha e cadastro de cliente e usuário.
│   ├── AvaliacaoServiceImpl.java  # Valida avaliações elegíveis e controla operações de moderação.
│   ├── PedidoVendaServiceImpl.java  # Executa vendas transacionais, descontos, movimentações de estoque e transições.
│   ├── ProdutoEstoqueServiceImpl.java  # Valida produtos e coordena entradas, perdas e consultas de estoque.
│   ├── README.md  # Guia de navegação e explicações desta pasta.
│   └── RelatorioFinanceiroServiceImpl.java  # Calcula indicadores financeiros e gera o balanço CSV.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── AgendaService.java  # Contrato de operações e busca da agenda de clientes e fornecedores.
├── AutenticacaoService.java  # Contrato para autenticação única e cadastro público de clientes.
├── AvaliacaoService.java  # Contrato para avaliações, médias por produto e moderação administrativa.
├── PedidoVendaService.java  # Contrato para simular descontos, finalizar pedidos e alterar status de vendas.
├── ProdutoEstoqueService.java  # Contrato para produtos, consultas de estoque, entradas e perdas.
├── README.md  # Guia de navegação e explicações desta pasta.
├── RelatorioFinanceiroService.java  # Contrato de cálculos de balanço, gráficos e exportação CSV.
└── WhatsAppLinkService.java  # Constrói e abre links de mensagens WhatsApp.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
