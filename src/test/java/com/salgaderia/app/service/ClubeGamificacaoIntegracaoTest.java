package com.salgaderia.app.service;

import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.impl.*;
import com.salgaderia.app.util.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/** Testes transacionais da v1.6.0, executados apenas no banco local de desenvolvimento. */
@EnabledIfEnvironmentVariable(named="SALGADERIA_TESTE_MYSQL",matches="true")
class ClubeGamificacaoIntegracaoTest {
    @Test void clubePreservaCotacaoAtivaRenovaCancelaERecalculaCheckout() throws Exception {
        try(var fixture=new Cenario()){
            var admin=new UsuarioAdmin(2L,"Admin","toor","não usada",true);
            var clube=new ClubeServiceImpl();
            var cliente=fixture.cliente();
            String nome="Clube teste "+fixture.token;
            clube.salvarPlano(admin,new PlanoAssinatura(null,nome,"Plano de teste",new BigDecimal("19.99"),new BigDecimal("8.75"),true));
            long planoId=clube.painel(admin).planos().stream().filter(p->p.nome().equals(nome)).findFirst().orElseThrow().id();
            fixture.plano=planoId;
            Pedido pedido=fixture.pedido(cliente,1);
            assertEquals("NENHUM",fixture.vendas.simular(pedido,null).estrategia());
            clube.solicitar(cliente,planoId);
            assertThrows(IllegalArgumentException.class,()->clube.solicitar(cliente,planoId));
            var pendencia=clube.vitrine(cliente).pendencia();
            assertEquals(new BigDecimal("19.99"),pendencia.valor());
            assertEquals("NENHUM",fixture.vendas.simular(pedido,null).estrategia());
            clube.salvarPlano(admin,new PlanoAssinatura(planoId,nome,"Alterado",new BigDecimal("23.99"),new BigDecimal("9.75"),true));
            clube.confirmar(admin,cliente.getId(),pendencia.id());
            assertThrows(IllegalArgumentException.class,()->clube.confirmar(admin,cliente.getId(),pendencia.id()));
            var ativa=clube.vitrine(cliente).assinatura();
            assertTrue(ativa.ativa(LocalDate.now()));assertEquals(new BigDecimal("8.75"),ativa.descontoVigente());
            assertTrue(clube.painel(admin).ativos().stream().anyMatch(a->a.usuarioId()==cliente.getId()));
            assertThrows(IllegalArgumentException.class,()->clube.solicitar(cliente,planoId));
            var desconto=fixture.vendas.simular(pedido,null);
            assertEquals("ASSINANTE",desconto.estrategia());assertEquals(new BigDecimal("0.88"),desconto.desconto());
            assertEquals("CUPOM",fixture.vendas.simular(pedido,"UIKFOMI10").estrategia());

            Clock relogio=Clock.fixed(ativa.validaAte().minusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant(),ZoneId.systemDefault());
            var clubeFuturo=new ClubeServiceImpl(new ClubeDAOImpl(),new UsuarioDAOImpl(new ContatoDAOImpl()),relogio);
            clubeFuturo.solicitar(cliente,planoId);
            var renovacao=clubeFuturo.vitrine(cliente).pendencia();assertEquals(new BigDecimal("23.99"),renovacao.valor());
            clubeFuturo.confirmar(admin,cliente.getId(),renovacao.id());
            assertEquals(ativa.validaAte().plusMonths(1),clubeFuturo.vitrine(cliente).assinatura().validaAte());
            clube.cancelar(cliente);
            assertTrue(clube.vitrine(cliente).assinatura().cancelada());
            assertFalse(clube.painel(admin).ativos().stream().anyMatch(a->a.usuarioId()==cliente.getId()));
            assertEquals("NENHUM",fixture.vendas.simular(pedido,null).estrategia());
            assertThrows(IllegalArgumentException.class,()->fixture.vendas.finalizar(pedido,null,desconto));
        }
    }

