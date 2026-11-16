[🏠 Início](../../README.md) / [⬆️ src](../README.md) / 📂 test

# 🧪 Testes automatizados

Contém os testes das regras de domínio, serviços, persistência e interface quando presentes.

Testes de integração e JavaFX podem exigir banco local ou ambiente gráfico; consulte as instruções da raiz.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [java/](java/) | Pacotes de código Java. [Abrir guia](java/README.md). |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |

## Árvore estrutural comentada

O ponto inicial é `src/test/`.

```text
src/test/  # Contém os testes das regras de domínio, serviços, persistência e interface quando presentes.
├── java/  # Pacotes de código Java.
│   ├── com/  # Raiz de pacotes Java.
│   │   ├── salgaderia/  # Pacote da aplicação Salgaderia.
│   │   │   ├── app/  # Componentes da aplicação e suas camadas.
│   │   │   │   ├── controller/  # Testes automatizados dos componentes desta camada.
│   │   │   │   │   ├── AdminDashboardJavaFXTest.java  # Testes JavaFX do painel e notificações.
│   │   │   │   │   ├── AgendaJavaFXTest.java  # Testes JavaFX da agenda.
│   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   ├── TelasJavaFXTest.java  # Testes JavaFX de autenticação e navegação.
│   │   │   │   │   └── VitrineJavaFXTest.java  # Testes JavaFX da vitrine, pagamento e atualização.
│   │   │   │   ├── dao/  # Testes automatizados dos componentes desta camada.
│   │   │   │   │   └── AgendaJDBCTest.java  # Teste de integração JDBC da agenda.
│   │   │   │   ├── model/  # Testes automatizados dos componentes desta camada.
│   │   │   │   │   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   │   │   │   │   └── EnumsTest.java  # Testes de comportamento dos enums.
│   │   │   │   │   └── DominioTest.java  # Testes das entidades e regras de domínio.
│   │   │   │   ├── service/  # Testes automatizados dos componentes desta camada.
│   │   │   │   │   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   │   │   │   │   └── EstrategiasDescontoTest.java  # Testa as estratégias e a escolha do melhor desconto.
│   │   │   │   │   ├── DescontoIntegracaoTest.java  # Integração de descontos e pagamentos no MySQL.
│   │   │   │   │   ├── Etapa4IntegracaoTest.java  # Fluxos transacionais no MySQL.
│   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   ├── RelatorioCsvTest.java  # Verifica o balanço exportado em CSV.
│   │   │   │   │   └── WhatsAppLinkServiceTest.java  # Testa links, mensagens e configuração WhatsApp.
│   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   └── README.md  # Guia de navegação e explicações desta pasta.
└── README.md  # Guia de navegação e explicações desta pasta.
```

[🏠 Voltar ao README principal](../../README.md)
