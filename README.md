# Salgaderia Pro

Sistema desktop para uma salgaderia, com login único, vitrine de pedidos, agenda de contatos e painel administrativo de vendas e estoque. O projeto adota **Java 17**, **JavaFX 21**, **MySQL** com **JDBC puro**, pool HikariCP e arquitetura **MVC + Service + DAO**, sem ORM.

## Funcionalidades

- **Autenticação e cadastro público:** perfil `CLIENTE` ou `ADMIN`; o cadastro público cria um cliente na agenda. A tela inicial é escolhida polimorficamente conforme o perfil.
- **Agenda:** clientes e fornecedores, busca, filtros, cadastro, edição e exclusão. Contatos vinculados a outros registros podem ter a exclusão impedida pelo banco.
- **Vitrine do cliente:** catálogo com filtros, imagens opcionais, preços, disponibilidade e médias de avaliações; carrinho com retirada ou entrega no endereço cadastrado; histórico e avaliação de produtos recebidos.
- **Administração:** PDV com formas de pagamento e troco, fila de pedidos da vitrine, produtos e estoque, entrada por fornecedor, perdas, indicadores financeiros, gráficos e moderação de avaliações.
- **Estados vazios:** as telas não exibem cadastros fictícios; mostras de ausência de dados são distintas de falhas de conexão.

### Regras importantes

- Pedidos da vitrine avançam de `PENDENTE` para `PREPARANDO`, `PRONTO` e `ENTREGUE`. O cancelamento é permitido antes da entrega e devolve estoque. A venda de PDV é registrada como `ENTREGUE` na própria transação de finalização.
- Finalização, itens, baixa de estoque e movimentações compartilham uma transação JDBC; o estoque é bloqueado durante a venda. A falta de estoque aborta a operação.
- Produtos são criados com estoque zero; use **Entrada de lote** para registrar o estoque com um fornecedor. Alterar o cadastro do produto não altera o estoque.
- Uma avaliação só é aceita quando há pedido `ENTREGUE` do cliente contendo o produto. O acesso aos controles administrativos é restrito ao perfil de administrador na interface.
- O financeiro considera pedidos entregues pela **data de criação**, e perdas pela data da movimentação. O lucro exibido é faturamento bruto menos custo dos itens vendidos e perdas; **não inclui impostos nem outras despesas**. O painel mostra o mês atual. Custos históricos anteriores à migração que permaneçam `NULL` não são estimados e impedem o cálculo do período correspondente.

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
```

`ConnectionFactory` procura `database.local.properties` no **classpath** e, se ele não existir, usa `database.properties`. O arquivo local é ignorado pelo Git. O Maven copia recursos para `target/classes`; portanto, a configuração local pode também entrar em artefatos empacotados: **não distribua um build contendo credenciais**. Revise a configuração de recursos antes de qualquer distribuição.

### 4. Compilar e iniciar

```bash
mvn clean test-compile
mvn javafx:run
```

A aplicação abre na tela única de login. Em caso de falha, verifique se o servidor MySQL está ativo, se os três scripts foram aplicados e se a conta tem acesso ao banco. Para renovar os recursos copiados ao `target/classes` após editar a configuração, repita a compilação.

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
SALGADERIA_TESTE_MYSQL=true mvn -Dtest=Etapa4IntegracaoTest,AgendaJDBCTest test

# Servidor gráfico disponível
SALGADERIA_TESTE_JAVAFX=true mvn -Dtest=TelasJavaFXTest,AgendaJavaFXTest,VitrineJavaFXTest,AdminDashboardJavaFXTest test
```

Os testes MySQL alteram temporariamente o banco configurado. Use um banco de desenvolvimento/teste, nunca dados de produção.

## Licença

Distribuído sob a [licença MIT](LICENSE).
