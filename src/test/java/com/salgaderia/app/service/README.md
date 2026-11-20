[🏠 Início](../../../../../../../README.md) / [⬆️ app](../README.md) / 📂 service

# 🧪 service — testes

Reúne testes automatizados dos componentes presentes neste pacote.

Execute os testes compatíveis com o ambiente; os testes de integração podem exigir banco ou display.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [desconto/](desconto/) | Estratégias de desconto e testes correspondentes. |
| [pix/](pix/) | Testes HTTP em loopback para a integração PIX. [Abrir guia](pix/README.md). |
| [ClubeGamificacaoIntegracaoTest.java](ClubeGamificacaoIntegracaoTest.java) | Valida clube, cupons e roleta concorrentes e estados do pagamento PIX no MySQL. |
| [ComandaIntegracaoTest.java](ComandaIntegracaoTest.java) | Valida operações de mesa, pagamentos concorrentes, baixa e fechamento no MySQL. |
| [CozinhaStateTest.java](CozinhaStateTest.java) | Verifica mensagens e progresso dos estados da cozinha. |
| [DescontoIntegracaoTest.java](DescontoIntegracaoTest.java) | Integração de descontos e pagamentos no MySQL. |
| [Etapa4IntegracaoTest.java](Etapa4IntegracaoTest.java) | Verifica transações de estoque, pedidos, PDV, avaliações com foto e financeiro no MySQL. |
| [FotoAvaliacaoStorageTest.java](FotoAvaliacaoStorageTest.java) | Testa a cópia validada, limites, caminhos locais e remoção de anexos. |
| [OperacaoLojaServiceTest.java](OperacaoLojaServiceTest.java) | Testa os limites do expediente e a combinação dos modos manual e automático. |
| [OperacaoV162IntegracaoTest.java](OperacaoV162IntegracaoTest.java) | Valida configuração persistida, agendamento, venda sob demanda, bloqueio e despacho no MySQL. |
| [PedidoAgendamentoTest.java](PedidoAgendamentoTest.java) | Verifica rejeição de agendamento passado. |
| [PersonalizacaoServiceTest.java](PersonalizacaoServiceTest.java) | Testa a composição do combo, sorteio com restrições e desconto não cumulativo. |
| [PixPayloadServiceTest.java](PixPayloadServiceTest.java) | Valida valor, referência e CRC16 e decodifica a imagem QR que foi gerada. |
| [README.md](README.md) | Guia de navegação e explicações desta pasta. |
| [RelatorioCsvTest.java](RelatorioCsvTest.java) | Verifica o balanço exportado em CSV. |
| [VendaTeste.java](VendaTeste.java) | Fornece relógio fixo e configuração isolada aos testes do fluxo de venda. |
| [VitrineV150IntegracaoTest.java](VitrineV150IntegracaoTest.java) | Verifica combos e caixas, restrições e cancelamento com devolução da baixa real. |
| [WhatsAppLinkServiceTest.java](WhatsAppLinkServiceTest.java) | Testa links, mensagens e configuração WhatsApp. |

## Árvore estrutural comentada

O ponto inicial é `src/test/java/com/salgaderia/app/service/`.

```text
src/test/java/com/salgaderia/app/service/  # Reúne testes automatizados dos componentes presentes neste pacote.
├── desconto/  # Estratégias de desconto e testes correspondentes.
│   └── EstrategiasDescontoTest.java  # Testa as estratégias e a escolha do melhor desconto.
├── pix/  # Testes HTTP em loopback para a integração PIX.
│   ├── MercadoPagoPixGatewayTest.java  # Verifica a integração HTTP com um servidor local, sem emitir cobranças.
│   └── README.md  # Guia dos testes HTTP locais.
├── ClubeGamificacaoIntegracaoTest.java  # Valida clube, cupons e roleta concorrentes e estados do pagamento PIX no MySQL.
├── ComandaIntegracaoTest.java  # Valida operações de mesa, pagamentos concorrentes, baixa e fechamento no MySQL.
├── CozinhaStateTest.java  # Verifica mensagens e progresso dos estados da cozinha.
├── DescontoIntegracaoTest.java  # Integração de descontos e pagamentos no MySQL.
├── Etapa4IntegracaoTest.java  # Verifica transações de estoque, pedidos, PDV, avaliações com foto e financeiro no MySQL.
├── FotoAvaliacaoStorageTest.java  # Testa a cópia validada, limites, caminhos locais e remoção de anexos.
├── OperacaoLojaServiceTest.java  # Testa os limites do expediente e a combinação dos modos manual e automático.
├── OperacaoV162IntegracaoTest.java  # Valida configuração persistida, agendamento, venda sob demanda, bloqueio e despacho no MySQL.
├── PedidoAgendamentoTest.java  # Verifica rejeição de agendamento passado.
├── PersonalizacaoServiceTest.java  # Testa a composição do combo, sorteio com restrições e desconto não cumulativo.
├── PixPayloadServiceTest.java  # Valida valor, referência e CRC16 e decodifica a imagem QR que foi gerada.
├── README.md  # Guia de navegação e explicações desta pasta.
├── RelatorioCsvTest.java  # Verifica o balanço exportado em CSV.
├── VendaTeste.java  # Fornece relógio fixo e configuração isolada aos testes do fluxo de venda.
├── VitrineV150IntegracaoTest.java  # Verifica combos e caixas, restrições e cancelamento com devolução da baixa real.
└── WhatsAppLinkServiceTest.java  # Testa links, mensagens e configuração WhatsApp.
```

[🏠 Voltar ao README principal](../../../../../../../README.md)
