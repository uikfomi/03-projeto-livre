package com.salgaderia.app.controller;

import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named="SALGADERIA_TESTE_JAVAFX",matches="true")
class VitrineJavaFXTest {
    @BeforeAll static void iniciar() throws Exception {
        CountDownLatch latch=new CountDownLatch(1);
        try { Platform.startup(()->{Platform.setImplicitExit(false);latch.countDown();}); }
        catch(IllegalStateException jaIniciado){Platform.runLater(latch::countDown);}
        assertTrue(latch.await(15,TimeUnit.SECONDS));
    }
    @Test void catalogoCarrinhoFiltrosEntregaEPedidos() throws Exception {
        Categoria categoria=new Categoria(1L,"Assados",null);
        Produto produto=new Produto(1L,categoria,"Coxinha de frango","Frango cremoso",new BigDecimal("8.00"),new BigDecimal("3"),new BigDecimal("2"),BigDecimal.ONE,null);
        Produto semEstoque=new Produto(2L,null,"Pastel",null,new BigDecimal("5"),BigDecimal.ONE,BigDecimal.ZERO,BigDecimal.ONE,null);
        Cliente cliente=new Cliente(10L,"Ana",null,null,"123","Rua das Flores",null);
        UsuarioCliente usuario=new UsuarioCliente(20L,"Ana","ana","senha",true,cliente);
        PedidoItem item=new PedidoItem(1L,produto,BigDecimal.ONE,new BigDecimal("8.00"));
        Pedido pendente=new Pedido(101L,usuario,cliente,StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,LocalDateTime.now(),List.of(item));
        Pedido entregue=new Pedido(102L,usuario,cliente,StatusPedido.ENTREGUE,TipoEntrega.RETIRADA,null,LocalDateTime.now(),List.of(item));
        var estoque=new ProdutosTeste(List.of(produto,semEstoque));var pedidos=new PedidosTeste(List.of(pendente,entregue));var avaliacoes=new AvaliacoesTeste();
        var loaderRef=new java.util.concurrent.atomic.AtomicReference<FXMLLoader>();var stageRef=new java.util.concurrent.atomic.AtomicReference<Stage>();
        fx(()->{
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/cliente-vitrine.fxml"));
            loader.setControllerFactory(c->new ClienteVitrineController(estoque,pedidos,avaliacoes));
            Parent root=loader.load();Stage stage=new Stage();stage.setScene(new Scene(root,1000,700));stage.show();
            root.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());root.applyCss();
            ((ClienteVitrineController)loader.getController()).definirUsuario(usuario);loaderRef.set(loader);stageRef.set(stage);return null;
        });
        try {
            FXMLLoader loader=loaderRef.get();
            aguardar(()->fx(()->((FlowPane)loader.getNamespace().get("cards")).getChildren().size()==2));
            fx(()->{
                Parent root=stageRef.get().getScene().getRoot();root.applyCss();
                assertTrue(((Button)root.lookup("#adicionar-2")).isDisabled());
                assertNotNull(root.lookup(".imagem-fallback"));
                assertTrue(root.lookupAll(".produto-estrelas").stream().anyMatch(n->((Label)n).getText().contains("4,5")));
                ((Button)root.lookup("#adicionar-1")).fire();((Button)root.lookup("#adicionar-1")).fire();
                assertEquals("2 itens",((Label)loader.getNamespace().get("contador")).getText());
                assertTrue(((Label)loader.getNamespace().get("total")).getText().contains("16,00"));
                ((RadioButton)loader.getNamespace().get("entrega")).fire();assertEquals("Rua das Flores",((Label)loader.getNamespace().get("endereco")).getText());
                ((TextField)loader.getNamespace().get("busca")).setText("FRANGO");return null;
            });
            assertEquals(1,fx(()->((FlowPane)loader.getNamespace().get("cards")).getChildren().size()));
            fx(()->{((TextField)loader.getNamespace().get("busca")).setText("nada encontrado");return null;});
            assertTrue(fx(()->((Label)loader.getNamespace().get("mensagemProdutos")).getText().contains("Nenhum salgado encontrado")));
            var timer=fx(()->{
                javafx.animation.Timeline t=new javafx.animation.Timeline(new javafx.animation.KeyFrame(javafx.util.Duration.millis(50),event->{
                    for(javafx.stage.Window janela:List.copyOf(javafx.stage.Window.getWindows())){
                        if(janela instanceof Stage dialogo && dialogo!=stageRef.get() && dialogo.getScene()!=null){
                            Parent raiz=dialogo.getScene().getRoot();
                            if(raiz instanceof DialogPane pane){
                                NodeBusca.preencherNota(pane);
                                javafx.scene.Node ok=pane.lookupButton(ButtonType.OK);
                                if(ok instanceof Button botao && !botao.isDisabled())botao.fire();
                            }
                        }
                    }
                }));t.setCycleCount(javafx.animation.Animation.INDEFINITE);t.play();return t;
            });
            fx(()->{((Button)loader.getNamespace().get("finalizar")).fire();return null;});
            aguardar(()->pedidos.enviado.get()!=null);
            aguardar(()->fx(()->((Label)loader.getNamespace().get("contador")).getText().equals("0 itens")));
            assertEquals(TipoEntrega.ENTREGA,pedidos.enviado.get().getTipoEntrega());
            assertEquals("Rua das Flores",pedidos.enviado.get().getEnderecoEntrega());
            assertEquals(new BigDecimal("2"),pedidos.enviado.get().getItens().get(0).getQuantidade());
            aguardar(()->fx(()->stageRef.get().getScene().getRoot().lookup("#avaliar-102-1")!=null));
            fx(()->{
                Parent root=stageRef.get().getScene().getRoot();
                assertTrue(((Button)root.lookup("#avaliar-101-1")).isDisabled());
                assertFalse(((Button)root.lookup("#avaliar-102-1")).isDisabled());
                ((Button)root.lookup("#avaliar-102-1")).fire();return null;
            });
            aguardar(()->avaliacoes.salva.get()!=null);
            assertEquals(5,avaliacoes.salva.get().getNota());
            fx(()->{timer.stop();return null;});
        } finally {fx(()->{stageRef.get().close();return null;});}
    }
    @Test void catalogoVazioNaoExibeProdutosFicticios() throws Exception {
        var loaderRef=new java.util.concurrent.atomic.AtomicReference<FXMLLoader>();var stageRef=new java.util.concurrent.atomic.AtomicReference<Stage>();
        fx(()->{
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/cliente-vitrine.fxml"));
            loader.setControllerFactory(c->new ClienteVitrineController(new ProdutosTeste(List.of()),new PedidosTeste(List.of()),new AvaliacoesTeste()));
            Parent root=loader.load();Stage stage=new Stage();stage.setScene(new Scene(root,900,650));stage.show();
            Cliente cliente=new Cliente(10L,"Ana",null,null,null,null,null);
            ((ClienteVitrineController)loader.getController()).definirUsuario(new UsuarioCliente(20L,"Ana","ana","senha",true,cliente));
            loaderRef.set(loader);stageRef.set(stage);return null;
        });
        try {
            aguardar(()->fx(()->((Label)loaderRef.get().getNamespace().get("mensagemProdutos")).getText().contains("Nenhum salgado cadastrado")));
            assertEquals(0,fx(()->((FlowPane)loaderRef.get().getNamespace().get("cards")).getChildren().size()));
        } finally {fx(()->{stageRef.get().close();return null;});}
    }
    private static <T> T fx(Callable<T> c) throws Exception{FutureTask<T> t=new FutureTask<>(c);Platform.runLater(t);return t.get(15,TimeUnit.SECONDS);}
    private static void aguardar(Callable<Boolean> condicao) throws Exception{long fim=System.nanoTime()+TimeUnit.SECONDS.toNanos(8);while(System.nanoTime()<fim){if(condicao.call())return;Thread.sleep(25);}fail("Operação assíncrona não concluiu");}
    private record ProdutosTeste(List<Produto> dados) implements ProdutoEstoqueService {
        public List<Produto> listar(){return dados;}public List<Produto> listarEstoqueBaixo(){return List.of();}public Optional<Produto> buscarPorId(long id){return dados.stream().filter(p->p.getId()==id).findFirst();}
        public long cadastrar(Produto p){throw new UnsupportedOperationException();}public boolean atualizar(Produto p){throw new UnsupportedOperationException();}public boolean excluir(long id){throw new UnsupportedOperationException();}
        public void entradaLote(long p,long f,BigDecimal q,String o){throw new UnsupportedOperationException();}public void registrarPerda(long p,BigDecimal q,String o){throw new UnsupportedOperationException();}
    }
    private static class PedidosTeste implements PedidoVendaService {
        final List<Pedido> dados;final java.util.concurrent.atomic.AtomicReference<Pedido> enviado=new java.util.concurrent.atomic.AtomicReference<>();
        PedidosTeste(List<Pedido> dados){this.dados=dados;}
        public List<Pedido> listarDoCliente(UsuarioCliente u){return dados;}public long finalizar(Pedido p){enviado.set(p);return 77;}public void transicionar(long id,StatusPedido s){throw new UnsupportedOperationException();}
    }
    private static class NodeBusca {
        @SuppressWarnings("unchecked") static void preencherNota(DialogPane pane){
            javafx.scene.Node node=pane.lookup(".combo-box");if(node instanceof ComboBox<?> combo)((ComboBox<Integer>)combo).setValue(5);
        }
    }
    private static class AvaliacoesTeste implements AvaliacaoService {
        final java.util.concurrent.atomic.AtomicReference<Avaliacao> salva=new java.util.concurrent.atomic.AtomicReference<>();
        public Map<Long,BigDecimal> mediasPorProduto(){return Map.of(1L,new BigDecimal("4.5"));}public long salvar(Avaliacao a){salva.set(a);return 1;}
        public boolean responder(Usuario u,long id,String r){throw new UnsupportedOperationException();}public boolean excluir(Usuario u,long id){throw new UnsupportedOperationException();}
    }
}
