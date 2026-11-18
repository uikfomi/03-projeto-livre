[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 util

# 🔧 Infraestrutura

Concentra conexão, navegação, autenticação auxiliar e suporte às telas, quando presentes.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [ConnectionFactory.java](ConnectionFactory.java) | Pool HikariCP e transações JDBC. |
| [FotoAvaliacaoUi.java](FotoAvaliacaoUi.java) | Carrega miniaturas JavaFX de fotos de avaliações guardadas localmente. |
| [Navegacao.java](Navegacao.java) | Navegação entre telas FXML. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [Senhas.java](Senhas.java) | Geração e verificação de hashes de senha. |
| [TelaAutenticada.java](TelaAutenticada.java) | Contrato para telas com usuário autenticado. |
| [WhatsAppUi.java](WhatsAppUi.java) | Feedback visual e alternativa para links WhatsApp. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/util/`.

```text
src/main/java/com/salgaderia/app/util/  # Concentra conexão, navegação, autenticação auxiliar e suporte às telas, quando presentes.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── ConnectionFactory.java  # Pool HikariCP e transações JDBC.
├── FotoAvaliacaoUi.java  # Carrega miniaturas JavaFX de fotos de avaliações guardadas localmente.
├── Navegacao.java  # Navegação entre telas FXML.
├── README.md  # Guia de navegação e explicações desta pasta.
├── Senhas.java  # Geração e verificação de hashes de senha.
├── TelaAutenticada.java  # Contrato para telas com usuário autenticado.
└── WhatsAppUi.java  # Feedback visual e alternativa para links WhatsApp.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
