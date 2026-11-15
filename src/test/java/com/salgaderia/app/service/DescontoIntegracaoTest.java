package com.salgaderia.app.service;

import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.impl.AutenticacaoServiceImpl;
import com.salgaderia.app.service.impl.PedidoVendaServiceImpl;
import com.salgaderia.app.service.impl.RelatorioFinanceiroServiceImpl;
import com.salgaderia.app.util.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named="SALGADERIA_TESTE_MYSQL",matches="true")
class DescontoIntegracaoTest {
    @Test void aplicaMelhorDescontoERegistraTotalETrocoNoBanco() throws Exception {
        String token=UUID.randomUUID().toString().replace("-","");
        List<Long> pedidosCriados=new ArrayList<>();
        UsuarioCliente usuario=null;long categoriaId=0,produtoId=0;
        try {
            usuario=new AutenticacaoServiceImpl().cadastrar("Cliente teste", "d"+token, "senha-teste",
                    new Cliente(null,"Cliente teste",null,null,null,null,null));
            try(Connection c=ConnectionFactory.getConnection()) {
                categoriaId=inserir(c,"INSERT INTO categorias(nome) VALUES (?)","Fritos teste "+token);
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO produtos(categoria_id,nome,preco_venda,custo_producao,estoque_atual,estoque_minimo) VALUES (?,?,2.00,1.00,1000,0)",Statement.RETURN_GENERATED_KEYS)){
                    ps.setLong(1,categoriaId);ps.setString(2,"Produto teste "+token);ps.executeUpdate();
                    try(ResultSet rs=ps.getGeneratedKeys()){assertTrue(rs.next());produtoId=rs.getLong(1);}
                }
            }
            Categoria categoria=new Categoria(categoriaId,"Fritos teste "+token,null);
            Produto produto=new Produto(produtoId,categoria,"Produto teste",null,new BigDecimal("2.00"),BigDecimal.ONE,new BigDecimal("1000"),BigDecimal.ZERO,null);
            PedidoVendaServiceImpl servico=new PedidoVendaServiceImpl();
            for(int i=0;i<3;i++){
                Pedido anterior=pedido(usuario,usuario.getCliente(),produto,1);
                anterior.setFormaPagamento(new FormaPagamento[]{FormaPagamento.PIX,FormaPagamento.DINHEIRO,FormaPagamento.CARTAO}[i]);
                long id=servico.finalizar(anterior);pedidosCriados.add(id);
                servico.transicionar(id,StatusPedido.PREPARANDO);
                servico.transicionar(id,StatusPedido.PRONTO);
                servico.transicionar(id,StatusPedido.ENTREGUE);
            }
            Pedido compra=pedido(usuario,usuario.getCliente(),produto,100);
            assertThrows(IllegalArgumentException.class,()->servico.finalizar(compra));
            compra.setFormaPagamento(FormaPagamento.CARTAO);
            var semCupom=servico.simular(compra,null);
            assertEquals("CENTO",semCupom.estrategia());assertEquals(0,new BigDecimal("24.00").compareTo(semCupom.desconto()));
            var comCupom=servico.simular(compra,"festa15");
            assertEquals("CUPOM",comCupom.estrategia());assertEquals(0,new BigDecimal("30.00").compareTo(comCupom.desconto()));
            assertThrows(IllegalArgumentException.class,()->servico.simular(compra,"INEXISTENTE"));
            assertThrows(IllegalArgumentException.class,()->servico.finalizar(compra,null,comCupom));
            long id=servico.finalizar(compra,"FESTA15",comCupom);pedidosCriados.add(id);
            UsuarioCliente clienteFinal=usuario;
            Pedido recuperado=servico.listarDoCliente(clienteFinal).stream().filter(p->p.getId()==id).findFirst().orElseThrow();
            assertEquals(0,new BigDecimal("170.00").compareTo(recuperado.calcularTotal()));
            assertEquals(FormaPagamento.CARTAO,recuperado.getFormaPagamento());
            var historico=servico.listarDoCliente(clienteFinal);
            for(int i=0;i<3;i++) {
                final long pedidoId=pedidosCriados.get(i);
                assertEquals(new FormaPagamento[]{FormaPagamento.PIX,FormaPagamento.DINHEIRO,FormaPagamento.CARTAO}[i],
                        historico.stream().filter(p->p.getId()==pedidoId).findFirst().orElseThrow().getFormaPagamento());
            }
            try(Connection c=ConnectionFactory.getConnection();PreparedStatement ps=c.prepareStatement("SELECT desconto,estrategia_desconto,cupom_codigo FROM pedidos WHERE id=?")) {
                ps.setLong(1,id);try(ResultSet rs=ps.executeQuery()){assertTrue(rs.next());assertEquals(0,new BigDecimal("30.00").compareTo(rs.getBigDecimal(1)));assertEquals("CUPOM",rs.getString(2));assertEquals("FESTA15",rs.getString(3));}
            }
            long adminId;
            try(Connection c=ConnectionFactory.getConnection();Statement st=c.createStatement();ResultSet rs=st.executeQuery("SELECT id FROM usuarios WHERE perfil='ADMIN' AND ativo=TRUE ORDER BY id LIMIT 1")){
                assertTrue(rs.next(),"É necessário um administrador ativo para testar o PDV");adminId=rs.getLong(1);
            }
            Usuario admin=new UsuarioAdmin(adminId,"Admin teste","admin-teste","não utilizada",true);
            Pedido pdv=pedido(admin,usuario.getCliente(),produto,100);
            var calculoPdv=servico.simular(pdv,"UIKFOMI10");
            assertEquals("CENTO",calculoPdv.estrategia());
            var financeiro=new RelatorioFinanceiroServiceImpl();
            LocalDateTime inicio=LocalDateTime.now().minusDays(1),fim=LocalDateTime.now().plusDays(1);
            var antes=financeiro.calcular(inicio,fim);
            long idPdv=servico.finalizarPdv(pdv,FormaPagamento.DINHEIRO,new BigDecimal("200.00"),"UIKFOMI10",calculoPdv);pedidosCriados.add(idPdv);
            try(Connection c=ConnectionFactory.getConnection();PreparedStatement ps=c.prepareStatement("SELECT desconto,troco,estrategia_desconto FROM pedidos WHERE id=?")) {
                ps.setLong(1,idPdv);try(ResultSet rs=ps.executeQuery()){assertTrue(rs.next());assertEquals(0,new BigDecimal("24.00").compareTo(rs.getBigDecimal(1)));assertEquals(0,new BigDecimal("24.00").compareTo(rs.getBigDecimal(2)));assertEquals("CENTO",rs.getString(3));}
            }
            var depois=financeiro.calcular(inicio,fim);
            assertEquals(0,new BigDecimal("200.00").compareTo(depois.faturamentoBruto().subtract(antes.faturamentoBruto())));
            assertEquals(0,new BigDecimal("176.00").compareTo(depois.faturamentoAposDescontos().subtract(antes.faturamentoAposDescontos())));
            assertEquals(0,new BigDecimal("76.00").compareTo(depois.lucroLiquidoReal().subtract(antes.lucroLiquidoReal())));
        } finally {
            try(Connection c=ConnectionFactory.getConnection()){
                if(produtoId!=0)excluir(c,"DELETE FROM movimentacoes_estoque WHERE produto_id=?",produtoId);
                for(long id:pedidosCriados){excluir(c,"DELETE FROM pedido_itens WHERE pedido_id=?",id);excluir(c,"DELETE FROM pedidos WHERE id=?",id);}
                if(produtoId!=0)excluir(c,"DELETE FROM produtos WHERE id=?",produtoId);
                if(categoriaId!=0)excluir(c,"DELETE FROM categorias WHERE id=?",categoriaId);
                if(usuario!=null){excluir(c,"DELETE FROM usuarios WHERE id=?",usuario.getId());excluir(c,"DELETE FROM contatos WHERE id=?",usuario.getCliente().getId());}
            }
        }
    }
    private static Pedido pedido(Usuario usuario,Cliente cliente,Produto produto,int quantidade){
        return new Pedido(null,usuario,cliente,StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,LocalDateTime.now(),
                List.of(new PedidoItem(null,produto,BigDecimal.valueOf(quantidade),produto.getPrecoVenda())));
    }
    private static long inserir(Connection c,String sql,String valor) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){ps.setString(1,valor);ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){if(!rs.next())throw new SQLException("ID ausente");return rs.getLong(1);}}
    }
    private static void excluir(Connection c,String sql,long id) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,id);ps.executeUpdate();}
    }
}
