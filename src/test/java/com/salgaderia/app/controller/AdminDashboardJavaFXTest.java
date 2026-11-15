package com.salgaderia.app.controller;

import com.salgaderia.app.dao.ContatoDAO.FiltroContato;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named="SALGADERIA_TESTE_JAVAFX",matches="true")
class AdminDashboardJavaFXTest {
    @BeforeAll static void iniciar() throws Exception {
        CountDownLatch l=new CountDownLatch(1);
        try{Platform.startup(()->{Platform.setImplicitExit(false);l.countDown();});}catch(IllegalStateException e){Platform.runLater(l::countDown);}
        assertTrue(l.await(15,TimeUnit.SECONDS));
    }
    @Test void secoesETrocoPdv() throws Exception {
        Produto p=new Produto(1L,null,"Coxinha",null,new BigDecimal("8.00"),new BigDecimal("3.00"),new BigDecimal("4"),BigDecimal.ONE,null);
        Produtos produtos=new Produtos(p);Vendas vendas=new Vendas();
        AtomicReference<FXMLLoader> l=new AtomicReference<>();AtomicReference<Stage> janela=new AtomicReference<>();
        fx(()->{FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/admin-dashboard.fxml"));loader.setControllerFactory(c->new AdminDashboardController(produtos,vendas,new Agenda(),new Financeiro(),new Avaliacoes()));Parent raiz=loader.load();Stage stage=new Stage();stage.setScene(new Scene(raiz,1100,700));stage.show();((AdminDashboardController)loader.getController()).definirUsuario(new UsuarioAdmin(2L,"Admin","admin","senha",true));l.set(loader);janela.set(stage);return null;});
        try {
            aguardar(()->fx(()->janela.get().getScene().getRoot().lookup("#pdv-produto-1")!=null));
            fx(()->{Parent root=janela.get().getScene().getRoot();((Button)root.lookup("#pdv-produto-1")).fire();((TextField)root.lookup("#pdv-cupom")).setText("UIKFOMI10");((TextField)root.lookup("#pdv-recebido")).setText("10,00");return null;});
            aguardar(()->fx(()->((Label)janela.get().getScene().getRoot().lookup("#pdv-troco")).getText().contains("2,80")));
            Timeline timer=fx(()->{Timeline t=new Timeline(new KeyFrame(Duration.millis(50),e->{for(var w:List.copyOf(javafx.stage.Window.getWindows()))if(w instanceof Stage s&&s!=janela.get()&&s.getScene()!=null&&s.getScene().getRoot() instanceof DialogPane d){var ok=d.lookupButton(ButtonType.OK);if(ok instanceof Button b)b.fire();}}));t.setCycleCount(Animation.INDEFINITE);t.play();return t;});
            fx(()->{((Button)janela.get().getScene().getRoot().lookup("#pdv-concluir")).fire();return null;});
            aguardar(()->vendas.enviado.get()!=null);assertEquals(FormaPagamento.DINHEIRO,vendas.forma.get());assertEquals(new BigDecimal("10.00"),vendas.recebido.get());assertEquals("UIKFOMI10",vendas.codigo.get());
            aguardar(()->fx(()->janela.get().getScene().getRoot().lookup("#pdv-produto-1")!=null && !janela.get().getScene().getRoot().isDisabled()));
            fx(()->{timer.stop();((Button)l.get().getNamespace().get("menuFila")).fire();return null;});
            aguardar(()->fx(()->((Label)l.get().getNamespace().get("titulo")).getText().equals("Fila de pedidos") && janela.get().getScene().getRoot().lookup(".admin-empty")!=null));
            fx(()->{((Button)l.get().getNamespace().get("menuEstoque")).fire();return null;});
            aguardar(()->fx(()->janela.get().getScene().getRoot().lookup("#tabela-produtos")!=null));
            fx(()->{((Button)l.get().getNamespace().get("menuFinanceiro")).fire();return null;});
            aguardar(()->fx(()->janela.get().getScene().getRoot().lookup(".chart-bar")!=null));
            fx(()->{((Button)l.get().getNamespace().get("menuModeracao")).fire();return null;});
            aguardar(()->fx(()->janela.get().getScene().getRoot().lookup("#tabela-avaliacoes")!=null));
            fx(()->{((Button)l.get().getNamespace().get("menuVoltar")).fire();return null;});
            assertNotNull(fx(()->janela.get().getScene().getRoot().lookup(".module-title")));
        }finally{fx(()->{janela.get().close();return null;});}
    }
    private static <T>T fx(Callable<T> c)throws Exception{FutureTask<T> t=new FutureTask<>(c);Platform.runLater(t);return t.get(15,TimeUnit.SECONDS);}
    private static void aguardar(Callable<Boolean> c)throws Exception{long fim=System.nanoTime()+TimeUnit.SECONDS.toNanos(8);while(System.nanoTime()<fim){if(c.call())return;Thread.sleep(25);}fail("Operação assíncrona não concluiu");}
    private record Produtos(Produto produto) implements ProdutoEstoqueService {
        public List<Categoria> listarCategorias(){return List.of();}public long cadastrar(Produto p){return 1;}public boolean atualizar(Produto p){return true;}public boolean excluir(long id){return true;}
        public Optional<Produto> buscarPorId(long id){return Optional.of(produto);}public List<Produto> listar(){return List.of(produto);}public List<Produto> listarEstoqueBaixo(){return List.of();}
        public void entradaLote(long p,long f,BigDecimal q,String o){}public void registrarPerda(long p,BigDecimal q,String o){}
    }
    private static class Vendas implements PedidoVendaService {
        final AtomicReference<Pedido> enviado=new AtomicReference<>();final AtomicReference<FormaPagamento> forma=new AtomicReference<>();final AtomicReference<BigDecimal> recebido=new AtomicReference<>();final AtomicReference<String> codigo=new AtomicReference<>();
        public List<PedidoFila> listarFila(){return List.of();}public long finalizarPdv(Pedido p,FormaPagamento f,BigDecimal r){enviado.set(p);forma.set(f);recebido.set(r);return 1;}
        public List<Pedido> listarDoCliente(UsuarioCliente u){return List.of();}public long finalizar(Pedido p){return 1;}public void transicionar(long id,StatusPedido status){}
        public com.salgaderia.app.service.desconto.ResultadoDesconto simular(Pedido p,String c){var cupom="UIKFOMI10".equals(c)?new com.salgaderia.app.service.desconto.Cupom(c,BigDecimal.TEN,BigDecimal.ZERO):null;return new com.salgaderia.app.service.desconto.CalculadoraDescontos().calcular(new com.salgaderia.app.service.desconto.ContextoDesconto(p.getItens(),0,cupom));}
        public long finalizar(Pedido p,String c,com.salgaderia.app.service.desconto.ResultadoDesconto d){return finalizar(p);}
        public long finalizarPdv(Pedido p,FormaPagamento f,BigDecimal r,String c,com.salgaderia.app.service.desconto.ResultadoDesconto d){codigo.set(c);return finalizarPdv(p,f,r);}
    }
    private static class Agenda implements AgendaService {
        public long cadastrar(Contato c){return 1;}public boolean atualizar(Contato c){return true;}public boolean excluir(long id){return true;}
        public Optional<Contato> buscarPorId(long id){return Optional.empty();}public List<Contato> buscar(FiltroContato f,String texto){return List.of();}
    }
    private static class Financeiro implements RelatorioFinanceiroService {
        public Relatorio calcular(LocalDateTime i,LocalDateTime f){return new Relatorio(BigDecimal.TEN,BigDecimal.ONE,BigDecimal.ZERO,new BigDecimal("9"));}
        public Map<String,BigDecimal> produtosMaisVendidos(LocalDateTime i,LocalDateTime f){return Map.of("Coxinha",BigDecimal.ONE);}public Map<String,BigDecimal> receitaPorCategoria(LocalDateTime i,LocalDateTime f){return Map.of("Sem categoria",BigDecimal.TEN);}
    }
    private static class Avaliacoes implements AvaliacaoService {
        public List<AvaliacaoResumo> listar(){return List.of();}public Map<Long,BigDecimal> mediasPorProduto(){return Map.of();}public long salvar(Avaliacao a){return 1;}public boolean responder(Usuario a,long id,String r){return true;}public boolean excluir(Usuario a,long id){return true;}
    }
}
