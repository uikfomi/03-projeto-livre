[🏠 Início](../../../../../README.md) / [⬆️ com](../README.md) / 📂 salgaderia

# 🧪 salgaderia — testes

Reúne testes automatizados dos componentes presentes neste pacote.

Execute os testes compatíveis com o ambiente; os testes de integração podem exigir banco ou display.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [app/](app/) | Componentes da aplicação e suas camadas. [Abrir guia](app/README.md). |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |

## Árvore estrutural comentada

O ponto inicial é `src/test/java/com/salgaderia/`.

```text
src/test/java/com/salgaderia/  # Reúne testes automatizados dos componentes presentes neste pacote.
├── app/  # Componentes da aplicação e suas camadas.
│   ├── controller/  # Controllers das telas e testes de interface, conforme a localização.
│   │   ├── AdminDashboardJavaFXTest.java  # Testes JavaFX do painel e notificações.
│   │   ├── AgendaJavaFXTest.java  # Testes JavaFX da agenda.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   ├── TelasJavaFXTest.java  # Testes JavaFX de autenticação e navegação.
│   │   └── VitrineJavaFXTest.java  # Testes JavaFX da vitrine, pagamento e atualização.
│   ├── dao/  # Contratos de acesso a dados ou testes JDBC.
│   │   └── AgendaJDBCTest.java  # Teste de integração JDBC da agenda.
│   ├── model/  # Entidades de domínio ou testes dessas entidades.
│   │   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   │   └── EnumsTest.java  # Testes de comportamento dos enums.
│   │   └── DominioTest.java  # Testes das entidades e regras de domínio.
│   ├── service/  # Contratos, regras de negócio e testes de serviços.
│   │   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   │   └── EstrategiasDescontoTest.java  # Testa as estratégias e a escolha do melhor desconto.
│   │   ├── ComandaIntegracaoTest.java  # Valida operações de mesa, pagamentos concorrentes, baixa e fechamento no MySQL.
│   │   ├── CozinhaStateTest.java  # Verifica mensagens e progresso dos estados da cozinha.
│   │   ├── DescontoIntegracaoTest.java  # Integração de descontos e pagamentos no MySQL.
│   │   ├── Etapa4IntegracaoTest.java  # Fluxos transacionais no MySQL.
│   │   ├── PedidoAgendamentoTest.java  # Verifica rejeição de agendamento passado.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   ├── RelatorioCsvTest.java  # Verifica o balanço exportado em CSV.
│   │   └── WhatsAppLinkServiceTest.java  # Testa links, mensagens e configuração WhatsApp.
│   └── README.md  # Guia de navegação e explicações desta pasta.
└── README.md  # Guia de navegação e explicações desta pasta.
```

[🏠 Voltar ao README principal](../../../../../README.md)
