package com.salgaderia.app.service;

import com.salgaderia.app.dao.impl.ProdutoDAOImpl;
import com.salgaderia.app.exception.EstoqueInsuficienteException;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.FichaVitrine.TipoCombo;
import com.salgaderia.app.model.PersonalizacaoItem.Restricao;
import com.salgaderia.app.model.PersonalizacaoItem.Tipo;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.impl.AutenticacaoServiceImpl;
import com.salgaderia.app.service.impl.PedidoVendaServiceImpl;
import com.salgaderia.app.util.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Usa somente registros de teste próprios e os remove mesmo em caso de falha. */
@EnabledIfEnvironmentVariable(named="SALGADERIA_TESTE_MYSQL",matches="true")
class VitrineV150IntegracaoTest {
    @Test void comboECaixaTemEstoqueCompartilhadoRevalidacaoEHistorico() {
        String token=UUID.randomUUID().toString().replace("-","");
        var dao=new ProdutoDAOImpl();
        var vendas=new PedidoVendaServiceImpl();
        Produto base=new Produto(null,null,"Empada "+token,null,new BigDecimal("8.00"),BigDecimal.ONE,new BigDecimal("12"),BigDecimal.ZERO,null);
        FichaVitrine ficha=new FichaVitrine(TipoCombo.EMPADA,"Carne de Sol",false,false,true);
        base.setFichaVitrine(ficha);
        Produto molho=new Produto(null,null,"Molho "+token,null,new BigDecimal("2.00"),BigDecimal.ONE,new BigDecimal("2"),BigDecimal.ZERO,null);
        molho.setFichaVitrine(new FichaVitrine(TipoCombo.ACOMPANHAMENTO,null,null,null,false));
        UsuarioCliente usuario=new AutenticacaoServiceImpl().cadastrar("Teste","caixa"+token,"senha-teste",new Cliente(null,"Cliente "+token,null,null,null,null,null));
        try {
            long[] ids=ConnectionFactory.executarEmTransacao(c->new long[]{dao.inserir(c,base),dao.inserir(c,molho)});
            base.setId(ids[0]);molho.setId(ids[1]);
            List<PedidoItem> itens=new ArrayList<>(new ComboBuilder().massa(TipoCombo.EMPADA).recheio(base).acompanhamento(molho).montar());
            itens.addAll(new PersonalizacaoService(new Random(123)).sortearCaixa(Tipo.FOME_LEVE,
                    Set.of(Restricao.SEM_QUEIJO,Restricao.SO_ASSADOS),List.of(base,molho),PersonalizacaoService.quantidades(itens)));
            Pedido pedido=pedido(usuario,itens);
            var esperado=vendas.simular(pedido,null);
            assertEquals(new BigDecimal("78.00"),esperado.total());

            base.setFichaVitrine(new FichaVitrine(TipoCombo.EMPADA,"Carne de Sol",true,false,true));
            ConnectionFactory.executarEmTransacao(c->dao.atualizar(c,base));
            assertThrows(IllegalArgumentException.class,()->vendas.finalizar(pedido,null,esperado));
            assertNull(pedido.getId());
            assertEquals(new BigDecimal("12.000"),saldo(dao,base.getId()));
            base.setFichaVitrine(ficha);
            ConnectionFactory.executarEmTransacao(c->dao.atualizar(c,base));

            long id=vendas.finalizar(pedido,null,esperado);
            assertEquals(new BigDecimal("1.000"),saldo(dao,base.getId()));
            List<String> preparo=vendas.listarFila().stream().filter(p->p.id()==id).findFirst().orElseThrow().itens().lines().toList();
            assertEquals(2,preparo.size());
            assertTrue(preparo.get(0).contains("Carne de Sol")&&preparo.get(0).contains(molho.getNome()));
            assertTrue(preparo.get(1).contains("Sem queijo")&&preparo.get(1).contains("10 × "+base.getNome()));
            Pedido historico=vendas.listarDoCliente(usuario).get(0);
            assertEquals(2,PersonalizacaoService.grupos(historico.getItens()).size());
            assertEquals(new BigDecimal("78.00000"),historico.calcularTotal());
            assertEquals(itens.get(2).getPersonalizacao(),historico.getItens().get(2).getPersonalizacao());
            assertThrows(EstoqueInsuficienteException.class,()->vendas.finalizar(pedido(usuario,itens)));
            vendas.transicionar(id,StatusPedido.CANCELADO);
            assertEquals(new BigDecimal("12.000"),saldo(dao,base.getId()));
            assertEquals(new BigDecimal("2.000"),saldo(dao,molho.getId()));

            long entregue=vendas.finalizar(pedido(usuario,itens),null,esperado);
            for(StatusPedido status:List.of(StatusPedido.PREPARANDO,StatusPedido.PRONTO,StatusPedido.ENTREGUE))
                vendas.transicionar(entregue,status);
            assertThrows(EstoqueInsuficienteException.class,()->vendas.repetirUltimoPedido(usuario));
            ConnectionFactory.executarEmTransacao(c->dao.atualizarEstoque(c,base.getId(),new BigDecimal("12")));
            List<PedidoItem> repetidos=vendas.repetirUltimoPedido(usuario);
            assertEquals(2,PersonalizacaoService.grupos(repetidos).size());
            assertEquals(esperado.total(),vendas.simular(pedido(usuario,repetidos),null).total());
        } finally {
            ConnectionFactory.executarEmTransacao(c->{
                excluir(c,"DELETE i FROM pedido_itens i JOIN pedidos p ON p.id=i.pedido_id WHERE p.usuario_id=?",usuario.getId());
                excluir(c,"DELETE FROM pedidos WHERE usuario_id=?",usuario.getId());
                for(Produto produto:List.of(base,molho))if(produto.getId()!=null){
                    excluir(c,"DELETE FROM movimentacoes_estoque WHERE produto_id=?",produto.getId());
                    excluir(c,"DELETE FROM produtos WHERE id=?",produto.getId());
                }
                excluir(c,"DELETE FROM usuarios WHERE id=?",usuario.getId());
                excluir(c,"DELETE FROM contatos WHERE id=?",usuario.getCliente().getId());return null;
            });
        }
    }

    private static BigDecimal saldo(ProdutoDAOImpl dao,long id) {
        return ConnectionFactory.executarEmTransacao(c->dao.buscarPorId(c,id,false).orElseThrow().getEstoqueAtual());
    }
    private static Pedido pedido(UsuarioCliente usuario,List<PedidoItem> itens) {
        Pedido pedido=new Pedido(null,usuario,usuario.getCliente(),StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,LocalDateTime.now(),itens);
        pedido.setFormaPagamento(FormaPagamento.DINHEIRO);return pedido;
    }
    private static void excluir(Connection c,String sql,long id) throws SQLException {
        try(var ps=c.prepareStatement(sql)){ps.setLong(1,id);ps.executeUpdate();}
    }
}
