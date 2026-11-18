[🏠 Início](../../../../../../../../README.md) / [⬆️ model](../README.md) / 📂 comanda

# 🪑 Domínio de comandas

Tipos imutáveis que representam mesas, itens, pagamentos e o saldo de uma comanda coletiva.

Os saldos são derivados dos itens e pagamentos; as operações transacionais são coordenadas pelo serviço de comanda.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [Comanda.java](Comanda.java) | Sessão da mesa e cálculo monetário de total, pagamentos e saldo. |
| [ItemComanda.java](ItemComanda.java) | Item lançado na comanda, com quantidade disponível e cotação por unidade. |
| [Mesa.java](Mesa.java) | Identificação, número e status da mesa. |
| [PagamentoComanda.java](PagamentoComanda.java) | Dados de pagamento parcial por valor ou item. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [StatusMesa.java](StatusMesa.java) | Estados livre e ocupada e suas permissões. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/model/comanda/`.

```text
comanda/  # Domínio das mesas e comandas coletivas.
├── Comanda.java  # Sessão da mesa e cálculo monetário de total, pagamentos e saldo.
├── ItemComanda.java  # Item lançado na comanda, com quantidade disponível e cotação por unidade.
├── Mesa.java  # Identificação, número e status da mesa.
├── PagamentoComanda.java  # Dados de pagamento parcial por valor ou item.
├── README.md  # Guia de navegação e explicações desta pasta.
└── StatusMesa.java  # Estados livre e ocupada e suas permissões.
```

[🏠 Voltar ao README principal](../../../../../../../../README.md)
