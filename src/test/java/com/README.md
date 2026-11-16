[🏠 Início](../../../../README.md) / [⬆️ java](../README.md) / 📂 com

# 🧪 com — testes

Reúne testes automatizados dos componentes presentes neste pacote.

Execute os testes compatíveis com o ambiente; os testes de integração podem exigir banco ou display.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [salgaderia/](salgaderia/) | Pacote da aplicação Salgaderia. [Abrir guia](salgaderia/README.md). |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |

## Árvore estrutural comentada

O ponto inicial é `src/test/java/com/`.

```text
src/test/java/com/  # Reúne testes automatizados dos componentes presentes neste pacote.
├── salgaderia/  # Pacote da aplicação Salgaderia.
│   ├── app/  # Componentes da aplicação e suas camadas.
│   │   ├── controller/  # Testes automatizados dos componentes desta camada.
│   │   │   ├── AdminDashboardJavaFXTest.java  # Testes JavaFX do painel e notificações.
│   │   │   ├── AgendaJavaFXTest.java  # Testes JavaFX da agenda.
│   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   ├── TelasJavaFXTest.java  # Testes JavaFX de autenticação e navegação.
│   │   │   └── VitrineJavaFXTest.java  # Testes JavaFX da vitrine, pagamento e atualização.
│   │   ├── dao/  # Testes automatizados dos componentes desta camada.
│   │   │   └── AgendaJDBCTest.java  # Teste de integração JDBC da agenda.
│   │   ├── model/  # Testes automatizados dos componentes desta camada.
│   │   │   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   │   │   └── EnumsTest.java  # Testes de comportamento dos enums.
│   │   │   └── DominioTest.java  # Testes das entidades e regras de domínio.
│   │   ├── service/  # Testes automatizados dos componentes desta camada.
│   │   │   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   │   │   └── EstrategiasDescontoTest.java  # Testa as estratégias e a escolha do melhor desconto.
│   │   │   ├── DescontoIntegracaoTest.java  # Integração de descontos e pagamentos no MySQL.
│   │   │   ├── Etapa4IntegracaoTest.java  # Fluxos transacionais no MySQL.
│   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   ├── RelatorioCsvTest.java  # Verifica o balanço exportado em CSV.
│   │   │   └── WhatsAppLinkServiceTest.java  # Testa links, mensagens e configuração WhatsApp.
│   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   └── README.md  # Guia de navegação e explicações desta pasta.
└── README.md  # Guia de navegação e explicações desta pasta.
```

[🏠 Voltar ao README principal](../../../../README.md)
