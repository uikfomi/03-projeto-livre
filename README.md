# Ui K'Fomi — Salgaderia Pro

![Versão](https://img.shields.io/badge/vers%C3%A3o-v1.6.2-8b4513)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1)
[![Licença MIT](https://img.shields.io/badge/licen%C3%A7a-MIT-green)](LICENSE)

Projeto livre da **Ui K'fomi**, startup de Maranguape, Ceará, dedicada a soluções digitais para o setor de alimentação. Este repositório reúne o sistema integrado, incluindo os módulos de autenticação e agenda.

Sistema desktop para uma salgaderia, com login único, vitrine de pedidos, agenda de contatos e painel administrativo de vendas e estoque. O projeto adota **Java 17**, **JavaFX 21**, **MySQL** com **JDBC puro**, pool HikariCP e arquitetura **MVC + Service + DAO**, sem ORM.

## Índice

- [Funcionalidades](#funcionalidades)
- [Operação da loja](#operação-da-loja)
- [Combos, caixas e fotos](#combos-caixas-e-fotos)
- [Clube da Fome, conquistas e roleta](#clube-da-fome-conquistas-e-roleta)
- [Pagamento PIX](#pagamento-pix)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e execução](#instalação-e-execução)
- [Credenciais iniciais de teste](#credenciais-iniciais-de-teste)
- [Arquitetura e organização](#arquitetura-e-organização)
- [Organização do Projeto](#-organização-do-projeto)
- [Diagramas](#diagramas)
- [Testes](#testes)
- [Licença](#licença)

## Funcionalidades

- **Autenticação e cadastro público:** perfil `CLIENTE` ou `ADMIN`; o cadastro público cria um cliente na agenda. A tela inicial é escolhida polimorficamente conforme o perfil.
- **Agenda:** clientes e fornecedores, busca, filtros, cadastro, edição e exclusão. Contatos vinculados a outros registros podem ter a exclusão impedida pelo banco.
- **Vitrine do cliente:** catálogo com status **Disponível** ou **Indisponível no momento**, carrinho, combos, caixas misteriosas e galeria de fotos de avaliações. O checkout aceita entrega, retirada ou consumo no local, pagamento e agendamento opcional. Também é possível repetir o último pedido concluído mediante revalidação da disponibilidade de venda.
- **Termômetro da cozinha:** estados visuais de preparo e estimativa informativa baseada na quantidade de pedidos ativos; pedidos futuros indicam que aguardam o horário agendado.
- **Administração:** PDV com formas de pagamento e troco, fila de pedidos da vitrine, status e horário da loja, bloqueio rápido de produtos, estoque interno, entrada por fornecedor, perdas, indicadores financeiros, gráficos e moderação de avaliações.
- **Mesas e comandas:** dez mesas, lançamento de itens sem cliente, divisão igual do saldo e pagamentos parciais por valor ou item; a mesa é liberada quando a conta é quitada.
- **Clube da Fome:** planos mensais administráveis, vigência por mensalidade confirmada e desconto automático no checkout da vitrine.
- **Gamificação:** selos persistentes, roleta animada com giro extra e modo VIP, prêmios de uso único e aviso de expiração.
- **PIX:** cobrança Mercado Pago e confirmação automática para pedidos da vitrine; QR local e confirmação manual no PDV, nas comandas e nas mensalidades.
- **Estados vazios:** as telas não exibem cadastros fictícios; mostras de ausência de dados são distintas de falhas de conexão.
- **WhatsApp:** após finalizar e no histórico, o cliente pode abrir uma conversa com a loja pelo botão de acompanhamento. Na agenda e na fila, o administrador pode abrir uma conversa com o contato; ao mudar o status, a opção de notificação vem marcada. O WhatsApp abre com mensagem preenchida: o envio continua dependendo de confirmação no aplicativo.
- **Acompanhamento:** a lista “Meus Pedidos” e o indicador administrativo de pedidos pendentes consultam o banco a cada cinco segundos enquanto as respectivas telas estão abertas. Os alertas recentes são locais à sessão administrativa.
- **Balanço CSV:** o painel financeiro permite exportar o período exibido, em UTF-8 com BOM e separador `;`, incluindo faturamento bruto, descontos, custos, perdas e lucro.

### Regras importantes

- Pedidos da vitrine avançam de `PENDENTE` para `PREPARANDO` e `PRONTO`. Em **Entrega**, passam por `SAIU_ENTREGA` antes de `ENTREGUE`; **Retirada** e **Local** vão direto de `PRONTO` para `ENTREGUE`. O PDV registra `ENTREGUE` na própria transação de venda.
- Agendamentos são opcionais e devem estar no futuro e dentro do expediente de Maranguape (`America/Fortaleza`); não há despacho automático. O termômetro usa uma estimativa simples de cinco minutos por pedido ativo, não uma previsão garantida.
- Itens de comanda usam quantidades inteiras, respeitam o bloqueio de venda e continuam baixando estoque no lançamento. A divisão igual distribui os centavos restantes entre as primeiras pessoas; os pagamentos são registrados individualmente por valor ou item. A venda entra no financeiro ao quitar o saldo, sem nova baixa de estoque. Comanda vazia pode ser fechada sem gerar venda.
- **Descontos não cumulativos:** aplica-se a maior vantagem entre caixa misteriosa (15% sobre os itens da caixa), cupom ativo (`UIKFOMI10`: 10%; `FESTA15`: 15% a partir de R$ 80), fidelidade (5% para cliente com pelo menos três pedidos reais entregues), cento (12% sobre os itens elegíveis quando houver pelo menos 100 unidades de salgados), assinatura ativa na vitrine e prêmio resgatável da roleta. Para o cento, categorias com “salgad”, “frit” ou “assad” no nome são elegíveis, exceto nomes contendo “bebid” ou “doc”, sem distinção de caixa ou acentos. Produtos sem categoria não entram. Pedidos de PDV identificados por cliente também contam para a fidelidade.
- Cupons inválidos, inativos ou abaixo do mínimo bloqueiam a finalização até sua correção ou remoção. O sistema mostra a melhor vantagem no carrinho e revalida preços, elegibilidade e desconto ao finalizar; se o resultado mudar, é necessário conferir novamente.
- Finalização, itens, baixa física e movimentações compartilham uma transação JDBC. Na vitrine e no PDV, produtos liberados podem ser vendidos mesmo com saldo zero. Se houver saldo, a venda baixa apenas a quantidade física disponível; o cancelamento repõe somente a baixa registrada. Lotes, perdas e comandas continuam controlando o estoque físico.
- Produtos são criados com estoque zero. Use **Entrada de lote** para registrar unidades recebidas de um fornecedor ou altere explicitamente o estoque físico em **Editar produto** para gerar um ajuste. O saldo numérico permanece restrito à administração.
- Uma avaliação só é aceita quando há pedido `ENTREGUE` do cliente contendo o produto. O acesso aos controles administrativos é restrito ao perfil de administrador na interface.
- O financeiro considera pedidos entregues pela **data de criação**, incluindo a taxa de entrega e excluindo PIX de demonstração; perdas entram pela data da movimentação. O lucro é faturamento após descontos menos custo dos itens vendidos e perdas; **não inclui impostos, custo de entrega, custo de brindes ou outras despesas**. As categorias mostram receita de produtos, sem a taxa de entrega ou seu abatimento, e rateiam os demais descontos. O painel mostra o mês atual. Custos históricos desconhecidos não são estimados.
- O número padrão de WhatsApp da loja é `5585989095223` e pode ser substituído por `whatsapp.loja` em `database.local.properties`. A mensagem inclui a forma de pagamento escolhida; pedidos históricos sem essa informação permanecem como “não informado”. Em ambientes sem navegador, tenta-se copiar o link; caso a área de transferência também esteja indisponível, o link é mostrado para cópia manual.

### Operação da loja

- Em **Status da Loja**, o Admin pode editar abertura e fechamento (padrão `08:00–23:00`), ativar/desativar o horário automático e usar **Fechar Loja Agora / Abrir Loja Agora**. Com o automático ativo, a chave manual aberta permite pedidos imediatos somente dentro do expediente. Para abrir fora do horário, desative o automático. A abertura é inclusiva, o fechamento é exclusivo; `18:00–01:00` atravessa a meia-noite.
- O selo da vitrine consulta o estado a cada cinco segundos. Quando fechada, a loja aceita na vitrine somente encomendas futuras dentro do expediente configurado, mesmo em pausa manual ou com o automático desligado. O serviço revalida a regra no banco ao finalizar; a pausa não desativa o PDV interno.
- Em **Produtos e estoque**, selecione um produto e use **Bloquear / Liberar Venda**. Estoque zero não esgota o cardápio: produtos liberados são feitos sob demanda. Uma mudança é revalidada antes de finalizar, inclusive para itens que já estejam no carrinho. Para a foto, prefira **Selecionar imagem do computador**.
- Na **Fila de pedidos**, **Despachar entrega** solicita o nome do entregador. A lista **Meus Pedidos** passa a mostrar o banner de entrega em rota. Se a notificação em WhatsApp estiver ativada e houver telefone, o sistema abre uma mensagem preenchida com o nome do entregador; o operador confirma o envio no WhatsApp.

### Combos, caixas e fotos

1. **Configurar o catálogo:** no Painel Administrativo, abra **Produtos e estoque → Novo produto/Editar → Ficha de personalização da vitrine**. Cadastre cada combinação que realmente existe na cozinha como um produto (por exemplo, uma empada de frango com tipo **Empada** e recheio **Frango**). Cadastre molhos como **Molho / acompanhamento**. O combo usa variantes e acompanhamentos liberados no catálogo, sem criar itens fictícios.
2. **Montar um combo:** escolha a massa, um recheio disponível para ela e, se desejar, um acompanhamento. A descrição e a soma dos preços mudam conforme a seleção. A fila da cozinha recebe a personalização e os produtos reais.
3. **Sortear uma caixa:** escolha **Fome Leve** (10 unidades) ou **Fome de Leão** (25) e combine as restrições desejadas. O sorteio usa os salgados elegíveis liberados para venda, inclusive para produção sob demanda e mantém a composição em segredo no carrinho. O preço da caixa é calculado sobre o sorteio, com 15% de desconto; na finalização, um desconto de pedido ainda melhor pode substituí-lo, mas não acumula com ele. Repetir um pedido entregue preserva os produtos já sorteados e revalida disponibilidade, preço e restrições.
4. **Conferir as restrições:** só marque **Não contém** ou **Confirmado como assado** na ficha se a cozinha confirmar. Ficha com ingrediente não conferido não atende às exigências de exclusão. Se não houver nenhum produto elegível liberado, o sistema não oferece substituições fora dos filtros. A ficha não garante ausência de contato cruzado.
5. **Avaliar com foto:** em **Meus Pedidos**, abra um pedido entregue e avalie um produto. A imagem opcional (JPEG ou PNG, até 8 MB e 12 megapixels) passa por validação e é armazenada como PNG sem os metadados do arquivo original. Até 12 fotos recentes aparecem na galeria sem o nome do cliente; o Admin vê a miniatura na moderação e, ao excluir a avaliação, remove a foto da galeria e a cópia local. Veja [como guardar os arquivos](uploads/README.md).

### Clube da Fome, conquistas e roleta

- **Assinatura mensal:** escolha um plano (**Fome Semanal** e **Festa Mensal** são as opções iniciais), solicite a mensalidade e use a referência exibida para pagar. A vigência de um mês começa quando a loja confirma o recebimento. Renovar fica disponível nos sete dias anteriores ao vencimento; quando confirmada antes do vencimento, a renovação estende a vigência anterior. O cancelamento encerra o benefício imediatamente e cancela mensalidades pendentes; não gera reembolso automático.
- **Administração:** em **Clube da Fome**, cadastre, edite ou desative planos, consulte assinantes ativos e confirme mensalidades após conferir a referência e o recebimento no banco. O valor e o desconto de uma mensalidade já solicitada não mudam ao editar o plano. As confirmações ficam registradas à parte e não entram no balanço de vendas. Não há débito recorrente automático.
- **Selos:** **Primeiro Pedido** é concedido na finalização, **Rei do Cento** após receber um pedido com 100 salgados elegíveis e **Cliente Fiel** após a terceira entrega. O barramento Observer grava as conquistas na mesma transação do pedido, sem duplicar selos.
- **Roleta:** pagamentos PIX confirmados liberam a roleta; pedidos com pagamento na entrega liberam ao marcar `ENTREGUE`. O limite é **um pedido por conta a cada 24 horas**, inclusive se cair “Não foi dessa vez”. Um giro extra fica no mesmo pedido e não pode gerar outro giro extra. A roleta regular oferece mini porção surpresa, frete grátis, pontos em dobro, 10% OFF, giro extra e uma fatia sem prêmio. Acima de **R$ 60,00 no total final**, a roleta VIP oferece também combo brinde e 15% OFF, no lugar da fatia sem prêmio e do cupom de 10%.
- **Resgate:** cada cupom é exclusivo da conta, exige **R$ 25,00 em produtos antes do desconto** e vence **cinco dias corridos** após o primeiro giro. A vitrine mostra a contagem regressiva quando faltam menos de 24 horas. O cupom é reservado somente quando seu benefício é escolhido. Cancelar o pedido de resgate devolve o cupom ainda válido; cancelar o pedido premiado revoga o prêmio não utilizado.
- **Benefícios:** frete grátis abate apenas a taxa de entrega. A taxa padrão é **R$ 5,00** para novos pedidos com entrega e pode ser alterada com `entrega.taxa` (`0.00` desativa a tarifa). A cozinha vê o brinde como observação para separar a cortesia. Para escolher a maior vantagem, o sistema compara a mini porção e o combo com valores indicativos de R$ 4 e R$ 8; eles não reduzem o total a pagar. Pontos em dobro não vencem um desconto em dinheiro: o pedido elegível acumula 10 pontos, ou 20 se o prêmio for selecionado. Os pontos não são dinheiro.

### Pagamento PIX

**Na vitrine:** configure `mercadopago.access.token` somente em `database.local.properties` e reinicie o app. Ao selecionar PIX, informe o e-mail e o CPF/CNPJ do pagador. Finalize para gerar a cobrança com o valor final. O app mostra o código oficial retornado pela API e consulta o status a cada **três segundos** enquanto o diálogo estiver aberto. Em **Meus Pedidos**, use **Ver PIX do pedido** para reabrir a mesma cobrança. O painel administrativo aberto também concilia transações conhecidas. O saldo físico baixado fica reservado e a cozinha só recebe o pedido após a aprovação. O fluxo segue a [documentação oficial de PIX da API de Pagamentos do Mercado Pago](https://www.mercadopago.com.br/developers/pt/docs/checkout-api-payments/integration-configuration/integrate-pix).

**Segurança e retomada:** o pedido usa a mesma referência e chave de idempotência em toda repetição da criação. Após vincular o ID remoto, o app só consulta essa transação, validando titularidade local, referência, valor, moeda e método antes de marcar `PAGO`. Os dados do pagador são removidos do pedido ao vincular o ID ou cancelar. Uma transação rejeitada ou cancelada pelo provedor devolve a reserva. Se o envio ficar sem resposta por 20 minutos e não houver ID conhecido, confira a referência no painel Mercado Pago antes de nova cobrança. O app não estorna PIX nem usa um webhook; se ambos os painéis estiverem fechados, a confirmação aguarda a retomada da consulta.

**Demonstração:** sem token, o checkout gera um BR Code não pagável e oferece **Confirmar Pagamento (Modo Offline)** apenas para pedidos de demonstração. A simulação é identificada no Admin e não gera receita, pontos nem cupons resgatáveis. Se a conexão cair com token configurado, o app preserva o pedido real; se ainda não tiver o QR oficial, mostra apenas uma prévia de demonstração, sem confirmação offline.

**No PDV, nas comandas e nas mensalidades:** continua o BR Code local com valor, configurado por `pix.chave`, `pix.beneficiario` e `pix.cidade`. O operador confere o recebimento no banco antes de registrar. Sem `pix.chave`, é demonstração (`uikfomi.demonstracao@example.invalid`). Na comanda é possível pagar o saldo, uma parcela digitada ou as unidades selecionadas. Não há débito recorrente automático.

## Pré-requisitos

- JDK **17 ou superior** e Maven instalados, com `java`, `javac` e `mvn` disponíveis no terminal.
- MySQL Server **8.0.16 ou superior** e cliente `mysql`; os scripts utilizam `CHECK` e recursos do MySQL 8. A aplicação usa `mysql-connector-j` 8.0.33.
- Ambiente gráfico compatível com JavaFX. A primeira execução Maven pode precisar de acesso à internet para baixar dependências.

Confira as ferramentas:

```bash
java -version
mvn -version
mysql --version
```

## Instalação e execução

Execute os comandos na raiz do repositório. Os exemplos usam um usuário administrativo do MySQL chamado `root`; ele é **diferente** do usuário `root` da aplicação.

### 1. Criar banco e aplicar migrações

Em uma instalação nova, com uma conta MySQL que possa criar o banco e alterar tabelas:

```bash
mysql -u root -p < database/scripts/schema.sql
mysql -u root -p < database/scripts/etapa4_migracao.sql
mysql -u root -p < database/scripts/etapa8_pdv.sql
mysql -u root -p < database/scripts/etapa9_descontos.sql
mysql -u root -p < database/scripts/etapa10_pagamento_vitrine.sql
mysql -u root -p < database/scripts/etapa11_salao_agendamento.sql
mysql -u root -p < database/scripts/etapa12_combo_avaliacoes.sql
mysql -u root -p < database/scripts/etapa13_clube_gamificacao.sql
mysql -u root -p < database/scripts/etapa14_pix_roleta_vip.sql
mysql -u root -p < database/scripts/etapa15_operacao_loja.sql
```

**Execute as migrações na ordem e somente uma vez.** Embora o schema inicial use `IF NOT EXISTS`, os scripts de migração usam `ALTER TABLE` não idempotente. Antes de atualizar um banco existente, faça backup, confira quais migrações já foram aplicadas e revise dados legados; DDL no MySQL pode causar commit implícito. Usuários clientes antigos sem contato vinculado e fornecedores sem os campos adicionais precisam de saneamento manual. Consulte [as notas de persistência](docs/etapa4-persistencia.md).

### 2. Criar uma conta local para a aplicação

No cliente MySQL, com uma conta que tenha privilégio de criar usuários e conceder permissões, por exemplo:

```sql
CREATE USER 'salgaderia'@'localhost' IDENTIFIED BY '123456';
GRANT SELECT, INSERT, UPDATE, DELETE ON salgaderia_db.* TO 'salgaderia'@'localhost';
```

Se a conta já existir, não execute `CREATE USER` novamente: ajuste a configuração e os privilégios conforme o ambiente. As credenciais acima são **somente um exemplo local de desenvolvimento**.

### 3. Configurar a conexão

Copie o template versionado e edite a cópia:

```bash
cp src/main/resources/database.properties src/main/resources/database.local.properties
```

Exemplo de `src/main/resources/database.local.properties`:

```properties
url=jdbc:mysql://localhost:3306/salgaderia_db
user=salgaderia
password=123456
whatsapp.loja=5585989095223
# Opcionais; sem chave real, o PIX permanece em demonstração
# pix.chave=
pix.beneficiario=UI KFOMI SALGADERIA
pix.cidade=MARANGUAPE
entrega.taxa=5.00
# Ativa PIX real via Mercado Pago na vitrine; mantenha a credencial fora do Git
# mercadopago.access.token=
```

`ConnectionFactory` procura `database.local.properties` no **classpath** e, se ele não existir, usa `database.properties`. O arquivo local é ignorado pelo Git. O Maven copia recursos para `target/classes`; portanto, a configuração local pode também entrar em artefatos empacotados: **não distribua um build contendo credenciais**. Revise a configuração de recursos antes de qualquer distribuição.

### 4. Compilar e iniciar

```bash
mvn clean test-compile
mvn javafx:run
```

A aplicação abre na tela única de login. Em caso de falha, verifique se o servidor MySQL está ativo, se o schema e todas as migrações foram aplicados e se a conta tem acesso ao banco. Para renovar os recursos copiados ao `target/classes` após editar a configuração, repita a compilação.

## Credenciais iniciais de teste

| Login da aplicação | Senha inicial | Perfil | Destino após login |
|---|---|---|---|
| `root` | `toor` | `CLIENTE` | Vitrine de pedidos |
| `toor` | `root` | `ADMIN` | Seleção de módulos |

As credenciais só correspondem às **sementes iniciais**, desde que ainda não tenham sido alteradas no banco. O schema as insere inicialmente em texto puro para desenvolvimento; após um login bem-sucedido, `AutenticacaoServiceImpl.login()` converte a senha legada para PBKDF2. Novos cadastros usam PBKDF2. Não use essas contas ou senhas em produção; reexecutar o schema **não redefine** as senhas existentes.

> **Segurança:** este é um projeto de desenvolvimento/estudo. A interface verifica o perfil e algumas operações consultam o administrador no banco, mas os serviços ainda recebem objetos de usuário fornecidos pelo chamador. É necessária uma sessão autenticada não falsificável e uma revisão de autorização antes de expor o sistema a clientes ou APIs não confiáveis.

## Arquitetura e organização

```text
src/main/java/com/salgaderia/app/
  Main.java               inicialização JavaFX
  model/                  entidades, enums e regras de domínio
  controller/             controladores das telas FXML
  service/ e service/impl/ contratos e regras de aplicação
  dao/ e dao/impl/        contratos e implementações JDBC
  exception/              exceções de domínio e persistência
  util/                   conexão, transações, senhas e navegação
src/main/resources/       FXML, CSS e templates de configuração
database/scripts/         schema e migrações SQL
database/DER/ e DL/       modelos de dados
docs/uml/                 diagrama de classes
```

Os controllers coordenam a interface; os services orquestram regras de negócio; DAOs executam SQL com `PreparedStatement` e `try-with-resources`. `ConnectionFactory.executarEmTransacao()` abre uma conexão do HikariCP, desativa auto-commit, confirma em caso de sucesso e reverte em caso de falha.

### Mapeamento de conceitos de orientação a objetos

A tabela apresenta pontos concretos da implementação; métodos herdados e regras adicionais podem existir fora dos exemplos indicados.

| Conceito | Classes e métodos exatos | Aplicação |
|---|---|---|
| Builder | `service.ComboBuilder.massa()`, `recheio()`, `acompanhamento()` e `montar()` | Monta a receita em etapas com variantes existentes, mantendo disponibilidade e preços do catálogo. |
| Observer | `service.BarramentoPedido.publicar()` e `service.PedidoConcluidoObserver.aoAlterarPedido()` | Propaga eventos transacionais do pedido; `ConquistasObserver` registra selos, liberação da roleta e revogação de cupons. |
| Adapter | `service.pix.PixGatewayService`, `MercadoPagoPixGateway` e `LocalDemoPixGateway` | A vitrine consulta um serviço de pagamento comum; o adapter traduz sua solicitação para HTTP e JSON ou gera uma demonstração sem cobrança. |
| Abstração | `model.Usuario.getTelaInicialFxml()` e `model.Usuario.podeAcessarPainelGerencial()`; `model.Contato.getResumoContato()` | Métodos abstratos definem comportamentos comuns sem implementar as variações dos subtipos. |
| Encapsulamento | `model.Produto.setPrecoVenda()`, `setCustoProducao()`, `setEstoqueAtual()`, `setEstoqueMinimo()`, `adicionarEstoque()` e `removerEstoque()` | Atributos privados e validações controlam valores monetários e quantidades de estoque. |
| Encapsulamento | `model.Pedido.getItens()`, `adicionarItem()`, `removerItem()` e `transicionarPara()`; `model.PedidoItem` e `calcularSubtotal()` | A lista retornada é uma cópia não modificável; o pedido controla alterações de itens e transições. Os campos de `PedidoItem` são finais. |
| Encapsulamento | `model.Avaliacao.setNota()`; `model.Fornecedor.setCnpj()` e `setPrazoEntregaDias()` | A nota deve estar entre 1 e 5; o CNPJ deve conter 14 dígitos após remoção da formatação e o prazo não pode ser negativo. A validação do CNPJ não calcula dígitos verificadores. |
| Herança | Construtores `model.UsuarioCliente(...)` e `model.UsuarioAdmin(...)`; `model.Cliente(...)` e `model.Fornecedor(...)` | `UsuarioCliente` e `UsuarioAdmin` estendem `Usuario`; `Cliente` e `Fornecedor` estendem `Contato`. |
| Polimorfismo | `model.UsuarioCliente.getTelaInicialFxml()` / `model.UsuarioAdmin.getTelaInicialFxml()`; `util.Navegacao.abrirTelaInicial()` | A navegação chama o método da referência `Usuario`, que retorna a tela específica do subtipo. |
| Polimorfismo | `model.Cliente.getResumoContato()` e `model.Fornecedor.getResumoContato()`; `model.enums.FormaPagamento.isPermiteTroco()` | Os subtipos formatam resumos diferentes; cada constante de pagamento implementa o método, permitindo troco apenas em `DINHEIRO`. |
| Interfaces | `service.PedidoVendaService.finalizar()` e `finalizarPdv()` implementados por `service.impl.PedidoVendaServiceImpl`; `service.AgendaService.buscar()` implementado por `service.impl.AgendaServiceImpl` | As interfaces definem contratos de aplicação independentes das implementações. |
| Interfaces | `dao.ContatoDAO.buscar()` implementado por `dao.impl.ContatoDAOImpl`; `dao.PedidoDAO.inserir()` implementado por `dao.impl.PedidoDAOImpl`; `util.ConnectionFactory.OperacaoTransacional.executar()` | Contratos DAO separam persistência da orquestração; a interface funcional recebe operações executadas em transação. |
| Exceções personalizadas | `model.Produto.exigirDisponivelParaVenda()` lança `exception.ProdutoIndisponivelException`; `removerEstoque()` lança `exception.EstoqueInsuficienteException` | Distingue o bloqueio de venda da falta de saldo para movimentações físicas. |
| Exceções personalizadas | `service.impl.AutenticacaoServiceImpl.login()` lança `exception.AutenticacaoInvalidaException`; `controller.LoginController.executar()` a trata | Credenciais inválidas ou conta inativa resultam em mensagem amigável. |
| Exceções personalizadas | `service.impl.AvaliacaoServiceImpl.salvar()` lança `exception.AvaliacaoNaoPermitidaException`; `controller.ClienteVitrineController.avaliar()` a trata | Uma avaliação é negada sem pedido entregue elegível. |
| Exceções personalizadas | `util.ConnectionFactory.executarEmTransacao()` encapsula `SQLException` em `exception.PersistenciaException` | Erros SQL transacionais são propagados com a causa original após tentativa de rollback. |

Os prefixos da tabela são relativos a `com.salgaderia.app`.

## 📂 Organização do Projeto

A árvore mostra todos os arquivos versionados e os guias desta documentação. Credenciais locais, arquivos ignorados e artefatos de compilação não fazem parte do inventário.

### Guias navegáveis

| Diretório | Guia |
|---|---|
| `database/` | [Abrir README](database/README.md) |
| `database/scripts/` | [Abrir README](database/scripts/README.md) |
| `docs/` | [Abrir README](docs/README.md) |
| `docs/ui-ux/` | [Abrir README](docs/ui-ux/README.md) |
| `resources/` | [Abrir README](resources/README.md) |
| `src/` | [Abrir README](src/README.md) |
| `src/main/` | [Abrir README](src/main/README.md) |
| `src/main/java/` | [Abrir README](src/main/java/README.md) |
| `src/main/java/com/` | [Abrir README](src/main/java/com/README.md) |
| `src/main/java/com/salgaderia/` | [Abrir README](src/main/java/com/salgaderia/README.md) |
| `src/main/java/com/salgaderia/app/` | [Abrir README](src/main/java/com/salgaderia/app/README.md) |
| `src/main/java/com/salgaderia/app/controller/` | [Abrir README](src/main/java/com/salgaderia/app/controller/README.md) |
| `src/main/java/com/salgaderia/app/dao/` | [Abrir README](src/main/java/com/salgaderia/app/dao/README.md) |
| `src/main/java/com/salgaderia/app/dao/impl/` | [Abrir README](src/main/java/com/salgaderia/app/dao/impl/README.md) |
| `src/main/java/com/salgaderia/app/exception/` | [Abrir README](src/main/java/com/salgaderia/app/exception/README.md) |
| `src/main/java/com/salgaderia/app/model/` | [Abrir README](src/main/java/com/salgaderia/app/model/README.md) |
| `src/main/java/com/salgaderia/app/model/comanda/` | [Abrir README](src/main/java/com/salgaderia/app/model/comanda/README.md) |
| `src/main/java/com/salgaderia/app/model/enums/` | [Abrir README](src/main/java/com/salgaderia/app/model/enums/README.md) |
| `src/main/java/com/salgaderia/app/service/` | [Abrir README](src/main/java/com/salgaderia/app/service/README.md) |
| `src/main/java/com/salgaderia/app/service/cozinha/` | [Abrir README](src/main/java/com/salgaderia/app/service/cozinha/README.md) |
| `src/main/java/com/salgaderia/app/service/desconto/` | [Abrir README](src/main/java/com/salgaderia/app/service/desconto/README.md) |
| `src/main/java/com/salgaderia/app/service/impl/` | [Abrir README](src/main/java/com/salgaderia/app/service/impl/README.md) |
| `src/main/java/com/salgaderia/app/service/pix/` | [Abrir README](src/main/java/com/salgaderia/app/service/pix/README.md) |
| `src/main/java/com/salgaderia/app/util/` | [Abrir README](src/main/java/com/salgaderia/app/util/README.md) |
| `src/main/resources/` | [Abrir README](src/main/resources/README.md) |
| `src/main/resources/css/` | [Abrir README](src/main/resources/css/README.md) |
| `src/main/resources/fxml/` | [Abrir README](src/main/resources/fxml/README.md) |
| `src/test/` | [Abrir README](src/test/README.md) |
| `src/test/java/` | [Abrir README](src/test/java/README.md) |
| `src/test/java/com/` | [Abrir README](src/test/java/com/README.md) |
| `src/test/java/com/salgaderia/` | [Abrir README](src/test/java/com/salgaderia/README.md) |
| `src/test/java/com/salgaderia/app/` | [Abrir README](src/test/java/com/salgaderia/app/README.md) |
| `src/test/java/com/salgaderia/app/controller/` | [Abrir README](src/test/java/com/salgaderia/app/controller/README.md) |
| `src/test/java/com/salgaderia/app/service/` | [Abrir README](src/test/java/com/salgaderia/app/service/README.md) |
| `src/test/java/com/salgaderia/app/service/pix/` | [Abrir README](src/test/java/com/salgaderia/app/service/pix/README.md) |
| `support/` | [Abrir README](support/README.md) |
| `uploads/` | [Abrir README](uploads/README.md) |

### Árvore completa comentada

Os comentários descrevem a responsabilidade de cada item.

```text
03-ui-kfomi/  # Raiz do repositório Ui K'Fomi
├── database/  # Modelos, schema e migrações para o MySQL.
│   ├── DER/  # Modelo entidade-relacionamento e seus materiais.
│   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   └── modelo-entidade-relacionamento.md  # Diagrama entidade-relacionamento Mermaid.
│   ├── DL/  # Modelo lógico das tabelas e relações.
│   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   └── modelo-logico.md  # Modelo lógico do banco.
│   ├── scripts/  # Schema e migrações SQL ordenadas pela evolução do sistema.
│   │   ├── etapa10_pagamento_vitrine.sql  # Amplia forma de pagamento para cartão genérico.
│   │   ├── etapa11_salao_agendamento.sql  # Migração de agendamento, mesas, comandas e pagamentos parciais.
│   │   ├── etapa12_combo_avaliacoes.sql  # Acrescenta a ficha do catálogo, personalização dos itens e caminhos das fotos de avaliações.
│   │   ├── etapa13_clube_gamificacao.sql  # Cria planos, assinaturas, mensalidades, conquistas e histórico da roleta; habilita cupons exclusivos.
│   │   ├── etapa14_pix_roleta_vip.sql  # Migra estados de pagamento, referências PIX, cupons com validade e prêmios VIP.
│   │   ├── etapa15_operacao_loja.sql  # Adiciona expediente da loja, bloqueio de venda, baixa física e saída para entrega.
│   │   ├── etapa4_migracao.sql  # Migração de contatos, usuários e custos históricos.
│   │   ├── etapa8_pdv.sql  # Colunas de origem e pagamento do PDV.
│   │   ├── etapa9_descontos.sql  # Cupons e registro de descontos por pedido.
│   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── schema.sql  # Schema inicial MySQL usado pelo respectivo repositório.
│   └── README.md  # Guia de navegação e explicações desta pasta.
├── docs/  # Documentação, diagramas, interfaces e apresentações.
│   ├── diagrams/  # Espaço para diagramas adicionais.
│   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── presentations/  # Espaço reservado para slides e apresentações.
│   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── ui-ux/  # Artefatos de experiência do usuário.
│   │   ├── mockups/  # Espaço reservado para mockups.
│   │   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── prototypes/  # Espaço reservado para protótipos.
│   │   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   ├── wireframes/  # Espaço reservado para wireframes.
│   │   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   ├── uml/  # Materiais para diagramas de classes e modelagem UML.
│   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   └── diagrama-de-classes.md  # Diagrama UML de classes Mermaid.
│   ├── etapa4-persistencia.md  # Notas de persistência e migração.
│   └── README.md  # Guia de navegação e explicações desta pasta.
├── resources/  # Recursos institucionais ou recursos JavaFX, conforme a localização.
│   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   └── README.md  # Guia de navegação e explicações desta pasta.
├── src/  # Fontes, recursos de execução e testes do projeto.
│   ├── main/  # Aplicação principal e seus recursos.
│   │   ├── java/  # Pacotes de código Java.
│   │   │   ├── com/  # Raiz de pacotes Java.
│   │   │   │   ├── salgaderia/  # Pacote da aplicação Salgaderia.
│   │   │   │   │   ├── app/  # Componentes da aplicação e suas camadas.
│   │   │   │   │   │   ├── controller/  # Controllers das telas e testes de interface, conforme a localização.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AdminDashboardController.java  # Coordena PDV, fila e despacho, expediente, catálogo e demais operações gerenciais.
│   │   │   │   │   │   │   ├── AgendaController.java  # Interface da agenda de clientes e fornecedores.
│   │   │   │   │   │   │   ├── ClienteVitrineController.java  # Coordena catálogo, carrinho, expediente e acompanhamento do pedido em rota.
│   │   │   │   │   │   │   ├── LoginController.java  # Tela de login e cadastro público.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   └── SelecaoModulosController.java  # Seleção de módulos administrativos.
│   │   │   │   │   │   ├── dao/  # Contratos de acesso a dados ou testes JDBC.
│   │   │   │   │   │   │   ├── impl/  # Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   │   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   │   ├── AvaliacaoDAOImpl.java  # Persiste avaliações e fotos e consulta a galeria e dados de moderação.
│   │   │   │   │   │   │   │   ├── CategoriaDAOImpl.java  # Operações JDBC de cadastro e consulta de categorias.
│   │   │   │   │   │   │   │   ├── ClubeDAOImpl.java  # Executa consultas e atualizações JDBC para adesões, vigência e mensalidades.
│   │   │   │   │   │   │   │   ├── ComandaDAOImpl.java  # Implementa operações JDBC de mesas e comandas com bloqueio de linha.
│   │   │   │   │   │   │   │   ├── ConfiguracaoLojaDAOImpl.java  # Persiste expediente e estado manual separadamente com JDBC.
│   │   │   │   │   │   │   │   ├── ContatoDAOImpl.java  # Operações JDBC de contatos com filtros de clientes e fornecedores.
│   │   │   │   │   │   │   │   ├── CupomDAOImpl.java  # Consulta elegibilidade, reserva cupons exclusivos e os devolve no cancelamento.
│   │   │   │   │   │   │   │   ├── GamificacaoDAOImpl.java  # Persiste selos, giros, resgates e trava de 24 horas usando o relógio do banco.
│   │   │   │   │   │   │   │   ├── MovimentacaoEstoqueDAOImpl.java  # Grava movimentações de estoque e consulta perdas por período via JDBC.
│   │   │   │   │   │   │   │   ├── PedidoDAOImpl.java  # Persiste pedidos, baixa física e despacho; consulta fila, histórico e relatórios.
│   │   │   │   │   │   │   │   ├── PixPagamentoDAOImpl.java  # Consulta pagamentos e vincula tentativas, IDs remotos e aprovação ao pedido em JDBC.
│   │   │   │   │   │   │   │   ├── ProdutoDAOImpl.java  # Persiste catálogo, disponibilidade de venda, estoque e fichas de personalização.
│   │   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   │   └── UsuarioDAOImpl.java  # Executa consultas e alterações de usuário e valida a conta ativa sob bloqueio.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AvaliacaoDAO.java  # Contrato JDBC para avaliações, médias, galeria de fotos e moderação.
│   │   │   │   │   │   │   ├── CategoriaDAO.java  # Contrato para cadastrar, atualizar, buscar, listar e excluir categorias.
│   │   │   │   │   │   │   ├── ClubeDAO.java  # Contrato de persistência de planos, assinaturas e mensalidades.
│   │   │   │   │   │   │   ├── ComandaDAO.java  # Contrato JDBC para mesas, itens, pagamentos e fechamento transacional.
│   │   │   │   │   │   │   ├── ConfiguracaoLojaDAO.java  # Define leitura e gravação da configuração operacional única da loja.
│   │   │   │   │   │   │   ├── ContatoDAO.java  # Contrato para cadastro, edição, exclusão e busca filtrada de contatos.
│   │   │   │   │   │   │   ├── CupomDAO.java  # Contrato de consulta, reserva e devolução de cupons com uso limitado.
│   │   │   │   │   │   │   ├── GamificacaoDAO.java  # Contrato para selos, elegibilidade por pagamento, prêmios, prazos e pontos.
│   │   │   │   │   │   │   ├── MovimentacaoEstoqueDAO.java  # Contrato para registrar movimentações e calcular perdas por período.
│   │   │   │   │   │   │   ├── PedidoDAO.java  # Define operações transacionais de pedidos, despacho, relatórios e baixa física.
│   │   │   │   │   │   │   ├── PixPagamentoDAO.java  # Contrato de persistência de cobranças, conciliação e status de pagamento.
│   │   │   │   │   │   │   ├── ProdutoDAO.java  # Define cadastro, consulta, bloqueio de venda e atualização de estoque físico.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   └── UsuarioDAO.java  # Contrato de acesso a usuários, credenciais e bloqueio transacional da conta do cliente.
│   │   │   │   │   │   ├── exception/  # Exceções personalizadas de domínio e persistência.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AutenticacaoInvalidaException.java  # Falha de autenticação.
│   │   │   │   │   │   │   ├── AvaliacaoNaoPermitidaException.java  # Avaliação sem compra elegível.
│   │   │   │   │   │   │   ├── EstoqueInsuficienteException.java  # Indica saldo físico insuficiente para uma movimentação de estoque.
│   │   │   │   │   │   │   ├── PersistenciaException.java  # Encapsula erro de persistência.
│   │   │   │   │   │   │   ├── ProdutoIndisponivelException.java  # Indica que um produto foi bloqueado para venda, independentemente do estoque.
│   │   │   │   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── model/  # Entidades de domínio ou testes dessas entidades.
│   │   │   │   │   │   │   ├── comanda/  # Entidades de mesa, comanda, itens e pagamentos parciais.
│   │   │   │   │   │   │   │   ├── Comanda.java  # Sessão da mesa e cálculo monetário de total, pagamentos e saldo.
│   │   │   │   │   │   │   │   ├── ItemComanda.java  # Item lançado na comanda, com quantidade disponível e cotação por unidade.
│   │   │   │   │   │   │   │   ├── Mesa.java  # Identificação, número e status da mesa.
│   │   │   │   │   │   │   │   ├── PagamentoComanda.java  # Dados de pagamento parcial por valor ou item.
│   │   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   │   └── StatusMesa.java  # Estados livre e ocupada e suas permissões.
│   │   │   │   │   │   │   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   │   ├── CategoriaInsumo.java  # Categorias de insumos de fornecedores.
│   │   │   │   │   │   │   │   ├── FormaPagamento.java  # Formas de pagamento e regra de troco.
│   │   │   │   │   │   │   │   ├── PerfilUsuario.java  # Perfis de acesso do usuário.
│   │   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   │   ├── StatusPagamento.java  # Distingue PIX pendente, pagamento confirmado e cobrança na entrega.
│   │   │   │   │   │   │   │   ├── StatusPedido.java  # Define as transições por modalidade, incluindo saída exclusiva para entrega.
│   │   │   │   │   │   │   │   ├── TipoEntrega.java  # Modalidades de entrega.
│   │   │   │   │   │   │   │   └── TipoMovimentacaoEstoque.java  # Tipos de movimentação de estoque.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AssinaturaCliente.java  # Representa o plano do cliente, o desconto vigente e o estado da assinatura.
│   │   │   │   │   │   │   ├── Avaliacao.java  # Registra nota, comentário, resposta oficial e referência opcional da foto.
│   │   │   │   │   │   │   ├── AvaliacaoFoto.java  # Projeção pública de fotos de avaliações que não expõe dados da conta.
│   │   │   │   │   │   │   ├── AvaliacaoResumo.java  # Projeção de avaliação com comentário, resposta oficial e foto para moderação.
│   │   │   │   │   │   │   ├── Categoria.java  # Categoria de produtos.
│   │   │   │   │   │   │   ├── Cliente.java  # Contato especializado em cliente.
│   │   │   │   │   │   │   ├── ConfiguracaoLoja.java  # Representa o expediente, o modo automático e a chave manual da loja.
│   │   │   │   │   │   │   ├── ConquistaCliente.java  # Representa um selo e a data em que foi desbloqueado.
│   │   │   │   │   │   │   ├── Contato.java  # Abstração comum de contatos.
│   │   │   │   │   │   │   ├── CupomRoleta.java  # Projeção de código de resgate com benefício, mínimo e data de expiração.
│   │   │   │   │   │   │   ├── FichaVitrine.java  # Dados confirmados no catálogo: tipo de combo, recheio, ingredientes e preparo assado.
│   │   │   │   │   │   │   ├── Fornecedor.java  # Contato especializado em fornecedor.
│   │   │   │   │   │   │   ├── MensalidadeClube.java  # Cotação imutável de valor e desconto de uma mensalidade pendente.
│   │   │   │   │   │   │   ├── MovimentacaoEstoque.java  # Registro de entrada ou saída de estoque.
│   │   │   │   │   │   │   ├── Pedido.java  # Mantém itens, total, frete, pagamento, status e liberação da roleta.
│   │   │   │   │   │   │   ├── PedidoFila.java  # Projeção de preparo com pagamento, modalidade, itens, entrega e agendamento.
│   │   │   │   │   │   │   ├── PedidoItem.java  # Registra produto, quantidade, preço histórico e eventual personalização do item.
│   │   │   │   │   │   │   ├── PersonalizacaoItem.java  # Identifica grupos de combo ou caixa, tamanho e restrições de ingredientes.
│   │   │   │   │   │   │   ├── PlanoAssinatura.java  # Valida nome, descrição, preço mensal e percentual do plano.
│   │   │   │   │   │   │   ├── PremioRoleta.java  # Define fatias comuns/VIP e registra resultado, prazo e giro extra.
│   │   │   │   │   │   │   ├── Produto.java  # Representa preço, custos, saldo físico, disponibilidade de venda e ficha do catálogo.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── Usuario.java  # Abstração de usuário e permissões.
│   │   │   │   │   │   │   ├── UsuarioAdmin.java  # Usuário administrativo.
│   │   │   │   │   │   │   ├── UsuarioCliente.java  # Usuário vinculado a um cliente.
│   │   │   │   │   │   │   └── Validacao.java  # Validações compartilhadas das entidades.
│   │   │   │   │   │   ├── service/  # Contratos, regras de negócio e testes de serviços.
│   │   │   │   │   │   │   ├── cozinha/  # Estados do termômetro visual de preparo dos pedidos.
│   │   │   │   │   │   │   │   ├── EstadoCozinha.java  # Contrato State de mensagem, progresso e espera.
│   │   │   │   │   │   │   │   ├── EtapaCozinha.java  # Mapeia os status aos estados visuais de preparo, saída em rota e conclusão.
│   │   │   │   │   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   │   │   │   │   │   │   ├── CalculadoraDescontos.java  # Escolhe uma única vantagem entre estratégias de desconto, frete, brindes e pontos.
│   │   │   │   │   │   │   │   ├── ContextoDesconto.java  # Agrupa itens, taxa de entrega, fidelidade, cupom e benefício do assinante.
│   │   │   │   │   │   │   │   ├── Cupom.java  # Representa percentual, frete, brinde ou bônus vinculado ao cupom.
│   │   │   │   │   │   │   │   ├── DescontoAssinante.java  # Calcula o desconto de assinatura ativa como candidato não cumulativo.
│   │   │   │   │   │   │   │   ├── DescontoCaixaMisteriosa.java  # Oferece 15% sobre o valor de cada caixa como desconto não cumulativo.
│   │   │   │   │   │   │   │   ├── DescontoCentoSalgados.java  # Estratégia de desconto para cento de salgados.
│   │   │   │   │   │   │   │   ├── DescontoCupomPercentual.java  # Estratégia percentual por cupom.
│   │   │   │   │   │   │   │   ├── DescontoFidelidade.java  # Estratégia para clientes fiéis.
│   │   │   │   │   │   │   │   ├── EstrategiaDesconto.java  # Contrato Strategy para descontos.
│   │   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   │   └── ResultadoDesconto.java  # Retorna valor final, taxa de entrega, desconto escolhido e eventual brinde.
│   │   │   │   │   │   │   ├── impl/  # Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   │   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   │   ├── AgendaServiceImpl.java  # Valida e coordena cadastro, busca e alterações de contatos pela camada DAO.
│   │   │   │   │   │   │   │   ├── AutenticacaoServiceImpl.java  # Orquestra login, verificação de senha e cadastro de cliente e usuário.
│   │   │   │   │   │   │   │   ├── AvaliacaoServiceImpl.java  # Valida a entrega e coordena avaliações, upload e exclusão de fotos na moderação.
│   │   │   │   │   │   │   │   ├── ClubeServiceImpl.java  # Valida adesão, vigência, renovação e confirmação manual no Clube da Fome.
│   │   │   │   │   │   │   │   ├── ComandaServiceImpl.java  # Orquestra estoque, validação do operador, cotação de pagamentos e fechamento atômico.
│   │   │   │   │   │   │   │   ├── GamificacaoServiceImpl.java  # Controla cooldown, separa sorteios de demonstração e aplica validade e giro extra.
│   │   │   │   │   │   │   │   ├── PedidoVendaServiceImpl.java  # Valida expediente e disponibilidade e coordena baixa física, PIX e despacho.
│   │   │   │   │   │   │   │   ├── ProdutoEstoqueServiceImpl.java  # Gerencia disponibilidade, ajustes, lotes e perdas do estoque físico.
│   │   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   │   └── RelatorioFinanceiroServiceImpl.java  # Calcula indicadores financeiros e gera o balanço CSV.
│   │   │   │   │   │   │   ├── pix/  # Porta, adapters e conciliação da cobrança PIX na vitrine.
│   │   │   │   │   │   │   │   ├── LocalDemoPixGateway.java  # Gera um BR Code de demonstração sem pagamento ou confirmação externa.
│   │   │   │   │   │   │   │   ├── MercadoPagoPixGateway.java  # Traduz cobranças para JSON e usa HttpClient para chamar a API Payments com idempotência.
│   │   │   │   │   │   │   │   ├── PixConfiguracao.java  # Lê o token local do Mercado Pago e a taxa de entrega configurável.
│   │   │   │   │   │   │   │   ├── PixGatewayService.java  # Porta de criação e consulta das cobranças PIX do checkout.
│   │   │   │   │   │   │   │   ├── PixPagamentoService.java  # Concilia a resposta do gateway com a cobrança local, sem aprovar valores divergentes.
│   │   │   │   │   │   │   │   └── README.md  # Guia da integração PIX da vitrine.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AgendaService.java  # Contrato de operações e busca da agenda de clientes e fornecedores.
│   │   │   │   │   │   │   ├── AutenticacaoService.java  # Contrato para autenticação única e cadastro público de clientes.
│   │   │   │   │   │   │   ├── AvaliacaoService.java  # Contrato para avaliações, médias, fotos e moderação administrativa.
│   │   │   │   │   │   │   ├── BarramentoPedido.java  # Notifica observadores na mesma transação JDBC do evento de pedido.
│   │   │   │   │   │   │   ├── ClubeService.java  # Contrato de adesão, administração, confirmação de mensalidade e cancelamento.
│   │   │   │   │   │   │   ├── ComandaService.java  # Contrato para abertura, lançamento, rateio e pagamentos de comandas.
│   │   │   │   │   │   │   ├── ComboBuilder.java  # Monta o combo com uma variante real da massa e do recheio e acompanhamento opcional.
│   │   │   │   │   │   │   ├── ConquistasObserver.java  # Avalia selos, libera a roleta após pagamento ou entrega e revoga prêmios cancelados.
│   │   │   │   │   │   │   ├── FotoAvaliacaoStorage.java  # Valida, redimensiona, salva e remove imagens locais de avaliações.
│   │   │   │   │   │   │   ├── GamificacaoService.java  # Contrato de selos, pontos, cupons, giro regular e giro extra.
│   │   │   │   │   │   │   ├── OperacaoLojaService.java  # Avalia a abertura e valida agendamentos pelo expediente local, inclusive após a meia-noite.
│   │   │   │   │   │   │   ├── PedidoCancelamentoService.java  # Devolve somente o saldo físico baixado e libera o cupom ao cancelar.
│   │   │   │   │   │   │   ├── PedidoConcluidoObserver.java  # Contrato Observer para finalização, confirmação PIX, entrega e cancelamento.
│   │   │   │   │   │   │   ├── PedidoVendaService.java  # Contrato para simular descontos, finalizar pedidos e alterar status de vendas.
│   │   │   │   │   │   │   ├── PersonalizacaoService.java  # Sorteia caixas com produtos liberados e valida composições e restrições.
│   │   │   │   │   │   │   ├── PixPayloadService.java  # Monta BR Code com CRC16 e gera a imagem Pix em memória com ZXing.
│   │   │   │   │   │   │   ├── ProdutoEstoqueService.java  # Define operações do catálogo, bloqueio de venda e movimentações do estoque físico.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── RelatorioFinanceiroService.java  # Contrato de cálculos de balanço, gráficos e exportação CSV.
│   │   │   │   │   │   │   └── WhatsAppLinkService.java  # Gera e abre links do WhatsApp com o acompanhamento, status e nome do entregador.
│   │   │   │   │   │   ├── util/  # Conexão, autenticação auxiliar e navegação.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── ConnectionFactory.java  # Pool HikariCP e transações JDBC.
│   │   │   │   │   │   │   ├── FotoAvaliacaoUi.java  # Carrega miniaturas JavaFX de fotos de avaliações guardadas localmente.
│   │   │   │   │   │   │   ├── Navegacao.java  # Navegação entre telas FXML.
│   │   │   │   │   │   │   ├── PagamentoPixUi.java  # Mostra QR Code e consulta o pagamento com Timeline, sem bloquear a interface.
│   │   │   │   │   │   │   ├── PixPainel.java  # Desenha o QR local ou do gateway e disponibiliza o código PIX para cópia.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── RoletaUi.java  # Anima o prêmio transacional em seis fatias regulares ou VIP, com giro extra quando disponível.
│   │   │   │   │   │   │   ├── Senhas.java  # Geração e verificação de hashes de senha.
│   │   │   │   │   │   │   ├── TelaAutenticada.java  # Contrato para telas com usuário autenticado.
│   │   │   │   │   │   │   └── WhatsAppUi.java  # Feedback visual e alternativa para links WhatsApp.
│   │   │   │   │   │   ├── Main.java  # Inicialização da aplicação JavaFX.
│   │   │   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   ├── resources/  # Recursos institucionais ou recursos JavaFX, conforme a localização.
│   │   │   ├── css/  # Folhas de estilo das telas JavaFX.
│   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   ├── admin.css  # Estilos do painel administrativo.
│   │   │   │   ├── agenda.css  # Estilos da agenda.
│   │   │   │   ├── cliente-vitrine.css  # Estiliza catálogo, checkout, disponibilidade e banner de entrega em rota.
│   │   │   │   ├── global.css  # Estilos compartilhados da aplicação.
│   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   ├── fxml/  # Descrições de interface JavaFX.
│   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   ├── admin-dashboard.fxml  # Estrutura a navegação administrativa, incluindo o painel de status da loja.
│   │   │   │   ├── agenda-contatos.fxml  # Estrutura visual da agenda.
│   │   │   │   ├── cliente-vitrine.fxml  # Estrutura a vitrine, o selo de funcionamento, o carrinho e o histórico de pedidos.
│   │   │   │   ├── login.fxml  # Estrutura visual de login e cadastro.
│   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   └── selecao-modulos.fxml  # Estrutura visual da seleção de módulos.
│   │   │   ├── icons/  # Espaço reservado para ícones.
│   │   │   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   ├── images/  # Espaço reservado para imagens.
│   │   │   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   ├── database.properties  # Template JDBC e propriedades opcionais de taxa de entrega e pagamentos PIX.
│   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   ├── test/  # Testes automatizados.
│   │   ├── java/  # Pacotes de código Java.
│   │   │   ├── com/  # Raiz de pacotes Java.
│   │   │   │   ├── salgaderia/  # Pacote da aplicação Salgaderia.
│   │   │   │   │   ├── app/  # Componentes da aplicação e suas camadas.
│   │   │   │   │   │   ├── controller/  # Controllers das telas e testes de interface, conforme a localização.
│   │   │   │   │   │   │   ├── AdminDashboardJavaFXTest.java  # Testa navegação administrativa, PDV, mesas, Pix e alertas de novos pedidos.
│   │   │   │   │   │   │   ├── AgendaJavaFXTest.java  # Testes JavaFX da agenda.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── TelasJavaFXTest.java  # Testes JavaFX de autenticação e navegação.
│   │   │   │   │   │   │   └── VitrineJavaFXTest.java  # Verifica catálogo, checkout, expediente e atualização da entrega na vitrine.
│   │   │   │   │   │   ├── dao/  # Contratos de acesso a dados ou testes JDBC.
│   │   │   │   │   │   │   └── AgendaJDBCTest.java  # Teste de integração JDBC da agenda.
│   │   │   │   │   │   ├── model/  # Entidades de domínio ou testes dessas entidades.
│   │   │   │   │   │   │   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   │   │   │   │   │   │   └── EnumsTest.java  # Testes de comportamento dos enums.
│   │   │   │   │   │   │   └── DominioTest.java  # Testes das entidades e regras de domínio.
│   │   │   │   │   │   ├── service/  # Contratos, regras de negócio e testes de serviços.
│   │   │   │   │   │   │   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   │   │   │   │   │   │   └── EstrategiasDescontoTest.java  # Testa as estratégias e a escolha do melhor desconto.
│   │   │   │   │   │   │   ├── pix/  # Testes HTTP em loopback para a integração PIX.
│   │   │   │   │   │   │   │   ├── MercadoPagoPixGatewayTest.java  # Verifica a integração HTTP com um servidor local, sem emitir cobranças.
│   │   │   │   │   │   │   │   └── README.md  # Guia dos testes HTTP locais.
│   │   │   │   │   │   │   ├── ClubeGamificacaoIntegracaoTest.java  # Valida clube, cupons e roleta concorrentes e estados do pagamento PIX no MySQL.
│   │   │   │   │   │   │   ├── ComandaIntegracaoTest.java  # Valida operações de mesa, pagamentos concorrentes, baixa e fechamento no MySQL.
│   │   │   │   │   │   │   ├── CozinhaStateTest.java  # Verifica mensagens e progresso dos estados da cozinha.
│   │   │   │   │   │   │   ├── DescontoIntegracaoTest.java  # Integração de descontos e pagamentos no MySQL.
│   │   │   │   │   │   │   ├── Etapa4IntegracaoTest.java  # Verifica transações de estoque, pedidos, PDV, avaliações com foto e financeiro no MySQL.
│   │   │   │   │   │   │   ├── FotoAvaliacaoStorageTest.java  # Testa a cópia validada, limites, caminhos locais e remoção de anexos.
│   │   │   │   │   │   │   ├── OperacaoLojaServiceTest.java  # Testa os limites do expediente e a combinação dos modos manual e automático.
│   │   │   │   │   │   │   ├── OperacaoV162IntegracaoTest.java  # Valida configuração persistida, agendamento, venda sob demanda, bloqueio e despacho no MySQL.
│   │   │   │   │   │   │   ├── PedidoAgendamentoTest.java  # Verifica rejeição de agendamento passado.
│   │   │   │   │   │   │   ├── PersonalizacaoServiceTest.java  # Testa a composição do combo, sorteio com restrições e desconto não cumulativo.
│   │   │   │   │   │   │   ├── PixPayloadServiceTest.java  # Valida valor, referência e CRC16 e decodifica a imagem QR que foi gerada.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── RelatorioCsvTest.java  # Verifica o balanço exportado em CSV.
│   │   │   │   │   │   │   ├── VendaTeste.java  # Fornece relógio fixo e configuração isolada aos testes do fluxo de venda.
│   │   │   │   │   │   │   ├── VitrineV150IntegracaoTest.java  # Verifica combos e caixas, restrições e cancelamento com devolução da baixa real.
│   │   │   │   │   │   │   └── WhatsAppLinkServiceTest.java  # Testa links, mensagens e configuração WhatsApp.
│   │   │   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   └── README.md  # Guia de navegação e explicações desta pasta.
├── support/  # Documentação de apoio, referências e materiais complementares.
│   ├── documents/  # Espaço para documentos de apoio.
│   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── references/  # Espaço para referências.
│   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── tutorials/  # Espaço para tutoriais.
│   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   ├── videos/  # Espaço para materiais em vídeo.
│   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   └── README.md  # Guia de navegação e explicações desta pasta.
├── uploads/  # Armazena os anexos locais fora do código-fonte.
│   ├── avaliacoes/  # Guarda as fotos localmente; apenas o marcador é versionado.
│   │   └── .gitkeep  # Preserva a pasta vazia no repositório.
│   └── README.md  # Guia de armazenamento, privacidade e backup das fotos de avaliações.
├── .gitignore  # Ignora arquivos de configuração local, saídas de compilação e fotos de clientes.
├── AGENTS.md  # Instruções locais aplicáveis aos agentes no repositório.
├── CLAUDE.md  # Orientações adicionais sobre o projeto.
├── LICENSE  # Texto da licença MIT.
├── pom.xml  # Configuração Maven, dependências e build do módulo.
└── README.md  # Guia de navegação e explicações desta pasta.
```

## Diagramas

- [Modelo entidade-relacionamento](database/DER/modelo-entidade-relacionamento.md)
- [Modelo lógico do banco](database/DL/modelo-logico.md)
- [Diagrama de classes UML](docs/uml/diagrama-de-classes.md)

Os diagramas são blocos Mermaid em Markdown; use um visualizador que suporte Mermaid.

## Testes

```bash
mvn test
mvn clean test-compile
```

`test-compile` compila os testes, mas **não os executa**. Testes opcionais exigem recursos locais:

```bash
# MySQL local configurado e migrado; cria e limpa registros temporários
SALGADERIA_TESTE_MYSQL=true mvn -Dtest=Etapa4IntegracaoTest,DescontoIntegracaoTest,ComandaIntegracaoTest,AgendaJDBCTest,VitrineV150IntegracaoTest,ClubeGamificacaoIntegracaoTest test

# Servidor gráfico disponível
SALGADERIA_TESTE_JAVAFX=true mvn -Dtest=TelasJavaFXTest,AgendaJavaFXTest,VitrineJavaFXTest,AdminDashboardJavaFXTest test

# Suíte completa com ambos os recursos locais disponíveis
SALGADERIA_TESTE_MYSQL=true SALGADERIA_TESTE_JAVAFX=true mvn clean test
```

Os testes MySQL alteram temporariamente o banco configurado. Use um banco de desenvolvimento/teste, nunca dados de produção.
O teste do adapter usa um servidor HTTP em loopback. A suíte não acessa o Mercado Pago nem cria cobranças reais, mesmo que exista token local.

## Licença

Distribuído sob a [licença MIT](LICENSE).
