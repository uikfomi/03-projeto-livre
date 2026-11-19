[🏠 Início](../../../../../../../../README.md) / [⬆️ service](../README.md) / 📂 pix

# 🧪 Teste do adapter PIX

Verifica as requisições HTTP, os cabeçalhos de idempotência e a extração do código PIX em um servidor de loopback. Não precisa de token real nem acessa o Mercado Pago.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [MercadoPagoPixGatewayTest.java](MercadoPagoPixGatewayTest.java) | Testa o corpo da criação e a rota de consulta com respostas controladas. |
| [README.md](README.md) | Guia dos testes HTTP locais. |

## Árvore estrutural comentada

```text
src/test/java/com/salgaderia/app/service/pix/  # Contrato HTTP do adapter PIX.
├── MercadoPagoPixGatewayTest.java  # Verifica o contrato sem emitir cobranças.
└── README.md  # Guia dos testes HTTP locais.
```

Os efeitos persistidos do pagamento — liberação da cozinha, elegibilidade da roleta e devolução da reserva — são cobertos pelo [teste de integração](../ClubeGamificacaoIntegracaoTest.java).
