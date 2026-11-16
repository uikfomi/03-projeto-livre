[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 service

# 🧪 service — testes

Reúne testes automatizados dos componentes presentes neste pacote.

Execute os testes compatíveis com o ambiente; os testes de integração podem exigir banco ou display.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [desconto/](desconto/) | Estratégias de desconto e testes correspondentes. |
| [ComandaIntegracaoTest.java](ComandaIntegracaoTest.java) | Valida operações de mesa, pagamentos concorrentes, baixa e fechamento no MySQL. |
| [CozinhaStateTest.java](CozinhaStateTest.java) | Verifica mensagens e progresso dos estados da cozinha. |
| [DescontoIntegracaoTest.java](DescontoIntegracaoTest.java) | Integração de descontos e pagamentos no MySQL. |
| [Etapa4IntegracaoTest.java](Etapa4IntegracaoTest.java) | Fluxos transacionais no MySQL. |
| [PedidoAgendamentoTest.java](PedidoAgendamentoTest.java) | Verifica rejeição de agendamento passado. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [RelatorioCsvTest.java](RelatorioCsvTest.java) | Verifica o balanço exportado em CSV. |
| [WhatsAppLinkServiceTest.java](WhatsAppLinkServiceTest.java) | Testa links, mensagens e configuração WhatsApp. |

## Árvore estrutural comentada

O ponto inicial é `src/test/java/com/salgaderia/app/service/`.

```text
src/test/java/com/salgaderia/app/service/  # Reúne testes automatizados dos componentes presentes neste pacote.
├── desconto/  # Estratégias de desconto e testes correspondentes.
│   └── EstrategiasDescontoTest.java  # Testa as estratégias e a escolha do melhor desconto.
├── ComandaIntegracaoTest.java  # Valida operações de mesa, pagamentos concorrentes, baixa e fechamento no MySQL.
├── CozinhaStateTest.java  # Verifica mensagens e progresso dos estados da cozinha.
├── DescontoIntegracaoTest.java  # Integração de descontos e pagamentos no MySQL.
├── Etapa4IntegracaoTest.java  # Fluxos transacionais no MySQL.
├── PedidoAgendamentoTest.java  # Verifica rejeição de agendamento passado.
├── README.md  # Guia de navegação e explicações desta pasta.
├── RelatorioCsvTest.java  # Verifica o balanço exportado em CSV.
└── WhatsAppLinkServiceTest.java  # Testa links, mensagens e configuração WhatsApp.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
