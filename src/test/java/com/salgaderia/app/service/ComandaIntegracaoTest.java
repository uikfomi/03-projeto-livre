package com.salgaderia.app.service;

import com.salgaderia.app.model.Usuario;
import com.salgaderia.app.model.UsuarioAdmin;
import com.salgaderia.app.model.comanda.*;
import com.salgaderia.app.model.enums.FormaPagamento;
import com.salgaderia.app.service.impl.ComandaServiceImpl;
import com.salgaderia.app.util.ConnectionFactory;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named="SALGADERIA_TESTE_MYSQL",matches="true")
class ComandaIntegracaoTest {
    @Test void pagamentosParciaisFechamMesaSemDuplicarEstoque() throws Exception {
        var service=new ComandaServiceImpl();
        assertEquals(List.of(new BigDecimal("3.34"),new BigDecimal("3.33"),new BigDecimal("3.33")),service.dividirIgualmente(new BigDecimal("10.00"),3));
        long mesa=service.listarMesas().stream().filter(m->m.status()==StatusMesa.LIVRE).findFirst().orElseThrow(()->new IllegalStateException("É necessária uma mesa livre para o teste")).id();
        long adminId;long produtoId=0,atendimentoId=0,pedidoId=0,novoAtendimento=0;
        try(Connection c=ConnectionFactory.getConnection();Statement st=c.createStatement();ResultSet rs=st.executeQuery("SELECT id FROM usuarios WHERE perfil='ADMIN' AND ativo=TRUE ORDER BY id LIMIT 1")){assertTrue(rs.next());adminId=rs.getLong(1);}
        Usuario admin=new UsuarioAdmin(adminId,"Teste","teste","nao-usada",true);
        try{
            try(Connection c=ConnectionFactory.getConnection();PreparedStatement ps=c.prepareStatement("INSERT INTO produtos(nome,preco_venda,custo_producao,estoque_atual,estoque_minimo) VALUES (?,8,3,10,0)",Statement.RETURN_GENERATED_KEYS)){
                ps.setString(1,"Teste comanda "+UUID.randomUUID());ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){assertTrue(rs.next());produtoId=rs.getLong(1);}
            }
            Comanda aberta=service.abrirOuConsultar(mesa,admin);atendimentoId=aberta.atendimentoId();
            final long sessao=atendimentoId;
            assertTrue(aberta.itens().isEmpty());
            Comanda lancada=service.adicionarItem(mesa,sessao,produtoId,new BigDecimal("3"),admin);
            assertEquals(0,new BigDecimal("24").compareTo(lancada.saldo()));
            long item=lancada.itens().get(0).id();
            assertFalse(service.pagarItem(mesa,sessao,"Pessoa 1",FormaPagamento.PIX,item,BigDecimal.ONE,admin));
            assertThrows(IllegalArgumentException.class,()->service.pagarValor(mesa,sessao,"Pessoa 2",FormaPagamento.DINHEIRO,new BigDecimal("16.01"),admin));
            var inicio=new java.util.concurrent.CountDownLatch(1);
            var pool=java.util.concurrent.Executors.newFixedThreadPool(2);
            try {
                List<java.util.concurrent.Future<Boolean>> tarefas=new ArrayList<>();
                for(int i=0;i<2;i++)tarefas.add(pool.submit(()->{inicio.await();try{service.pagarValor(mesa,sessao,"Pessoa 2",FormaPagamento.DINHEIRO,new BigDecimal("10.00"),admin);return true;}catch(IllegalArgumentException esperado){return false;}}));
                inicio.countDown();int confirmados=0;for(var tarefa:tarefas)if(tarefa.get(15,java.util.concurrent.TimeUnit.SECONDS))confirmados++;
                assertEquals(1,confirmados,"pagamentos concorrentes não podem ultrapassar o saldo");
            } finally { pool.shutdownNow(); }
            assertTrue(service.pagarValor(mesa,sessao,"Pessoa 3",FormaPagamento.DINHEIRO,new BigDecimal("6.00"),admin));
            assertEquals(StatusMesa.LIVRE,service.listarMesas().stream().filter(m->m.id()==mesa).findFirst().orElseThrow().status());
            try(Connection c=ConnectionFactory.getConnection();PreparedStatement ps=c.prepareStatement("SELECT a.pedido_id,p.origem,p.status,p.tipo_entrega FROM comanda_atendimentos a JOIN pedidos p ON p.id=a.pedido_id WHERE a.id=?")){
                ps.setLong(1,atendimentoId);try(ResultSet rs=ps.executeQuery()){assertTrue(rs.next());pedidoId=rs.getLong(1);assertEquals("COMANDA",rs.getString(2));assertEquals("ENTREGUE",rs.getString(3));assertEquals("LOCAL",rs.getString(4));}
            }
            try(Connection c=ConnectionFactory.getConnection();PreparedStatement ps=c.prepareStatement("SELECT estoque_atual FROM produtos WHERE id=?")){ps.setLong(1,produtoId);try(ResultSet rs=ps.executeQuery()){assertTrue(rs.next());assertEquals(0,new BigDecimal("7").compareTo(rs.getBigDecimal(1)));}}
            Comanda reaberta=service.abrirOuConsultar(mesa,admin);novoAtendimento=reaberta.atendimentoId();
            assertNotEquals(sessao,novoAtendimento);
            assertThrows(IllegalStateException.class,()->service.pagarValor(mesa,sessao,"Pessoa antiga",FormaPagamento.PIX,BigDecimal.ONE,admin));
            service.fecharSemSaldo(mesa,novoAtendimento,admin);
        }finally{
            try(Connection c=ConnectionFactory.getConnection()){
                if(novoAtendimento!=0){
                    try(PreparedStatement ps=c.prepareStatement("UPDATE comandas_mesa m JOIN comanda_atendimentos a ON a.mesa_id=m.id SET m.status='LIVRE' WHERE a.id=? AND a.fechado_em IS NULL")){ps.setLong(1,novoAtendimento);ps.executeUpdate();}
                    excluir(c,"DELETE FROM comanda_atendimentos WHERE id=?",novoAtendimento);
                }
                if(atendimentoId!=0){
                    try(PreparedStatement ps=c.prepareStatement("UPDATE comandas_mesa m JOIN comanda_atendimentos a ON a.mesa_id=m.id SET m.status='LIVRE' WHERE a.id=? AND a.fechado_em IS NULL")){ps.setLong(1,atendimentoId);ps.executeUpdate();}
                    excluir(c,"DELETE FROM comanda_pagamentos_parciais WHERE atendimento_id=?",atendimentoId);
                    excluir(c,"DELETE FROM comanda_itens WHERE atendimento_id=?",atendimentoId);
                    excluir(c,"DELETE FROM comanda_atendimentos WHERE id=?",atendimentoId);
                }
                if(pedidoId!=0){excluir(c,"DELETE FROM pedido_itens WHERE pedido_id=?",pedidoId);excluir(c,"DELETE FROM pedidos WHERE id=?",pedidoId);}
                if(produtoId!=0){excluir(c,"DELETE FROM movimentacoes_estoque WHERE produto_id=?",produtoId);excluir(c,"DELETE FROM produtos WHERE id=?",produtoId);}
            }
        }
    }
    private static void excluir(Connection c,String sql,long id)throws SQLException{try(PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,id);ps.executeUpdate();}}
}
