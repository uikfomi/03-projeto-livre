[🏠 Início](../../../../../../../../README.md) / [⬆️ service](../README.md) / 📂 cozinha

# 🌡️ Estados da cozinha

Implementação State do termômetro exibido no histórico de pedidos.

Os estados traduzem o status persistido para mensagem e progresso; o tempo exibido é uma estimativa, não uma promessa de entrega.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [EstadoCozinha.java](EstadoCozinha.java) | Contrato State de mensagem, progresso e espera. |
| [EtapaCozinha.java](EtapaCozinha.java) | Mapeia os status persistidos aos estados visuais da cozinha. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/service/cozinha/`.

```text
cozinha/  # Estados visuais de preparo do pedido.
├── EstadoCozinha.java  # Contrato State de mensagem, progresso e espera.
├── EtapaCozinha.java  # Mapeia os status persistidos aos estados visuais da cozinha.
└── README.md  # Guia de navegação e explicações desta pasta.
```

[🏠 Voltar ao README principal](../../../../../../../../README.md)
