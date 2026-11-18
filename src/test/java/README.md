[🏠 Início](../../../README.md) / [⬆️ test](../README.md) / 📂 java

# 🧪 java — testes

Reúne testes automatizados dos componentes presentes neste pacote.

Execute os testes compatíveis com o ambiente; os testes de integração podem exigir banco ou display.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [com/](com/) | Raiz de pacotes Java. [Abrir guia](com/README.md). |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |

## Árvore estrutural comentada

O ponto inicial é `src/test/java/`.

```text
src/test/java/  # Reúne testes automatizados dos componentes presentes neste pacote.
├── com/  # Raiz de pacotes Java.
│   ├── salgaderia/  # Pacote da aplicação Salgaderia.
│   │   ├── app/  # Componentes da aplicação e suas camadas.
│   │   │   ├── controller/  # Controllers das telas e testes de interface, conforme a localização.
│   │   │   │   ├── AdminDashboardJavaFXTest.java  # Testes JavaFX do painel e notificações.
│   │   │   │   ├── AgendaJavaFXTest.java  # Testes JavaFX da agenda.
│   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   ├── TelasJavaFXTest.java  # Testes JavaFX de autenticação e navegação.
│   │   │   │   └── VitrineJavaFXTest.java  # Verifica a vitrine JavaFX, disponibilidade, checkout, combo, caixa e galeria.
│   │   │   ├── dao/  # Contratos de acesso a dados ou testes JDBC.
│   │   │   │   └── AgendaJDBCTest.java  # Teste de integração JDBC da agenda.
│   │   │   ├── model/  # Entidades de domínio ou testes dessas entidades.
│   │   │   │   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   │   │   │   └── EnumsTest.java  # Testes de comportamento dos enums.
│   │   │   │   └── DominioTest.java  # Testes das entidades e regras de domínio.
│   │   │   ├── service/  # Contratos, regras de negócio e testes de serviços.
│   │   │   │   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   │   │   │   └── EstrategiasDescontoTest.java  # Testa as estratégias e a escolha do melhor desconto.
│   │   │   │   ├── ComandaIntegracaoTest.java  # Valida operações de mesa, pagamentos concorrentes, baixa e fechamento no MySQL.
│   │   │   │   ├── CozinhaStateTest.java  # Verifica mensagens e progresso dos estados da cozinha.
│   │   │   │   ├── DescontoIntegracaoTest.java  # Integração de descontos e pagamentos no MySQL.
│   │   │   │   ├── Etapa4IntegracaoTest.java  # Verifica transações de estoque, pedidos, PDV, avaliações com foto e financeiro no MySQL.
│   │   │   │   ├── FotoAvaliacaoStorageTest.java  # Testa a cópia validada, limites, caminhos locais e remoção de anexos.
│   │   │   │   ├── PedidoAgendamentoTest.java  # Verifica rejeição de agendamento passado.
│   │   │   │   ├── PersonalizacaoServiceTest.java  # Testa a composição do combo, sorteio com restrições e desconto não cumulativo.
│   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   ├── RelatorioCsvTest.java  # Verifica o balanço exportado em CSV.
│   │   │   │   ├── VitrineV150IntegracaoTest.java  # Valida baixa e devolução de estoque, restrições e histórico de combos e caixas no MySQL.
│   │   │   │   └── WhatsAppLinkServiceTest.java  # Testa links, mensagens e configuração WhatsApp.
│   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   └── README.md  # Guia de navegação e explicações desta pasta.
└── README.md  # Guia de navegação e explicações desta pasta.
```

[🏠 Voltar ao README principal](../../../README.md)