    @Test void observerRoletaEPremiosSaoPersistentesExclusivosEIdempotentes() throws Exception {
        try(var fixture=new Cenario()){
            UsuarioCliente cliente=fixture.cliente(),outro=fixture.cliente();
            GamificacaoService jogo=jogo(1);
            long primeiro=fixture.vendas.finalizar(fixture.pedido(cliente,100));
            assertEquals(Set.of("PRIMEIRO_PEDIDO"),selos(jogo,cliente));
            assertThrows(IllegalArgumentException.class,()->jogo.girar(outro,primeiro));
            PremioRoleta premio;
            var executor=Executors.newFixedThreadPool(2);
            try{
                var corrida1=executor.submit(()->jogo.girar(cliente,primeiro));
                var corrida2=executor.submit(()->jogo.girar(cliente,primeiro));
                premio=corrida1.get(10,TimeUnit.SECONDS);var repetido=corrida2.get(10,TimeUnit.SECONDS);
                assertNotEquals(premio.novo(),repetido.novo());
                assertEquals(premio.cupom(),repetido.cupom());
            }finally{executor.shutdownNow();}
            assertEquals(PremioRoleta.Tipo.CUPOM_7,premio.tipo());
            assertEquals(List.of(premio.cupom()),jogo.perfil(cliente).cupons());
            assertThrows(IllegalArgumentException.class,()->fixture.vendas.simular(fixture.pedido(outro,1),premio.cupom()));
            fixture.entregar(primeiro);
            assertTrue(selos(jogo,cliente).contains("REI_DO_CENTO"));

            Pedido segundo=fixture.pedido(cliente,1);
            var desconto=fixture.vendas.simular(segundo,premio.cupom());
            assertEquals(new BigDecimal("0.70"),desconto.desconto());
            fixture.entregar(fixture.vendas.finalizar(segundo,premio.cupom(),desconto));
            assertThrows(IllegalArgumentException.class,()->fixture.vendas.simular(fixture.pedido(cliente,1),premio.cupom()));
            assertTrue(jogo.perfil(cliente).cupons().isEmpty());
            long terceiro=fixture.vendas.finalizar(fixture.pedido(cliente,1));fixture.entregar(terceiro);
            assertEquals(Set.of("PRIMEIRO_PEDIDO","REI_DO_CENTO","CLIENTE_FIEL"),selos(jogo,cliente));
            var pontos=jogo(2);
            assertEquals(PremioRoleta.Tipo.PONTOS_50,pontos.girar(cliente,terceiro).tipo());
            assertEquals(50,pontos.perfil(cliente).pontos());
            assertEquals(50,pontos.perfil(cliente).pontos());

            long cancelavel=fixture.vendas.finalizar(fixture.pedido(cliente,1));
            String revogado=jogo.girar(cliente,cancelavel).cupom();
            assertTrue(jogo.perfil(cliente).cupons().contains(revogado));
            fixture.vendas.transicionar(cancelavel,StatusPedido.CANCELADO);
            assertTrue(jogo.perfil(cliente).cupons().isEmpty());
            assertThrows(IllegalArgumentException.class,()->jogo.girar(cliente,cancelavel));
        }
    }

    private static GamificacaoService jogo(int indice){
        return new GamificacaoServiceImpl(new GamificacaoDAOImpl(),new UsuarioDAOImpl(new ContatoDAOImpl()),new Random(){
            @Override public int nextInt(int limite){return indice;}
        });
    }
    private static Set<String> selos(GamificacaoService jogo,UsuarioCliente cliente){
        return jogo.perfil(cliente).selos().stream().map(ConquistaCliente::codigo).collect(Collectors.toSet());
    }

    private static final class Cenario implements AutoCloseable {
        final String token=UUID.randomUUID().toString().replace("-","");
        final PedidoVendaService vendas=new PedidoVendaServiceImpl();
        final List<UsuarioCliente> usuarios=new ArrayList<>();
        Long plano;
        final Produto produto;

        Cenario(){
            produto=ConnectionFactory.executarEmTransacao(c->{
                long id;
                try(var ps=c.prepareStatement("INSERT INTO categorias(nome) VALUES (?)",Statement.RETURN_GENERATED_KEYS)){
                    ps.setString(1,"Fritos teste "+token);ps.executeUpdate();
                    try(var chaves=ps.getGeneratedKeys()){assertTrue(chaves.next());id=chaves.getLong(1);}
                }
                var p=new Produto(null,new Categoria(id,"Fritos teste "+token,null),"Produto teste "+token,null,new BigDecimal("10.00"),
                        BigDecimal.ONE,new BigDecimal("200"),BigDecimal.ZERO,null);
                p.setId(new ProdutoDAOImpl().inserir(c,p));return p;
            });
        }
        UsuarioCliente cliente(){
            var u=new AutenticacaoServiceImpl().cadastrar("Cliente teste","g"+usuarios.size()+token,"senha-teste",
                    new Cliente(null,"Cliente teste "+token,null,null,null,null,null));
            usuarios.add(u);return u;
        }
        Pedido pedido(UsuarioCliente u,int quantidade){
            var p=new Pedido(null,u,u.getCliente(),StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,LocalDateTime.now(),
                    List.of(new PedidoItem(null,produto,BigDecimal.valueOf(quantidade),produto.getPrecoVenda())));
            p.setFormaPagamento(FormaPagamento.PIX);return p;
        }
        void entregar(long id){
            for(StatusPedido status:List.of(StatusPedido.PREPARANDO,StatusPedido.PRONTO,StatusPedido.ENTREGUE))vendas.transicionar(id,status);
        }
        @Override public void close(){
            ConnectionFactory.executarEmTransacao(c->{
                for(var u:usuarios){
                    excluir(c,"DELETE i FROM pedido_itens i JOIN pedidos p ON p.id=i.pedido_id WHERE p.usuario_id=?",u.getId());
                    excluir(c,"DELETE FROM pedidos WHERE usuario_id=?",u.getId());
                    excluir(c,"DELETE FROM usuarios WHERE id=?",u.getId());
                    excluir(c,"DELETE FROM contatos WHERE id=?",u.getCliente().getId());
                }
                if(plano!=null)excluir(c,"DELETE FROM planos_assinatura WHERE id=?",plano);
                excluir(c,"DELETE FROM movimentacoes_estoque WHERE produto_id=?",produto.getId());
                excluir(c,"DELETE FROM produtos WHERE id=?",produto.getId());
                excluir(c,"DELETE FROM categorias WHERE id=?",produto.getCategoria().getId());return null;
            });
        }
        private static void excluir(Connection c,String sql,long id)throws SQLException {
            try(var ps=c.prepareStatement(sql)){ps.setLong(1,id);ps.executeUpdate();}
        }
    }
}
