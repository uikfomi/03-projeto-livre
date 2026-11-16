[🏠 Início](../../../README.md) / [⬆️ main](../README.md) / 📂 resources

# 🎨 Recursos da aplicação

Agrupa telas FXML, estilos e configuração padrão consumidos pelo JavaFX e pela infraestrutura.

O arquivo database.local.properties é local e ignorado pelo Git; verifique se artefatos distribuídos não contêm credenciais.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [css/](css/) | Folhas de estilo das telas JavaFX. [Abrir guia](css/README.md). |
| [fxml/](fxml/) | Descrições de interface JavaFX. [Abrir guia](fxml/README.md). |
| [icons/](icons/) | Espaço reservado para ícones. |
| [images/](images/) | Espaço reservado para imagens. |
| [database.properties](database.properties) | Template de configuração JDBC; não contém credenciais locais. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |

## Árvore estrutural comentada

O ponto inicial é `src/main/resources/`; cada comentário apresenta o caminho completo relativo à raiz do repositório.

```text
src/main/resources/  # Agrupa telas FXML, estilos e configuração padrão consumidos pelo JavaFX e pela infraestrutura.
├── css/  # src/main/resources/css/ — Folhas de estilo das telas JavaFX.
│   ├── .gitkeep  # src/main/resources/css/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── admin.css  # src/main/resources/css/admin.css — Estilos do painel administrativo.
│   ├── agenda.css  # src/main/resources/css/agenda.css — Estilos da agenda.
│   ├── cliente-vitrine.css  # src/main/resources/css/cliente-vitrine.css — Estilos da vitrine.
│   ├── global.css  # src/main/resources/css/global.css — Estilos compartilhados da aplicação.
│   └── README.md  # src/main/resources/css/README.md — Guia de navegação e explicações desta pasta.
├── fxml/  # src/main/resources/fxml/ — Descrições de interface JavaFX.
│   ├── .gitkeep  # src/main/resources/fxml/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── admin-dashboard.fxml  # src/main/resources/fxml/admin-dashboard.fxml — Estrutura visual do painel administrativo.
│   ├── agenda-contatos.fxml  # src/main/resources/fxml/agenda-contatos.fxml — Estrutura visual da agenda.
│   ├── cliente-vitrine.fxml  # src/main/resources/fxml/cliente-vitrine.fxml — Estrutura visual da vitrine e checkout.
│   ├── login.fxml  # src/main/resources/fxml/login.fxml — Estrutura visual de login e cadastro.
│   ├── README.md  # src/main/resources/fxml/README.md — Guia de navegação e explicações desta pasta.
│   └── selecao-modulos.fxml  # src/main/resources/fxml/selecao-modulos.fxml — Estrutura visual da seleção de módulos.
├── icons/  # src/main/resources/icons/ — Espaço reservado para ícones.
│   └── .gitkeep  # src/main/resources/icons/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── images/  # src/main/resources/images/ — Espaço reservado para imagens.
│   └── .gitkeep  # src/main/resources/images/.gitkeep — Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── database.properties  # src/main/resources/database.properties — Template de configuração JDBC; não contém credenciais locais.
└── README.md  # src/main/resources/README.md — Guia de navegação e explicações desta pasta.
```

## 📌 Status do Conteúdo

As pastas abaixo estão reservadas e aguardam materiais finais; os arquivos `.gitkeep` apenas preservam a estrutura:

- `src/main/resources/icons/` — Espaço reservado para ícones.
- `src/main/resources/images/` — Espaço reservado para imagens.

[🏠 Voltar ao README principal](../../../README.md)
