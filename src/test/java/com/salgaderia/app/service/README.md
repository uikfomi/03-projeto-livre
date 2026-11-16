[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 service

# 🧪 service — testes

Reúne testes automatizados dos componentes presentes neste pacote.

Execute os testes compatíveis com o ambiente; os testes de integração podem exigir banco ou display.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [desconto/](desconto/) | Estratégias de desconto e testes correspondentes. |
| [DescontoIntegracaoTest.java](DescontoIntegracaoTest.java) | Integração de descontos e pagamentos no MySQL. |
| [Etapa4IntegracaoTest.java](Etapa4IntegracaoTest.java) | Fluxos transacionais no MySQL. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [RelatorioCsvTest.java](RelatorioCsvTest.java) | Verifica o balanço exportado em CSV. |
| [WhatsAppLinkServiceTest.java](WhatsAppLinkServiceTest.java) | Testa links, mensagens e configuração WhatsApp. |

## Árvore estrutural comentada

O ponto inicial é `src/test/java/com/salgaderia/app/service/`; cada comentário apresenta o caminho completo relativo à raiz do repositório.

```text
src/test/java/com/salgaderia/app/service/  # Reúne testes automatizados dos componentes presentes neste pacote.
├── desconto/  # src/test/java/com/salgaderia/app/service/desconto/ — Estratégias de desconto e testes correspondentes.
│   └── EstrategiasDescontoTest.java  # src/test/java/com/salgaderia/app/service/desconto/EstrategiasDescontoTest.java — Testa as estratégias e a escolha do melhor desconto.
├── DescontoIntegracaoTest.java  # src/test/java/com/salgaderia/app/service/DescontoIntegracaoTest.java — Integração de descontos e pagamentos no MySQL.
├── Etapa4IntegracaoTest.java  # src/test/java/com/salgaderia/app/service/Etapa4IntegracaoTest.java — Fluxos transacionais no MySQL.
├── README.md  # src/test/java/com/salgaderia/app/service/README.md — Guia de navegação e explicações desta pasta.
├── RelatorioCsvTest.java  # src/test/java/com/salgaderia/app/service/RelatorioCsvTest.java — Verifica o balanço exportado em CSV.
└── WhatsAppLinkServiceTest.java  # src/test/java/com/salgaderia/app/service/WhatsAppLinkServiceTest.java — Testa links, mensagens e configuração WhatsApp.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
