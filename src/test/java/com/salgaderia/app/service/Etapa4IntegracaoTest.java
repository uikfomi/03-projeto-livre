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
        UsuarioCliente usuario=null; Fornecedor fornecedor=null; Produto produto=null; Long pedidoId=null; Long avaliacaoId=null;
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
            Pedido pedido=new Pedido(null,usuario,cliente,StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,data,List.of(new PedidoItem(null,produto,new BigDecimal("2"),new BigDecimal("8.00"))));
            var vendas=new PedidoVendaServiceImpl();
            MovimentacaoEstoqueDAO falha=new MovimentacaoEstoqueDAO(){
                public long inserir(Connection c,MovimentacaoEstoque m,Long f,String motivo,BigDecimal custo) throws SQLException {throw new SQLException("falha simulada");}
                public BigDecimal calcularPerdas(Connection c,LocalDateTime i,LocalDateTime f){return BigDecimal.ZERO;}
            };
            var vendasComFalha=new PedidoVendaServiceImpl(new PedidoDAOImpl(),new ProdutoDAOImpl(),falha);
            assertThrows(PersistenciaException.class,()->vendasComFalha.finalizar(pedido));
            assertNull(pedido.getId());assertEquals(0,estoque.buscarPorId(produto.getId()).orElseThrow().getEstoqueAtual().compareTo(new BigDecimal("3")));
            pedidoId=vendas.finalizar(pedido);
            assertEquals(0,estoque.buscarPorId(produto.getId()).orElseThrow().getEstoqueAtual().compareTo(BigDecimal.ONE));
            Pedido outro=new Pedido(null,usuario,cliente,StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,data,List.of(new PedidoItem(null,produto,new BigDecimal("2"),new BigDecimal("8.00"))));
            assertThrows(EstoqueInsuficienteException.class,()->vendas.finalizar(outro));assertNull(outro.getId());
            Avaliacao avaliacao=new Avaliacao(null,usuario,produto,5,"ótimo",null,data);
            var avaliacoes=new AvaliacaoServiceImpl();
            assertThrows(AvaliacaoNaoPermitidaException.class,()->avaliacoes.salvar(avaliacao));
            vendas.transicionar(pedidoId,StatusPedido.PREPARANDO);vendas.transicionar(pedidoId,StatusPedido.PRONTO);vendas.transicionar(pedidoId,StatusPedido.ENTREGUE);
            var historico=vendas.listarDoCliente(usuario);
            assertEquals(1,historico.size());
            assertEquals(StatusPedido.ENTREGUE,historico.get(0).getStatus());
            assertEquals(0,historico.get(0).getItens().get(0).getPrecoUnitario().compareTo(new BigDecimal("8.00")));
            UsuarioCliente outroCliente=new UsuarioCliente(Long.MAX_VALUE,"Outro","outro","senha",true,cliente);
            assertTrue(vendas.listarDoCliente(outroCliente).isEmpty());
            avaliacaoId=avaliacoes.salvar(avaliacao);
            assertEquals(0,avaliacoes.mediasPorProduto().get(produto.getId()).compareTo(new BigDecimal("5")));
            Usuario admin=new UsuarioAdmin(2L,"Admin","admin","nao-usada",true);
            assertTrue(avaliacoes.responder(admin,avaliacaoId,"obrigado"));
            var relatorio=new RelatorioFinanceiroServiceImpl().calcular(data.minusHours(1),data.plusHours(1));
            assertTrue(relatorio.faturamentoBruto().compareTo(new BigDecimal("16"))>=0);
            assertTrue(relatorio.custoTotalProducao().compareTo(new BigDecimal("6"))>=0);
            assertEquals(0,relatorio.lucroLiquidoReal().compareTo(relatorio.faturamentoBruto().subtract(relatorio.custoTotalProducao()).subtract(relatorio.perdas())));
            estoque.registrarPerda(produto.getId(),BigDecimal.ONE,"teste");
            assertEquals(0,estoque.buscarPorId(produto.getId()).orElseThrow().getEstoqueAtual().signum());
        } finally {
            try(Connection c=ConnectionFactory.getConnection()) {
                if(avaliacaoId!=null) excluir(c,"DELETE FROM avaliacoes WHERE id=?",avaliacaoId);
                if(pedidoId!=null){excluir(c,"DELETE FROM pedido_itens WHERE pedido_id=?",pedidoId);excluir(c,"DELETE FROM pedidos WHERE id=?",pedidoId);}
                if(produto!=null && produto.getId()!=null){excluir(c,"DELETE FROM movimentacoes_estoque WHERE produto_id=?",produto.getId());excluir(c,"DELETE FROM produtos WHERE id=?",produto.getId());}
                if(usuario!=null){excluir(c,"DELETE FROM usuarios WHERE id=?",usuario.getId());excluir(c,"DELETE FROM contatos WHERE id=?",usuario.getCliente().getId());}
                if(fornecedor!=null && fornecedor.getId()!=null)excluir(c,"DELETE FROM contatos WHERE id=?",fornecedor.getId());
            }
        }
    }
    private static void excluir(Connection c,String sql,long id) throws SQLException {try(PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,id);ps.executeUpdate();}}
}
