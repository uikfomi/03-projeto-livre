[🏠 Início](../../../../../../../../README.md) / [⬆️ service](../README.md) / 📂 desconto

# 🏷️ Estratégias de desconto

Agrupa os contratos, contextos e implementações Strategy para calcular descontos.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [CalculadoraDescontos.java](CalculadoraDescontos.java) | Mantém caixas no preço fechado, isenta frete VIP e escolhe o melhor benefício elegível. |
| [ContextoDesconto.java](ContextoDesconto.java) | Agrupa itens, cupom, frete e fidelidade e separa mercadorias elegíveis para descontos. |
| [Cupom.java](Cupom.java) | Representa percentual, frete, brinde ou bônus vinculado ao cupom. |
| [DescontoAssinante.java](DescontoAssinante.java) | Calcula o desconto do membro VIP nos itens elegíveis. |
| [DescontoCaixaMisteriosa.java](DescontoCaixaMisteriosa.java) | Ajusta o valor de catálogo de cada caixa ao preço fechado da faixa escolhida. |
| [DescontoCentoSalgados.java](DescontoCentoSalgados.java) | Estratégia de desconto para cento de salgados. |
| [DescontoCombo.java](DescontoCombo.java) | Calcula 10% de desconto sobre os itens do combo simples. |
| [DescontoCupomPercentual.java](DescontoCupomPercentual.java) | Estratégia percentual por cupom. |
| [DescontoFidelidade.java](DescontoFidelidade.java) | Estratégia para clientes fiéis. |
| [EstrategiaDesconto.java](EstrategiaDesconto.java) | Contrato Strategy para descontos. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [ResultadoDesconto.java](ResultadoDesconto.java) | Informa total, desconto, frete efetivo, isenção VIP e eventual brinde. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/service/desconto/`.

```text
src/main/java/com/salgaderia/app/service/desconto/  # Agrupa os contratos, contextos e implementações Strategy para calcular descontos.
├── CalculadoraDescontos.java  # Mantém caixas no preço fechado, isenta frete VIP e escolhe o melhor benefício elegível.
├── ContextoDesconto.java  # Agrupa itens, cupom, frete e fidelidade e separa mercadorias elegíveis para descontos.
├── Cupom.java  # Representa percentual, frete, brinde ou bônus vinculado ao cupom.
├── DescontoAssinante.java  # Calcula o desconto do membro VIP nos itens elegíveis.
├── DescontoCaixaMisteriosa.java  # Ajusta o valor de catálogo de cada caixa ao preço fechado da faixa escolhida.
├── DescontoCentoSalgados.java  # Estratégia de desconto para cento de salgados.
├── DescontoCombo.java  # Calcula 10% de desconto sobre os itens do combo simples.
├── DescontoCupomPercentual.java  # Estratégia percentual por cupom.
├── DescontoFidelidade.java  # Estratégia para clientes fiéis.
├── EstrategiaDesconto.java  # Contrato Strategy para descontos.
├── README.md  # Guia de navegação e explicações desta pasta.
└── ResultadoDesconto.java  # Informa total, desconto, frete efetivo, isenção VIP e eventual brinde.
```

[🏠 Voltar ao README principal](../../../../../../../../README.md)
