package com.salgaderia.app.service;

import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.exception.ProdutoIndisponivelException;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.PersonalizacaoItem.Tipo;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.impl.AutenticacaoServiceImpl;
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
    @Test void caixaPersisteOrcamentoEFiltrosERevalidaCustoDisponibilidadeECategoria(){
        String token=UUID.randomUUID().toString().replace("-","");
        var dao=new ProdutoDAOImpl();var vendas=VendaTeste.vendas();
        Categoria bebidas=new Categoria(null,"Bebidas "+token,null);
        Produto base=produto("Empada "+token,"13.00","2",null);
        Produto molho=produto("Molho "+token,"4.00","0",null);
        Produto suco=produto("Suco "+token,"5.00","0",bebidas);
        var usuario=new AutenticacaoServiceImpl().cadastrar("caixa"+token+"@example.test","senha-teste");
        try{
            ConnectionFactory.executarEmTransacao(c->{
                bebidas.setId(new CategoriaDAOImpl().inserir(c,bebidas));
                for(Produto produto:List.of(base,molho,suco))produto.setId(dao.inserir(c,produto));
                return null;
            });
            List<PedidoItem> itens=new ArrayList<>(new ComboBuilder().principal(base).acompanhamento(molho).bebida(suco).montar());
            var caixa=new PersonalizacaoService(new Random(123)).sortearCaixa(Tipo.CAIXA_25,List.of(base,molho,suco),Set.of(bebidas.getId()),Set.of(molho.getId()));
            itens.addAll(caixa);
            Pedido pedido=pedido(usuario,itens);
            var esperado=vendas.simular(pedido,null);
            assertEquals(new BigDecimal("44.80"),esperado.total());

            base.setCustoProducao(new BigDecimal("12.51"));
            ConnectionFactory.executarEmTransacao(c->dao.atualizar(c,base));
            assertThrows(IllegalArgumentException.class,()->vendas.finalizar(pedido,null,esperado));
            assertNull(pedido.getId());assertEquals(new BigDecimal("2.000"),saldo(dao,base.getId()));
            base.setCustoProducao(BigDecimal.ONE);base.setCategoria(bebidas);
            ConnectionFactory.executarEmTransacao(c->dao.atualizar(c,base));
            assertThrows(IllegalArgumentException.class,()->vendas.finalizar(pedido(usuario,caixa)));
            base.setCategoria(null);
            ConnectionFactory.executarEmTransacao(c->{dao.atualizar(c,base);dao.alternarDisponibilidade(c,base.getId());return null;});
            assertThrows(ProdutoIndisponivelException.class,()->vendas.finalizar(pedido,null,esperado));
            ConnectionFactory.executarEmTransacao(c->dao.alternarDisponibilidade(c,base.getId()));

            long id=vendas.finalizar(pedido,null,esperado);
            assertEquals(new BigDecimal("0.000"),saldo(dao,base.getId()));
            var preparo=vendas.listarFila().stream().filter(p->p.id()==id).findFirst().orElseThrow().itens();
            assertTrue(preparo.contains("2 × "+base.getNome()));
            var historico=vendas.listarDoCliente(usuario).get(0);
            assertEquals(2,PersonalizacaoService.grupos(historico.getItens()).size());
            assertEquals(0,new BigDecimal("44.80").compareTo(historico.calcularTotal()));
            assertEquals(caixa.get(0).getPersonalizacao(),historico.getItens().get(3).getPersonalizacao());
            vendas.transicionar(id,StatusPedido.CANCELADO);
            assertEquals(new BigDecimal("2.000"),saldo(dao,base.getId()));

            long entregue=vendas.finalizar(pedido(usuario,itens),null,esperado);
            for(StatusPedido status:List.of(StatusPedido.PREPARANDO,StatusPedido.PRONTO,StatusPedido.ENTREGUE))vendas.transicionar(entregue,status);
            var repetidos=vendas.repetirUltimoPedido(usuario);
            assertEquals(2,PersonalizacaoService.grupos(repetidos).size());
            assertEquals(esperado.total(),vendas.simular(pedido(usuario,repetidos),null).total());
        }finally{
            ConnectionFactory.executarEmTransacao(c->{
                excluir(c,"DELETE i FROM pedido_itens i JOIN pedidos p ON p.id=i.pedido_id WHERE p.usuario_id=?",usuario.getId());
                excluir(c,"DELETE FROM pedidos WHERE usuario_id=?",usuario.getId());
                for(Produto produto:List.of(base,molho,suco))if(produto.getId()!=null){
                    excluir(c,"DELETE FROM movimentacoes_estoque WHERE produto_id=?",produto.getId());excluir(c,"DELETE FROM produtos WHERE id=?",produto.getId());
                }
                if(bebidas.getId()!=null)excluir(c,"DELETE FROM categorias WHERE id=?",bebidas.getId());
                excluir(c,"DELETE FROM usuarios WHERE id=?",usuario.getId());
                excluir(c,"DELETE FROM contatos WHERE id=?",usuario.getCliente().getId());return null;
            });
        }
    }
    private static Produto produto(String nome,String preco,String estoque,Categoria categoria){
        return new Produto(null,categoria,nome,null,new BigDecimal(preco),BigDecimal.ONE,new BigDecimal(estoque),BigDecimal.ZERO,null);
    }
    private static BigDecimal saldo(ProdutoDAOImpl dao,long id){
        return ConnectionFactory.executarEmTransacao(c->dao.buscarPorId(c,id,false).orElseThrow().getEstoqueAtual());
    }
    private static Pedido pedido(UsuarioCliente usuario,List<PedidoItem> itens){
        Pedido pedido=new Pedido(null,usuario,usuario.getCliente(),StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,LocalDateTime.now(),itens);
        pedido.setFormaPagamento(FormaPagamento.DINHEIRO);return pedido;
    }
    private static void excluir(Connection c,String sql,long id)throws SQLException{
        try(var ps=c.prepareStatement(sql)){ps.setLong(1,id);ps.executeUpdate();}
    }
}
