[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 exception

# 🚨 Exceções

Define falhas específicas do domínio, autenticação e persistência para tratamento consistente.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [.gitkeep](.gitkeep) | Marcador que mantém a pasta no controle de versão; não contém dados de aplicação. |
| [AutenticacaoInvalidaException.java](AutenticacaoInvalidaException.java) | Falha de autenticação. |
| [AvaliacaoNaoPermitidaException.java](AvaliacaoNaoPermitidaException.java) | Avaliação sem compra elegível. |
| [EstoqueInsuficienteException.java](EstoqueInsuficienteException.java) | Indica saldo físico insuficiente para uma movimentação de estoque. |
| [PersistenciaException.java](PersistenciaException.java) | Encapsula erro de persistência. |
| [ProdutoIndisponivelException.java](ProdutoIndisponivelException.java) | Indica que um produto foi bloqueado para venda, independentemente do estoque. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/exception/`.

```text
src/main/java/com/salgaderia/app/exception/  # Define falhas específicas do domínio, autenticação e persistência para tratamento consistente.
├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
├── AutenticacaoInvalidaException.java  # Falha de autenticação.
├── AvaliacaoNaoPermitidaException.java  # Avaliação sem compra elegível.
├── EstoqueInsuficienteException.java  # Indica saldo físico insuficiente para uma movimentação de estoque.
├── PersistenciaException.java  # Encapsula erro de persistência.
├── ProdutoIndisponivelException.java  # Indica que um produto foi bloqueado para venda, independentemente do estoque.
└── README.md  # Guia de navegação e explicações desta pasta.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
