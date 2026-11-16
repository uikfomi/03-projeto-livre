[🏠 Início](../../../../../../../../README.md) / [⬆️ service](../README.md) / 📂 desconto

# 🏷️ Estratégias de desconto

Agrupa os contratos, contextos e implementações Strategy para calcular descontos.

Mantenha as responsabilidades desta camada separadas das demais e consulte os arquivos relacionados antes de alterações.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [CalculadoraDescontos.java](CalculadoraDescontos.java) | Seleciona o maior desconto elegível. |
| [ContextoDesconto.java](ContextoDesconto.java) | Dados de entrada para estratégias de desconto. |
| [Cupom.java](Cupom.java) | Valor de domínio de cupom. |
| [DescontoCentoSalgados.java](DescontoCentoSalgados.java) | Estratégia de desconto para cento de salgados. |
| [DescontoCupomPercentual.java](DescontoCupomPercentual.java) | Estratégia percentual por cupom. |
| [DescontoFidelidade.java](DescontoFidelidade.java) | Estratégia para clientes fiéis. |
| [EstrategiaDesconto.java](EstrategiaDesconto.java) | Contrato Strategy para descontos. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [ResultadoDesconto.java](ResultadoDesconto.java) | Resumo do desconto e total calculados. |

## Árvore estrutural comentada

O ponto inicial é `src/main/java/com/salgaderia/app/service/desconto/`.

```text
src/main/java/com/salgaderia/app/service/desconto/  # Agrupa os contratos, contextos e implementações Strategy para calcular descontos.
├── CalculadoraDescontos.java  # Seleciona o maior desconto elegível.
├── ContextoDesconto.java  # Dados de entrada para estratégias de desconto.
├── Cupom.java  # Valor de domínio de cupom.
├── DescontoCentoSalgados.java  # Estratégia de desconto para cento de salgados.
├── DescontoCupomPercentual.java  # Estratégia percentual por cupom.
├── DescontoFidelidade.java  # Estratégia para clientes fiéis.
├── EstrategiaDesconto.java  # Contrato Strategy para descontos.
├── README.md  # Guia de navegação e explicações desta pasta.
└── ResultadoDesconto.java  # Resumo do desconto e total calculados.
```

[🏠 Voltar ao README principal](../../../../../../../../README.md)
