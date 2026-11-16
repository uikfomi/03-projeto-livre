package com.salgaderia.app.service;

import com.salgaderia.app.dao.MovimentacaoEstoqueDAO;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.exception.*;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.impl.*;
import com.salgaderia.app.util.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Teste opcional contra banco local migrado; remove apenas registros criados pelo teste. */
@EnabledIfEnvironmentVariable(named="SALGADERIA_TESTE_MYSQL",matches="true")
class Etapa4IntegracaoTest {
    @Test void fluxosTransacionais() throws Exception {
        String token=UUID.randomUUID().toString().replace("-","");
        UsuarioCliente usuario=null; Fornecedor fornecedor=null; Produto produto=null; Long pedidoId=null; Long avaliacaoId=null; Long pdvId=null; Long pdvPixId=null;
        try {
            var auth=new AutenticacaoServiceImpl();
            Cliente cliente=new Cliente(null,"Teste "+token,null,null,null,null,null);
            usuario=auth.cadastrar("Teste", "t"+token, "senha-teste", cliente);
            assertEquals(usuario.getId(),auth.login("t"+token,"senha-teste").getId());
            assertThrows(AutenticacaoInvalidaException.class,()->auth.login("t"+token,"invalida"));
            fornecedor=new Fornecedor(null,"Contato "+token,null,null,"Empresa "+token,"12345678000199",CategoriaInsumo.FARINHA,1);
            var agenda=new AgendaServiceImpl(new ContatoDAOImpl());agenda.cadastrar(fornecedor);
            assertEquals(1,agenda.buscar(com.salgaderia.app.dao.ContatoDAO.FiltroContato.FORNECEDORES,token).size());
            produto=new Produto(null,null,"Produto "+token,null,new BigDecimal("8.00"),new BigDecimal("3.00"),BigDecimal.ZERO,BigDecimal.ONE,null);
            var estoque=new ProdutoEstoqueServiceImpl();estoque.cadastrar(produto);estoque.entradaLote(produto.getId(),fornecedor.getId(),new BigDecimal("3"),"teste");
            LocalDateTime data=LocalDateTime.of(2001,1,1,12,0);
            Pedido pedido=new Pedido(null,usuario,cliente,StatusPedido.PENDENTE,TipoEntrega.LOCAL,null,data,List.of(new PedidoItem(null,produto,new BigDecimal("2"),new BigDecimal("8.00"))));
            pedido.setFormaPagamento(FormaPagamento.PIX);
            LocalDateTime agendado=LocalDateTime.now().plusDays(1).withNano(0);pedido.setAgendadoPara(agendado);
            var vendas=new PedidoVendaServiceImpl();
            MovimentacaoEstoqueDAO falha=new MovimentacaoEstoqueDAO(){
                public long inserir(Connection c,MovimentacaoEstoque m,Long f,String motivo,BigDecimal custo) throws SQLException {throw new SQLException("falha simulada");}
                public BigDecimal calcularPerdas(Connection c,LocalDateTime i,LocalDateTime f){return BigDecimal.ZERO;}
            };
            var vendasComFalha=new PedidoVendaServiceImpl(new PedidoDAOImpl(),new ProdutoDAOImpl(),falha);
            assertThrows(PersistenciaException.class,()->vendasComFalha.finalizar(pedido));
            assertNull(pedido.getId());assertEquals(0,estoque.buscarPorId(produto.getId()).orElseThrow().getEstoqueAtual().compareTo(new BigDecimal("3")));
            pedidoId=vendas.finalizar(pedido);
            long pedidoFilaId=pedidoId;
            assertTrue(vendas.listarFila().stream().anyMatch(f->f.id()==pedidoFilaId && f.itens().contains("Produto "+token)));
            assertEquals(0,estoque.buscarPorId(produto.getId()).orElseThrow().getEstoqueAtual().compareTo(BigDecimal.ONE));
            Pedido outro=new Pedido(null,usuario,cliente,StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,data,List.of(new PedidoItem(null,produto,new BigDecimal("2"),new BigDecimal("8.00"))));
            outro.setFormaPagamento(FormaPagamento.DINHEIRO);
            assertThrows(EstoqueInsuficienteException.class,()->vendas.finalizar(outro));assertNull(outro.getId());
            Avaliacao avaliacao=new Avaliacao(null,usuario,produto,5,"ótimo",null,data);
            var avaliacoes=new AvaliacaoServiceImpl();
            assertThrows(AvaliacaoNaoPermitidaException.class,()->avaliacoes.salvar(avaliacao));
            vendas.transicionar(pedidoId,StatusPedido.PREPARANDO);vendas.transicionar(pedidoId,StatusPedido.PRONTO);vendas.transicionar(pedidoId,StatusPedido.ENTREGUE);
            var historico=vendas.listarDoCliente(usuario);
            assertEquals(1,historico.size());
            assertEquals(StatusPedido.ENTREGUE,historico.get(0).getStatus());
            assertEquals(FormaPagamento.PIX,historico.get(0).getFormaPagamento());
            assertEquals(TipoEntrega.LOCAL,historico.get(0).getTipoEntrega());
            assertEquals(agendado,historico.get(0).getAgendadoPara());
            UsuarioCliente clienteParaRepeticao=usuario;
            assertThrows(IllegalArgumentException.class,()->vendas.repetirUltimoPedido(clienteParaRepeticao));
            assertEquals(0,historico.get(0).getItens().get(0).getPrecoUnitario().compareTo(new BigDecimal("8.00")));
            UsuarioCliente outroCliente=new UsuarioCliente(Long.MAX_VALUE,"Outro","outro","senha",true,cliente);
            assertTrue(vendas.listarDoCliente(outroCliente).isEmpty());
            avaliacaoId=avaliacoes.salvar(avaliacao);
            assertEquals(0,avaliacoes.mediasPorProduto().get(produto.getId()).compareTo(new BigDecimal("5")));
            Usuario admin=new UsuarioAdmin(2L,"Admin","admin","nao-usada",true);
            assertTrue(avaliacoes.responder(admin,avaliacaoId,"obrigado"));
            var relatorio=new RelatorioFinanceiroServiceImpl().calcular(data.minusHours(1),data.plusHours(1));
            assertTrue(relatorio.faturamentoAposDescontos().compareTo(new BigDecimal("16"))>=0);
            assertTrue(relatorio.custoTotalProducao().compareTo(new BigDecimal("6"))>=0);
            assertEquals(0,relatorio.lucroLiquidoReal().compareTo(relatorio.faturamentoAposDescontos().subtract(relatorio.custoTotalProducao()).subtract(relatorio.perdas())));
            estoque.registrarPerda(produto.getId(),BigDecimal.ONE,"teste");
            assertEquals(0,estoque.buscarPorId(produto.getId()).orElseThrow().getEstoqueAtual().signum());
            estoque.entradaLote(produto.getId(),fornecedor.getId(),new BigDecimal("2"),"novo lote");
            assertEquals(0,new BigDecimal("2").compareTo(vendas.repetirUltimoPedido(clienteParaRepeticao).get(0).getQuantidade()));
            Produto atual=estoque.buscarPorId(produto.getId()).orElseThrow();
            Pedido venda=new Pedido(null,admin,cliente,StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,LocalDateTime.now(),List.of(new PedidoItem(null,atual,BigDecimal.ONE,atual.getPrecoVenda())));
            assertThrows(IllegalArgumentException.class,()->vendas.finalizarPdv(venda,FormaPagamento.DINHEIRO,new BigDecimal("1.00")));
            assertThrows(PersistenciaException.class,()->vendasComFalha.finalizarPdv(venda,FormaPagamento.DINHEIRO,new BigDecimal("10.00")));
            assertNull(venda.getId());
            assertEquals(0,estoque.buscarPorId(produto.getId()).orElseThrow().getEstoqueAtual().compareTo(new BigDecimal("2")));
            pdvId=vendas.finalizarPdv(venda,FormaPagamento.DINHEIRO,new BigDecimal("10.00"));
            try(Connection c=ConnectionFactory.getConnection();PreparedStatement ps=c.prepareStatement("SELECT origem,status,forma_pagamento,troco FROM pedidos WHERE id=?")){
                ps.setLong(1,pdvId);try(ResultSet rs=ps.executeQuery()){assertTrue(rs.next());assertEquals("PDV",rs.getString(1));assertEquals("ENTREGUE",rs.getString(2));assertEquals("DINHEIRO",rs.getString(3));assertEquals(0,new BigDecimal("2.00").compareTo(rs.getBigDecimal(4)));}
            }
            assertEquals(0,estoque.buscarPorId(produto.getId()).orElseThrow().getEstoqueAtual().compareTo(BigDecimal.ONE));
            Produto restante=estoque.buscarPorId(produto.getId()).orElseThrow();
            Pedido pix=new Pedido(null,admin,null,StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,LocalDateTime.now(),List.of(new PedidoItem(null,restante,BigDecimal.ONE,restante.getPrecoVenda())));
            pdvPixId=vendas.finalizarPdv(pix,FormaPagamento.PIX,new BigDecimal("20.00"));
            try(Connection c=ConnectionFactory.getConnection();PreparedStatement ps=c.prepareStatement("SELECT forma_pagamento,valor_recebido,troco FROM pedidos WHERE id=?")){
                ps.setLong(1,pdvPixId);try(ResultSet rs=ps.executeQuery()){assertTrue(rs.next());assertEquals("PIX",rs.getString(1));assertEquals(0,rs.getBigDecimal(2).compareTo(new BigDecimal("8.00")));assertEquals(0,rs.getBigDecimal(3).signum());}
            }
            assertTrue(avaliacoes.listar().stream().anyMatch(a->a.produto().equals(atual.getNome())));
            LocalDateTime inicio=LocalDateTime.now().minusDays(1),fim=LocalDateTime.now().plusDays(1);
            var financeiro=new RelatorioFinanceiroServiceImpl();
            assertTrue(financeiro.produtosMaisVendidos(inicio,fim).keySet().stream().anyMatch(nome->nome.contains(atual.getNome())));
            assertTrue(financeiro.receitaPorCategoria(inicio,fim).containsKey("Sem categoria"));
        } finally {
            try(Connection c=ConnectionFactory.getConnection()) {
                if(avaliacaoId!=null) excluir(c,"DELETE FROM avaliacoes WHERE id=?",avaliacaoId);
                if(pedidoId!=null){excluir(c,"DELETE FROM pedido_itens WHERE pedido_id=?",pedidoId);excluir(c,"DELETE FROM pedidos WHERE id=?",pedidoId);}
                if(pdvId!=null){excluir(c,"DELETE FROM pedido_itens WHERE pedido_id=?",pdvId);excluir(c,"DELETE FROM pedidos WHERE id=?",pdvId);}
                if(pdvPixId!=null){excluir(c,"DELETE FROM pedido_itens WHERE pedido_id=?",pdvPixId);excluir(c,"DELETE FROM pedidos WHERE id=?",pdvPixId);}
                if(produto!=null && produto.getId()!=null){excluir(c,"DELETE FROM movimentacoes_estoque WHERE produto_id=?",produto.getId());excluir(c,"DELETE FROM produtos WHERE id=?",produto.getId());}
                if(usuario!=null){excluir(c,"DELETE FROM usuarios WHERE id=?",usuario.getId());excluir(c,"DELETE FROM contatos WHERE id=?",usuario.getCliente().getId());}
                if(fornecedor!=null && fornecedor.getId()!=null)excluir(c,"DELETE FROM contatos WHERE id=?",fornecedor.getId());
            }
        }
    }
    private static void excluir(Connection c,String sql,long id) throws SQLException {try(PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,id);ps.executeUpdate();}}
}
