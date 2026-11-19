[🏠 Início](../../../../../../../../README.md) / [⬆️ service](../README.md) / 📂 desconto

# 🏷️ Estratégias de desconto

Agrupa os contratos, contextos e implementações Strategy para calcular descontos.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [CalculadoraDescontos.java](CalculadoraDescontos.java) | Escolhe uma única vantagem entre estratégias de desconto, frete, brindes e pontos. |
| [ContextoDesconto.java](ContextoDesconto.java) | Agrupa itens, taxa de entrega, fidelidade, cupom e benefício do assinante. |
| [Cupom.java](Cupom.java) | Representa percentual, frete, brinde ou bônus vinculado ao cupom. |
| [DescontoAssinante.java](DescontoAssinante.java) | Calcula o desconto de assinatura ativa como candidato não cumulativo. |
| [DescontoCaixaMisteriosa.java](DescontoCaixaMisteriosa.java) | Oferece 15% sobre o valor de cada caixa como desconto não cumulativo. |
| [DescontoCentoSalgados.java](DescontoCentoSalgados.java) | Estratégia de desconto para cento de salgados. |
| [DescontoCupomPercentual.java](DescontoCupomPercentual.java) | Estratégia percentual por cupom. |
| [DescontoFidelidade.java](DescontoFidelidade.java) | Estratégia para clientes fiéis. |
| [EstrategiaDesconto.java](EstrategiaDesconto.java) | Contrato Strategy para descontos. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [ResultadoDesconto.java](ResultadoDesconto.java) | Retorna valor final, taxa de entrega, desconto escolhido e eventual brinde. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/service/desconto/`.

```text
src/main/java/com/salgaderia/app/service/desconto/  # Agrupa os contratos, contextos e implementações Strategy para calcular descontos.
├── CalculadoraDescontos.java  # Escolhe uma única vantagem entre estratégias de desconto, frete, brindes e pontos.
├── ContextoDesconto.java  # Agrupa itens, taxa de entrega, fidelidade, cupom e benefício do assinante.
├── Cupom.java  # Representa percentual, frete, brinde ou bônus vinculado ao cupom.
├── DescontoAssinante.java  # Calcula o desconto de assinatura ativa como candidato não cumulativo.
├── DescontoCaixaMisteriosa.java  # Oferece 15% sobre o valor de cada caixa como desconto não cumulativo.
├── DescontoCentoSalgados.java  # Estratégia de desconto para cento de salgados.
├── DescontoCupomPercentual.java  # Estratégia percentual por cupom.
├── DescontoFidelidade.java  # Estratégia para clientes fiéis.
├── EstrategiaDesconto.java  # Contrato Strategy para descontos.
├── README.md  # Guia de navegação e explicações desta pasta.
└── ResultadoDesconto.java  # Retorna valor final, taxa de entrega, desconto escolhido e eventual brinde.
```

[🏠 Voltar ao README principal](../../../../../../../../README.md)
