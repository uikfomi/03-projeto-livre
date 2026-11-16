[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 util

# 🔧 Infraestrutura

Concentra conexão, navegação, autenticação auxiliar e suporte às telas, quando presentes.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [ConnectionFactory.java](ConnectionFactory.java) | Pool HikariCP e transações JDBC. |
| [Navegacao.java](Navegacao.java) | Navegação entre telas FXML. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [Senhas.java](Senhas.java) | Geração e verificação de hashes de senha. |
| [TelaAutenticada.java](TelaAutenticada.java) | Contrato para telas com usuário autenticado. |
| [WhatsAppUi.java](WhatsAppUi.java) | Feedback visual e alternativa para links WhatsApp. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/util/`; cada comentário apresenta o caminho completo relativo à raiz do repositório.

```text
src/main/java/com/salgaderia/app/util/  # Concentra conexão, navegação, autenticação auxiliar e suporte às telas, quando presentes.
├── .gitkeep  # src/main/java/com/salgaderia/app/util/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── ConnectionFactory.java  # src/main/java/com/salgaderia/app/util/ConnectionFactory.java — Pool HikariCP e transações JDBC.
├── Navegacao.java  # src/main/java/com/salgaderia/app/util/Navegacao.java — Navegação entre telas FXML.
├── README.md  # src/main/java/com/salgaderia/app/util/README.md — Guia de navegação e explicações desta pasta.
├── Senhas.java  # src/main/java/com/salgaderia/app/util/Senhas.java — Geração e verificação de hashes de senha.
├── TelaAutenticada.java  # src/main/java/com/salgaderia/app/util/TelaAutenticada.java — Contrato para telas com usuário autenticado.
└── WhatsAppUi.java  # src/main/java/com/salgaderia/app/util/WhatsAppUi.java — Feedback visual e alternativa para links WhatsApp.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
