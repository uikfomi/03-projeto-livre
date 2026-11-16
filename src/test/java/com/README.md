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

O ponto inicial é `src/test/java/com/`; cada comentário apresenta o caminho completo relativo à raiz do repositório.

```text
src/test/java/com/  # Reúne testes automatizados dos componentes presentes neste pacote.
├── salgaderia/  # src/test/java/com/salgaderia/ — Pacote da aplicação Salgaderia.
│   ├── app/  # src/test/java/com/salgaderia/app/ — Componentes da aplicação e suas camadas.
│   │   ├── controller/  # src/test/java/com/salgaderia/app/controller/ — Testes automatizados dos componentes desta camada.
│   │   │   ├── AdminDashboardJavaFXTest.java  # src/test/java/com/salgaderia/app/controller/AdminDashboardJavaFXTest.java — Testes JavaFX do painel e notificações.
│   │   │   ├── AgendaJavaFXTest.java  # src/test/java/com/salgaderia/app/controller/AgendaJavaFXTest.java — Testes JavaFX da agenda.
│   │   │   ├── README.md  # src/test/java/com/salgaderia/app/controller/README.md — Guia de navegação e explicações desta pasta.
│   │   │   ├── TelasJavaFXTest.java  # src/test/java/com/salgaderia/app/controller/TelasJavaFXTest.java — Testes JavaFX de autenticação e navegação.
│   │   │   └── VitrineJavaFXTest.java  # src/test/java/com/salgaderia/app/controller/VitrineJavaFXTest.java — Testes JavaFX da vitrine, pagamento e atualização.
│   │   ├── dao/  # src/test/java/com/salgaderia/app/dao/ — Testes automatizados dos componentes desta camada.
│   │   │   └── AgendaJDBCTest.java  # src/test/java/com/salgaderia/app/dao/AgendaJDBCTest.java — Teste de integração JDBC da agenda.
│   │   ├── model/  # src/test/java/com/salgaderia/app/model/ — Testes automatizados dos componentes desta camada.
│   │   │   ├── enums/  # src/test/java/com/salgaderia/app/model/enums/ — Enumerações com comportamento e respectivos testes.
│   │   │   │   └── EnumsTest.java  # src/test/java/com/salgaderia/app/model/enums/EnumsTest.java — Testes de comportamento dos enums.
│   │   │   └── DominioTest.java  # src/test/java/com/salgaderia/app/model/DominioTest.java — Testes das entidades e regras de domínio.
│   │   ├── service/  # src/test/java/com/salgaderia/app/service/ — Testes automatizados dos componentes desta camada.
│   │   │   ├── desconto/  # src/test/java/com/salgaderia/app/service/desconto/ — Estratégias de desconto e testes correspondentes.
│   │   │   │   └── EstrategiasDescontoTest.java  # src/test/java/com/salgaderia/app/service/desconto/EstrategiasDescontoTest.java — Testa as estratégias e a escolha do melhor desconto.
│   │   │   ├── DescontoIntegracaoTest.java  # src/test/java/com/salgaderia/app/service/DescontoIntegracaoTest.java — Integração de descontos e pagamentos no MySQL.
│   │   │   ├── Etapa4IntegracaoTest.java  # src/test/java/com/salgaderia/app/service/Etapa4IntegracaoTest.java — Fluxos transacionais no MySQL.
│   │   │   ├── README.md  # src/test/java/com/salgaderia/app/service/README.md — Guia de navegação e explicações desta pasta.
│   │   │   ├── RelatorioCsvTest.java  # src/test/java/com/salgaderia/app/service/RelatorioCsvTest.java — Verifica o balanço exportado em CSV.
│   │   │   └── WhatsAppLinkServiceTest.java  # src/test/java/com/salgaderia/app/service/WhatsAppLinkServiceTest.java — Testa links, mensagens e configuração WhatsApp.
│   │   └── README.md  # src/test/java/com/salgaderia/app/README.md — Guia de navegação e explicações desta pasta.
│   └── README.md  # src/test/java/com/salgaderia/README.md — Guia de navegação e explicações desta pasta.
└── README.md  # src/test/java/com/README.md — Guia de navegação e explicações desta pasta.
```

[🏠 Voltar ao README principal](../../../../README.md)
