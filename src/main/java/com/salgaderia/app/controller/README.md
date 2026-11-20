[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 controller

# 🖥️ Controllers JavaFX

Coordena a interação das telas e delega operações de negócio aos serviços.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [AdminDashboardController.java](AdminDashboardController.java) | Coordena PDV, fila e despacho, expediente, catálogo e demais operações gerenciais. |
| [AgendaController.java](AgendaController.java) | Interface da agenda de clientes e fornecedores. |
| [ClienteVitrineController.java](ClienteVitrineController.java) | Coordena catálogo, carrinho, expediente e acompanhamento do pedido em rota. |
| [LoginController.java](LoginController.java) | Tela de login e cadastro público. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [SelecaoModulosController.java](SelecaoModulosController.java) | Seleção de módulos administrativos. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/controller/`.

```text
src/main/java/com/salgaderia/app/controller/  # Coordena a interação das telas e delega operações de negócio aos serviços.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── AdminDashboardController.java  # Coordena PDV, fila e despacho, expediente, catálogo e demais operações gerenciais.
├── AgendaController.java  # Interface da agenda de clientes e fornecedores.
├── ClienteVitrineController.java  # Coordena catálogo, carrinho, expediente e acompanhamento do pedido em rota.
├── LoginController.java  # Tela de login e cadastro público.
├── README.md  # Guia de navegação e explicações desta pasta.
└── SelecaoModulosController.java  # Seleção de módulos administrativos.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
