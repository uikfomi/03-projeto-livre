# Persistência e serviços — Etapa 4

## Banco

Para um banco novo, execute `database/scripts/schema.sql` e depois `database/scripts/etapa4_migracao.sql`. A migração é única, não deve ser repetida, e o MySQL efetiva DDL implicitamente; faça backup antes de aplicá-la em outro banco. Ela vincula o cliente inicial `root` a um contato. Usuários clientes adicionais sem contato e fornecedores antigos sem os novos campos exigem saneamento manual antes de serem carregados.

Custos históricos de itens e perdas anteriores à migração permanecem desconhecidos (`NULL`); o relatório recusa calcular períodos que os incluam, em vez de inventar valores. Novas vendas registram o custo de produção vigente na finalização, e perdas registram o custo vigente na movimentação.

## Regras adotadas

- Novos cadastros iniciam com estoque zero; lotes e perdas modificam estoque e lançam movimento na mesma transação. Atualizar produto não altera estoque.
- PDV e vitrine usam `PedidoVendaService.finalizar`: os produtos são bloqueados em ordem de ID, os preços são conferidos com o cadastro atual e o pedido, seus itens, as baixas e os movimentos são gravados atomicamente. O cancelamento antes da entrega devolve estoque atomicamente.
- O relatório contabiliza pedidos atualmente entregues pela **data de criação do pedido**, no intervalo semiaberto informado; perdas são contabilizadas pela data da movimentação. Lucro líquido real é faturamento bruto menos custo dos itens vendidos e perdas. Impostos e outras despesas não são modelados.
- As sementes legadas em texto puro continuam autenticáveis e são convertidas para PBKDF2 após o primeiro login bem-sucedido; novos cadastros usam PBKDF2. As credenciais iniciais são apenas para desenvolvimento.
- A checagem de administrador consulta o perfil ativo no banco, mas os serviços ainda recebem objetos de usuário fornecidos pelo chamador. Antes de expor essas operações a uma interface ou API não confiável é necessário implementar uma sessão autenticada que não permita forjar a identidade.

## Testes

`mvn test` executa os testes unitários. Para executar também o teste de integração, configure o banco local migrado e use `SALGADERIA_TESTE_MYSQL=true mvn -Dtest=Etapa4IntegracaoTest test`. O teste cria registros identificados aleatoriamente e remove apenas os que criou.
