# Ui K'Fomi — Salgaderia Pro

![Versão](https://img.shields.io/badge/vers%C3%A3o-v1.3.0-8b4513)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1)
[![Licença MIT](https://img.shields.io/badge/licen%C3%A7a-MIT-green)](LICENSE)

Projeto livre da **Ui K'fomi**, startup de Maranguape, Ceará, dedicada a soluções digitais para o setor de alimentação. Este repositório reúne o sistema integrado, incluindo os módulos de autenticação e agenda.

Sistema desktop para uma salgaderia, com login único, vitrine de pedidos, agenda de contatos e painel administrativo de vendas e estoque. O projeto adota **Java 17**, **JavaFX 21**, **MySQL** com **JDBC puro**, pool HikariCP e arquitetura **MVC + Service + DAO**, sem ORM.

## Índice

- [Funcionalidades](#funcionalidades)
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
- **Vitrine do cliente:** catálogo com filtros, imagens opcionais, preços, disponibilidade e médias de avaliações; carrinho com retirada ou entrega e escolha de PIX, dinheiro ou cartão; histórico e avaliação de produtos recebidos.
- **Administração:** PDV com formas de pagamento e troco, fila de pedidos da vitrine, produtos e estoque, entrada por fornecedor, perdas, indicadores financeiros, gráficos e moderação de avaliações.
- **Estados vazios:** as telas não exibem cadastros fictícios; mostras de ausência de dados são distintas de falhas de conexão.
- **WhatsApp:** após finalizar e no histórico, o cliente pode abrir uma conversa com a loja pelo botão de acompanhamento. Na agenda e na fila, o administrador pode abrir uma conversa com o contato; ao mudar o status, a opção de notificação vem marcada. O WhatsApp abre com mensagem preenchida: o envio continua dependendo de confirmação no aplicativo.
- **Acompanhamento:** a lista “Meus Pedidos” e o indicador administrativo de pedidos pendentes consultam o banco a cada cinco segundos enquanto as respectivas telas estão abertas. Os alertas recentes são locais à sessão administrativa.
- **Balanço CSV:** o painel financeiro permite exportar o período exibido, em UTF-8 com BOM e separador `;`, incluindo faturamento bruto, descontos, custos, perdas e lucro.

### Regras importantes

- Pedidos da vitrine avançam de `PENDENTE` para `PREPARANDO`, `PRONTO` e `ENTREGUE`. O cancelamento é permitido antes da entrega e devolve estoque. A venda de PDV é registrada como `ENTREGUE` na própria transação de finalização.
- **Descontos não cumulativos:** aplica-se a maior vantagem entre cupom ativo (`UIKFOMI10`: 10%; `FESTA15`: 15% a partir de R$ 80), fidelidade (5% para cliente com pelo menos três pedidos entregues) e cento (12% sobre os itens elegíveis quando houver pelo menos 100 unidades de salgados). Para o cento, categorias com “salgad”, “frit” ou “assad” no nome são elegíveis, exceto nomes contendo “bebid” ou “doc”, sem distinção de caixa ou acentos. Produtos sem categoria não entram. Pedidos de PDV identificados por cliente também contam para a fidelidade.
- Cupons inválidos, inativos ou abaixo do mínimo bloqueiam a finalização até sua correção ou remoção. O sistema mostra a melhor vantagem no carrinho e revalida preços, elegibilidade e desconto ao finalizar; se o resultado mudar, é necessário conferir novamente.
- Finalização, itens, baixa de estoque e movimentações compartilham uma transação JDBC; o estoque é bloqueado durante a venda. A falta de estoque aborta a operação.
- Produtos são criados com estoque zero; use **Entrada de lote** para registrar o estoque com um fornecedor. Alterar o cadastro do produto não altera o estoque.
- Uma avaliação só é aceita quando há pedido `ENTREGUE` do cliente contendo o produto. O acesso aos controles administrativos é restrito ao perfil de administrador na interface.
- O financeiro considera pedidos entregues pela **data de criação**, e perdas pela data da movimentação. O lucro exibido é faturamento após descontos menos custo dos itens vendidos e perdas; **não inclui impostos nem outras despesas**. A receita por categoria rateia o desconto proporcionalmente ao valor dos itens. O painel mostra o mês atual. Custos históricos anteriores à migração que permaneçam `NULL` não são estimados e impedem o cálculo do período correspondente.
- O número padrão de WhatsApp da loja é `5585989095223` e pode ser substituído por `whatsapp.loja` em `database.local.properties`. A mensagem inclui a forma de pagamento escolhida; pedidos históricos sem essa informação permanecem como “não informado”. Em ambientes sem navegador, tenta-se copiar o link; caso a área de transferência também esteja indisponível, o link é mostrado para cópia manual.

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
| Abstração | `model.Usuario.getTelaInicialFxml()` e `model.Usuario.podeAcessarPainelGerencial()`; `model.Contato.getResumoContato()` | Métodos abstratos definem comportamentos comuns sem implementar as variações dos subtipos. |
| Encapsulamento | `model.Produto.setPrecoVenda()`, `setCustoProducao()`, `setEstoqueAtual()`, `setEstoqueMinimo()`, `adicionarEstoque()` e `removerEstoque()` | Atributos privados e validações controlam valores monetários e quantidades de estoque. |
| Encapsulamento | `model.Pedido.getItens()`, `adicionarItem()`, `removerItem()` e `transicionarPara()`; `model.PedidoItem` e `calcularSubtotal()` | A lista retornada é uma cópia não modificável; o pedido controla alterações de itens e transições. Os campos de `PedidoItem` são finais. |
| Encapsulamento | `model.Avaliacao.setNota()`; `model.Fornecedor.setCnpj()` e `setPrazoEntregaDias()` | A nota deve estar entre 1 e 5; o CNPJ deve conter 14 dígitos após remoção da formatação e o prazo não pode ser negativo. A validação do CNPJ não calcula dígitos verificadores. |
| Herança | Construtores `model.UsuarioCliente(...)` e `model.UsuarioAdmin(...)`; `model.Cliente(...)` e `model.Fornecedor(...)` | `UsuarioCliente` e `UsuarioAdmin` estendem `Usuario`; `Cliente` e `Fornecedor` estendem `Contato`. |
| Polimorfismo | `model.UsuarioCliente.getTelaInicialFxml()` / `model.UsuarioAdmin.getTelaInicialFxml()`; `util.Navegacao.abrirTelaInicial()` | A navegação chama o método da referência `Usuario`, que retorna a tela específica do subtipo. |
| Polimorfismo | `model.Cliente.getResumoContato()` e `model.Fornecedor.getResumoContato()`; `model.enums.FormaPagamento.isPermiteTroco()` | Os subtipos formatam resumos diferentes; cada constante de pagamento implementa o método, permitindo troco apenas em `DINHEIRO`. |
| Interfaces | `service.PedidoVendaService.finalizar()` e `finalizarPdv()` implementados por `service.impl.PedidoVendaServiceImpl`; `service.AgendaService.buscar()` implementado por `service.impl.AgendaServiceImpl` | As interfaces definem contratos de aplicação independentes das implementações. |
| Interfaces | `dao.ContatoDAO.buscar()` implementado por `dao.impl.ContatoDAOImpl`; `dao.PedidoDAO.inserir()` implementado por `dao.impl.PedidoDAOImpl`; `util.ConnectionFactory.OperacaoTransacional.executar()` | Contratos DAO separam persistência da orquestração; a interface funcional recebe operações executadas em transação. |
| Exceções personalizadas | `model.Produto.removerEstoque()` lança `exception.EstoqueInsuficienteException`; `controller.ClienteVitrineController.finalizar()` e `controller.AdminDashboardController.executar()` tratam a falha | A exceção mantém produto, estoque disponível e quantidade solicitada; a interface informa o usuário. |
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
| `src/main/java/com/salgaderia/app/model/enums/` | [Abrir README](src/main/java/com/salgaderia/app/model/enums/README.md) |
| `src/main/java/com/salgaderia/app/service/` | [Abrir README](src/main/java/com/salgaderia/app/service/README.md) |
| `src/main/java/com/salgaderia/app/service/desconto/` | [Abrir README](src/main/java/com/salgaderia/app/service/desconto/README.md) |
| `src/main/java/com/salgaderia/app/service/impl/` | [Abrir README](src/main/java/com/salgaderia/app/service/impl/README.md) |
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
| `support/` | [Abrir README](support/README.md) |

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
│   │   │   │   │   │   │   ├── AdminDashboardController.java  # PDV, fila, estoque, financeiro e alertas administrativos.
│   │   │   │   │   │   │   ├── AgendaController.java  # Interface da agenda de clientes e fornecedores.
│   │   │   │   │   │   │   ├── ClienteVitrineController.java  # Vitrine, carrinho, pagamento e histórico do cliente.
│   │   │   │   │   │   │   ├── LoginController.java  # Tela de login e cadastro público.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   └── SelecaoModulosController.java  # Seleção de módulos administrativos.
│   │   │   │   │   │   ├── dao/  # Contratos de acesso a dados ou testes JDBC.
│   │   │   │   │   │   │   ├── impl/  # Implementações JDBC dos contratos DAO, com comandos parametrizados.
│   │   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   │   ├── AvaliacaoDAOImpl.java  # Consultas JDBC de avaliações, médias, respostas e exclusão.
│   │   │   │   │   │   │   │   ├── CategoriaDAOImpl.java  # Operações JDBC de cadastro e consulta de categorias.
│   │   │   │   │   │   │   │   ├── ContatoDAOImpl.java  # Operações JDBC de contatos com filtros de clientes e fornecedores.
│   │   │   │   │   │   │   │   ├── CupomDAOImpl.java  # Consulta JDBC parametrizada para localizar cupons ativos.
│   │   │   │   │   │   │   │   ├── MovimentacaoEstoqueDAOImpl.java  # Grava movimentações de estoque e consulta perdas por período via JDBC.
│   │   │   │   │   │   │   │   ├── PedidoDAOImpl.java  # Persiste pedidos, itens, estado, pagamentos e descontos; consulta fila e relatórios.
│   │   │   │   │   │   │   │   ├── ProdutoDAOImpl.java  # Persiste produtos, consulta categorias e atualiza estoque com JDBC.
│   │   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   │   └── UsuarioDAOImpl.java  # Persiste usuários, consulta credenciais e atualiza hashes de senha.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AvaliacaoDAO.java  # Contrato para consultar médias, listar, inserir, responder e excluir avaliações.
│   │   │   │   │   │   │   ├── CategoriaDAO.java  # Contrato para cadastrar, atualizar, buscar, listar e excluir categorias.
│   │   │   │   │   │   │   ├── ContatoDAO.java  # Contrato para cadastro, edição, exclusão e busca filtrada de contatos.
│   │   │   │   │   │   │   ├── CupomDAO.java  # Contrato para consultar cupom ativo por código.
│   │   │   │   │   │   │   ├── MovimentacaoEstoqueDAO.java  # Contrato para registrar movimentações e calcular perdas por período.
│   │   │   │   │   │   │   ├── PedidoDAO.java  # Contrato para pedidos, fila, status, pagamentos, descontos e agregações de vendas.
│   │   │   │   │   │   │   ├── ProdutoDAO.java  # Contrato para cadastro de produtos, consultas e atualização de estoque.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   └── UsuarioDAO.java  # Contrato para inserir usuários, consultar login e atualizar senhas.
│   │   │   │   │   │   ├── exception/  # Exceções personalizadas de domínio e persistência.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AutenticacaoInvalidaException.java  # Falha de autenticação.
│   │   │   │   │   │   │   ├── AvaliacaoNaoPermitidaException.java  # Avaliação sem compra elegível.
│   │   │   │   │   │   │   ├── EstoqueInsuficienteException.java  # Erro de quantidade indisponível.
│   │   │   │   │   │   │   ├── PersistenciaException.java  # Encapsula erro de persistência.
│   │   │   │   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   ├── model/  # Entidades de domínio ou testes dessas entidades.
│   │   │   │   │   │   │   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   │   ├── CategoriaInsumo.java  # Categorias de insumos de fornecedores.
│   │   │   │   │   │   │   │   ├── FormaPagamento.java  # Formas de pagamento e regra de troco.
│   │   │   │   │   │   │   │   ├── PerfilUsuario.java  # Perfis de acesso do usuário.
│   │   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   │   ├── StatusPedido.java  # Estados e transições permitidas do pedido.
│   │   │   │   │   │   │   │   ├── TipoEntrega.java  # Modalidades de entrega.
│   │   │   │   │   │   │   │   └── TipoMovimentacaoEstoque.java  # Tipos de movimentação de estoque.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── Avaliacao.java  # Avaliação de produto e validação da nota.
│   │   │   │   │   │   │   ├── AvaliacaoResumo.java  # Dados resumidos para moderação.
│   │   │   │   │   │   │   ├── Categoria.java  # Categoria de produtos.
│   │   │   │   │   │   │   ├── Cliente.java  # Contato especializado em cliente.
│   │   │   │   │   │   │   ├── Contato.java  # Abstração comum de contatos.
│   │   │   │   │   │   │   ├── Fornecedor.java  # Contato especializado em fornecedor.
│   │   │   │   │   │   │   ├── MovimentacaoEstoque.java  # Registro de entrada ou saída de estoque.
│   │   │   │   │   │   │   ├── Pedido.java  # Agregado do pedido, itens, desconto e pagamento.
│   │   │   │   │   │   │   ├── PedidoFila.java  # Resumo de pedido para a fila administrativa.
│   │   │   │   │   │   │   ├── PedidoItem.java  # Quantidade e preço histórico de um item.
│   │   │   │   │   │   │   ├── Produto.java  # Produto, preços, estoque e comportamento de domínio.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── Usuario.java  # Abstração de usuário e permissões.
│   │   │   │   │   │   │   ├── UsuarioAdmin.java  # Usuário administrativo.
│   │   │   │   │   │   │   ├── UsuarioCliente.java  # Usuário vinculado a um cliente.
│   │   │   │   │   │   │   └── Validacao.java  # Validações compartilhadas das entidades.
│   │   │   │   │   │   ├── service/  # Contratos, regras de negócio e testes de serviços.
│   │   │   │   │   │   │   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   │   │   │   │   │   │   ├── CalculadoraDescontos.java  # Seleciona o maior desconto elegível.
│   │   │   │   │   │   │   │   ├── ContextoDesconto.java  # Dados de entrada para estratégias de desconto.
│   │   │   │   │   │   │   │   ├── Cupom.java  # Valor de domínio de cupom.
│   │   │   │   │   │   │   │   ├── DescontoCentoSalgados.java  # Estratégia de desconto para cento de salgados.
│   │   │   │   │   │   │   │   ├── DescontoCupomPercentual.java  # Estratégia percentual por cupom.
│   │   │   │   │   │   │   │   ├── DescontoFidelidade.java  # Estratégia para clientes fiéis.
│   │   │   │   │   │   │   │   ├── EstrategiaDesconto.java  # Contrato Strategy para descontos.
│   │   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   │   └── ResultadoDesconto.java  # Resumo do desconto e total calculados.
│   │   │   │   │   │   │   ├── impl/  # Implementações de regras e orquestração dos serviços.
│   │   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   │   ├── AgendaServiceImpl.java  # Valida e coordena cadastro, busca e alterações de contatos pela camada DAO.
│   │   │   │   │   │   │   │   ├── AutenticacaoServiceImpl.java  # Orquestra login, verificação de senha e cadastro de cliente e usuário.
│   │   │   │   │   │   │   │   ├── AvaliacaoServiceImpl.java  # Valida avaliações elegíveis e controla operações de moderação.
│   │   │   │   │   │   │   │   ├── PedidoVendaServiceImpl.java  # Executa vendas transacionais, descontos, movimentações de estoque e transições.
│   │   │   │   │   │   │   │   ├── ProdutoEstoqueServiceImpl.java  # Valida produtos e coordena entradas, perdas e consultas de estoque.
│   │   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   │   └── RelatorioFinanceiroServiceImpl.java  # Calcula indicadores financeiros e gera o balanço CSV.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── AgendaService.java  # Contrato de operações e busca da agenda de clientes e fornecedores.
│   │   │   │   │   │   │   ├── AutenticacaoService.java  # Contrato para autenticação única e cadastro público de clientes.
│   │   │   │   │   │   │   ├── AvaliacaoService.java  # Contrato para avaliações, médias por produto e moderação administrativa.
│   │   │   │   │   │   │   ├── PedidoVendaService.java  # Contrato para simular descontos, finalizar pedidos e alterar status de vendas.
│   │   │   │   │   │   │   ├── ProdutoEstoqueService.java  # Contrato para produtos, consultas de estoque, entradas e perdas.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── RelatorioFinanceiroService.java  # Contrato de cálculos de balanço, gráficos e exportação CSV.
│   │   │   │   │   │   │   └── WhatsAppLinkService.java  # Constrói e abre links de mensagens WhatsApp.
│   │   │   │   │   │   ├── util/  # Conexão, autenticação auxiliar e navegação.
│   │   │   │   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   │   │   │   ├── ConnectionFactory.java  # Pool HikariCP e transações JDBC.
│   │   │   │   │   │   │   ├── Navegacao.java  # Navegação entre telas FXML.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
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
│   │   │   │   ├── cliente-vitrine.css  # Estilos da vitrine.
│   │   │   │   ├── global.css  # Estilos compartilhados da aplicação.
│   │   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   ├── fxml/  # Descrições de interface JavaFX.
│   │   │   │   ├── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   │   ├── admin-dashboard.fxml  # Estrutura visual do painel administrativo.
│   │   │   │   ├── agenda-contatos.fxml  # Estrutura visual da agenda.
│   │   │   │   ├── cliente-vitrine.fxml  # Estrutura visual da vitrine e checkout.
│   │   │   │   ├── login.fxml  # Estrutura visual de login e cadastro.
│   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   └── selecao-modulos.fxml  # Estrutura visual da seleção de módulos.
│   │   │   ├── icons/  # Espaço reservado para ícones.
│   │   │   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   ├── images/  # Espaço reservado para imagens.
│   │   │   │   └── .gitkeep  # Marcador que mantém a pasta no controle de versão; não contém dados de aplicação.
│   │   │   ├── database.properties  # Template de configuração JDBC; não contém credenciais locais.
│   │   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   │   └── README.md  # Guia de navegação e explicações desta pasta.
│   ├── test/  # Testes automatizados.
│   │   ├── java/  # Pacotes de código Java.
│   │   │   ├── com/  # Raiz de pacotes Java.
│   │   │   │   ├── salgaderia/  # Pacote da aplicação Salgaderia.
│   │   │   │   │   ├── app/  # Componentes da aplicação e suas camadas.
│   │   │   │   │   │   ├── controller/  # Testes automatizados dos componentes desta camada.
│   │   │   │   │   │   │   ├── AdminDashboardJavaFXTest.java  # Testes JavaFX do painel e notificações.
│   │   │   │   │   │   │   ├── AgendaJavaFXTest.java  # Testes JavaFX da agenda.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── TelasJavaFXTest.java  # Testes JavaFX de autenticação e navegação.
│   │   │   │   │   │   │   └── VitrineJavaFXTest.java  # Testes JavaFX da vitrine, pagamento e atualização.
│   │   │   │   │   │   ├── dao/  # Testes automatizados dos componentes desta camada.
│   │   │   │   │   │   │   └── AgendaJDBCTest.java  # Teste de integração JDBC da agenda.
│   │   │   │   │   │   ├── model/  # Testes automatizados dos componentes desta camada.
│   │   │   │   │   │   │   ├── enums/  # Enumerações com comportamento e respectivos testes.
│   │   │   │   │   │   │   │   └── EnumsTest.java  # Testes de comportamento dos enums.
│   │   │   │   │   │   │   └── DominioTest.java  # Testes das entidades e regras de domínio.
│   │   │   │   │   │   ├── service/  # Testes automatizados dos componentes desta camada.
│   │   │   │   │   │   │   ├── desconto/  # Estratégias de desconto e testes correspondentes.
│   │   │   │   │   │   │   │   └── EstrategiasDescontoTest.java  # Testa as estratégias e a escolha do melhor desconto.
│   │   │   │   │   │   │   ├── DescontoIntegracaoTest.java  # Integração de descontos e pagamentos no MySQL.
│   │   │   │   │   │   │   ├── Etapa4IntegracaoTest.java  # Fluxos transacionais no MySQL.
│   │   │   │   │   │   │   ├── README.md  # Guia de navegação e explicações desta pasta.
│   │   │   │   │   │   │   ├── RelatorioCsvTest.java  # Verifica o balanço exportado em CSV.
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
├── .gitignore  # Regras para não versionar configurações locais e artefatos gerados.
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
SALGADERIA_TESTE_MYSQL=true mvn -Dtest=Etapa4IntegracaoTest,DescontoIntegracaoTest,AgendaJDBCTest test

# Servidor gráfico disponível
SALGADERIA_TESTE_JAVAFX=true mvn -Dtest=TelasJavaFXTest,AgendaJavaFXTest,VitrineJavaFXTest,AdminDashboardJavaFXTest test
```

Os testes MySQL alteram temporariamente o banco configurado. Use um banco de desenvolvimento/teste, nunca dados de produção.

## Licença

Distribuído sob a [licença MIT](LICENSE).
