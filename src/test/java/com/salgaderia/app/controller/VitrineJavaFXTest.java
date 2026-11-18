package com.salgaderia.app.controller;

import com.salgaderia.app.model.*;
import com.salgaderia.app.model.FichaVitrine.TipoCombo;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.*;
import javafx.application.Platform;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import javax.imageio.ImageIO;
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
                assertEquals(Set.of("Disponível"),root.lookupAll(".estoque-disponivel").stream().map(n->((Label)n).getText()).collect(java.util.stream.Collectors.toSet()));
                assertEquals("Esgotado",((Label)root.lookup(".estoque-esgotado")).getText());
                assertNotNull(root.lookup("#montarCombo"));assertNotNull(root.lookup("#caixaMisteriosa"));
                assertTrue(root.lookupAll(".produto-estrelas").stream().anyMatch(n->((Label)n).getText().contains("4,5")));
                ((Button)root.lookup("#adicionar-1")).fire();((Button)root.lookup("#adicionar-1")).fire();
                assertEquals("2 itens",((Label)loader.getNamespace().get("contador")).getText());
                assertTrue(((Label)loader.getNamespace().get("total")).getText().contains("16,00"));
                ((RadioButton)loader.getNamespace().get("entrega")).fire();assertEquals("Rua das Flores",((Label)loader.getNamespace().get("endereco")).getText());
                ((TextField)loader.getNamespace().get("busca")).setText("FRANGO");return null;
            });
            assertEquals(1,fx(()->((FlowPane)loader.getNamespace().get("cards")).getChildren().size()));
            fx(()->{((TextField)loader.getNamespace().get("cupom")).setText("UIKFOMI10");return null;});
            aguardar(()->fx(()->((Label)loader.getNamespace().get("total")).getText().contains("14,40")));
            fx(()->{
                assertTrue(((Button)loader.getNamespace().get("finalizar")).isDisabled());
                @SuppressWarnings("unchecked") ComboBox<FormaPagamento> pagamento=(ComboBox<FormaPagamento>)loader.getNamespace().get("formaPagamento");
                assertEquals(List.of(FormaPagamento.PIX,FormaPagamento.DINHEIRO,FormaPagamento.CARTAO),List.copyOf(pagamento.getItems()));
                pagamento.setValue(FormaPagamento.CARTAO);assertFalse(((Button)loader.getNamespace().get("finalizar")).isDisabled());return null;
            });
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
            assertEquals(FormaPagamento.CARTAO,pedidos.enviado.get().getFormaPagamento());
            assertEquals("UIKFOMI10",pedidos.codigo.get());
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
    @Test void meusPedidosAtualizaStatusAutomaticamente() throws Exception {
        Categoria categoria=new Categoria(1L,"Fritos",null);
        Produto produto=new Produto(1L,categoria,"Coxinha",null,BigDecimal.TEN,BigDecimal.ONE,BigDecimal.TEN,BigDecimal.ONE,null);
        Cliente cliente=new Cliente(10L,"Ana",null,null,"85999999999",null,null);
        UsuarioCliente usuario=new UsuarioCliente(20L,"Ana","ana","senha",true,cliente);
        List<Pedido> lista=new CopyOnWriteArrayList<>();
        lista.add(new Pedido(101L,usuario,cliente,StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,LocalDateTime.now(),List.of(new PedidoItem(null,produto,BigDecimal.ONE,BigDecimal.TEN))));
        var loaderRef=new java.util.concurrent.atomic.AtomicReference<FXMLLoader>();var stageRef=new java.util.concurrent.atomic.AtomicReference<Stage>();
        fx(()->{FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/cliente-vitrine.fxml"));loader.setControllerFactory(c->new ClienteVitrineController(new ProdutosTeste(List.of()),new PedidosTeste(lista),new AvaliacoesTeste()));Parent root=loader.load();Stage stage=new Stage();stage.setScene(new Scene(root,1000,700));stage.show();((ClienteVitrineController)loader.getController()).definirUsuario(usuario);loaderRef.set(loader);stageRef.set(stage);((Button)loader.getNamespace().get("botaoPedidos")).fire();return null;});
        try{
            aguardar(()->fx(()->stageRef.get().getScene().getRoot().lookup(".status-pendente")!=null));
            lista.set(0,new Pedido(101L,usuario,cliente,StatusPedido.PRONTO,TipoEntrega.RETIRADA,null,LocalDateTime.now(),List.of(new PedidoItem(null,produto,BigDecimal.ONE,BigDecimal.TEN))));
            aguardar(()->fx(()->stageRef.get().getScene().getRoot().lookup(".status-pronto")!=null));
            assertNotNull(fx(()->stageRef.get().getScene().getRoot().lookup("#whatsapp-pedido-101")));
        }finally{fx(()->{stageRef.get().close();return null;});}
    }
    @Test void repetePedidoEAgendaConsumoLocal() throws Exception {
        Produto produto=new Produto(1L,null,"Coxinha",null,new BigDecimal("8.00"),BigDecimal.ONE,BigDecimal.TEN,BigDecimal.ONE,null);
        Cliente cliente=new Cliente(10L,"Ana",null,null,null,null,null);UsuarioCliente usuario=new UsuarioCliente(20L,"Ana","ana","senha",true,cliente);
        Pedido anterior=new Pedido(101L,usuario,cliente,StatusPedido.ENTREGUE,TipoEntrega.RETIRADA,null,LocalDateTime.now(),List.of(new PedidoItem(null,produto,BigDecimal.ONE,produto.getPrecoVenda())));
        PedidosTeste pedidos=new PedidosTeste(List.of(anterior));var loader=new java.util.concurrent.atomic.AtomicReference<FXMLLoader>();var stage=new java.util.concurrent.atomic.AtomicReference<Stage>();
        fx(()->{FXMLLoader l=new FXMLLoader(getClass().getResource("/fxml/cliente-vitrine.fxml"));l.setControllerFactory(c->new ClienteVitrineController(new ProdutosTeste(List.of(produto)),pedidos,new AvaliacoesTeste()));Parent root=l.load();Stage s=new Stage();s.setScene(new Scene(root,1100,800));s.show();((ClienteVitrineController)l.getController()).definirUsuario(usuario);loader.set(l);stage.set(s);((Button)l.getNamespace().get("pedirDeSempre")).fire();return null;});
        try{
            aguardar(()->fx(()->((Label)loader.get().getNamespace().get("contador")).getText().equals("1 item")));
            LocalDateTime futuro=LocalDateTime.now().plusDays(2).withSecond(0).withNano(0);
            fx(()->{var n=loader.get().getNamespace();((RadioButton)n.get("local")).fire();((CheckBox)n.get("agendar")).setSelected(true);((DatePicker)n.get("dataAgendamento")).setValue(futuro.toLocalDate());((TextField)n.get("horaAgendamento")).setText(futuro.toLocalTime().toString());@SuppressWarnings("unchecked") ComboBox<FormaPagamento> forma=(ComboBox<FormaPagamento>)n.get("formaPagamento");forma.setValue(FormaPagamento.PIX);return null;});
            aguardar(()->fx(()->!((Button)loader.get().getNamespace().get("finalizar")).isDisabled()));
            Timeline timer=fx(()->{Timeline t=new Timeline(new KeyFrame(Duration.millis(50),e->{for(var w:List.copyOf(javafx.stage.Window.getWindows()))if(w instanceof Stage s&&s!=stage.get()&&s.getScene()!=null&&s.getScene().getRoot() instanceof DialogPane pane){var ok=pane.lookupButton(ButtonType.OK);if(ok instanceof Button b)b.fire();}}));t.setCycleCount(Animation.INDEFINITE);t.play();return t;});
            fx(()->{((Button)loader.get().getNamespace().get("finalizar")).fire();return null;});
            aguardar(()->pedidos.enviado.get()!=null);
            assertEquals(TipoEntrega.LOCAL,pedidos.enviado.get().getTipoEntrega());assertEquals(futuro,pedidos.enviado.get().getAgendadoPara());
            fx(()->{timer.stop();return null;});
        }finally{fx(()->{stage.get().close();return null;});}
    }
    @Test void comboRecalculaPrecoCaixaEscondeSorteioEGaleriaExibeFoto(@TempDir Path pasta) throws Exception {
        Produto empada=new Produto(1L,null,"Empada especial",null,new BigDecimal("8.00"),BigDecimal.ONE,new BigDecimal("12"),BigDecimal.ONE,null);
        empada.setFichaVitrine(new FichaVitrine(TipoCombo.EMPADA,"Carne de Sol",false,false,true));
        Produto molho=new Produto(2L,null,"Molho da casa",null,new BigDecimal("2.00"),BigDecimal.ONE,BigDecimal.TEN,BigDecimal.ONE,null);
        molho.setFichaVitrine(new FichaVitrine(TipoCombo.ACOMPANHAMENTO,null,null,null,false));
        Path original=pasta.resolve("foto.png");
        assertTrue(ImageIO.write(new BufferedImage(20,20,BufferedImage.TYPE_INT_RGB),"png",original.toFile()));
        var storage=new FotoAvaliacaoStorage();
        String foto=storage.salvar(original);
        var avaliacoes=new AvaliacoesTeste(){
            public List<AvaliacaoFoto> fotosRecentes(){return List.of(new AvaliacaoFoto(123L,empada.getId(),empada.getNome(),5,foto));}
        };
        var loader=new java.util.concurrent.atomic.AtomicReference<FXMLLoader>();var stage=new java.util.concurrent.atomic.AtomicReference<Stage>();
        try {
            fx(()->{
                FXMLLoader l=new FXMLLoader(getClass().getResource("/fxml/cliente-vitrine.fxml"));
                l.setControllerFactory(c->new ClienteVitrineController(new ProdutosTeste(List.of(empada,molho)),new PedidosTeste(List.of()),avaliacoes));
                Stage s=new Stage();s.setScene(new Scene(l.load(),1100,800));s.show();stage.set(s);loader.set(l);
                Cliente cliente=new Cliente(10L,"Ana",null,null,null,null,null);
                ((ClienteVitrineController)l.getController()).definirUsuario(new UsuarioCliente(20L,"Ana","ana","senha",true,cliente));return null;
            });
            aguardar(()->fx(()->stage.get().getScene().getRoot().lookup("#foto-avaliacao-123")!=null));
            fx(()->{Platform.runLater(()->((Button)loader.get().getNamespace().get("montarCombo")).fire());return null;});
            aguardar(()->fx(()->dialogoAberto(stage.get()).map(p->p.lookup("#combo-previa")!=null).orElse(false)));
            fx(()->{
                DialogPane p=dialogoAberto(stage.get()).orElseThrow();
                selecionar(p,"#combo-massa",0);selecionar(p,"#combo-recheio",0);selecionar(p,"#combo-acompanhamento",1);
                String previa=((Label)p.lookup("#combo-previa")).getText();
                assertTrue(previa.contains("10,00")&&previa.contains("Carne de Sol + Molho da casa"));
                confirmar(p);return null;
            });
            aguardar(()->fx(()->((Label)loader.get().getNamespace().get("total")).getText().contains("10,00")));
            fx(()->{Platform.runLater(()->((Button)loader.get().getNamespace().get("caixaMisteriosa")).fire());return null;});
            aguardar(()->fx(()->dialogoAberto(stage.get()).map(p->p.lookup("#caixa-tamanho")!=null).orElse(false)));
            fx(()->{confirmar(dialogoAberto(stage.get()).orElseThrow());return null;});
            aguardar(()->fx(()->dialogoAberto(stage.get()).map(p->p.getContent() instanceof Label).orElse(false)));
            fx(()->{
                DialogPane p=dialogoAberto(stage.get()).orElseThrow();String previa=((Label)p.getContent()).getText();
                assertTrue(previa.contains("68,00"));assertFalse(previa.contains(empada.getNome()));
                confirmar(p);return null;
            });
            aguardar(()->fx(()->((Label)loader.get().getNamespace().get("total")).getText().contains("78,00")));
        }finally{
            try{fx(()->{if(stage.get()!=null){dialogoAberto(stage.get()).ifPresent(p->p.getScene().getWindow().hide());stage.get().close();}return null;});}
            finally{storage.excluir(foto);}
        }
    }
    private static Optional<DialogPane> dialogoAberto(Stage dona){
        for(var w:List.copyOf(javafx.stage.Window.getWindows()))
            if(w instanceof Stage s&&s.getOwner()==dona&&s.getScene()!=null&&s.getScene().getRoot() instanceof DialogPane pane)return Optional.of(pane);
        return Optional.empty();
    }
    private static void selecionar(DialogPane pane,String seletor,int indice){
        ((ComboBox<?>)pane.lookup(seletor)).getSelectionModel().select(indice);
    }
    private static void confirmar(DialogPane p){
        var tipo=p.getButtonTypes().stream().filter(t->t.getButtonData()==ButtonBar.ButtonData.OK_DONE).findFirst().orElseThrow();
        ((Button)p.lookupButton(tipo)).fire();
    }
    private static <T> T fx(Callable<T> c) throws Exception{FutureTask<T> t=new FutureTask<>(c);Platform.runLater(t);return t.get(15,TimeUnit.SECONDS);}
    private static void aguardar(Callable<Boolean> condicao) throws Exception{long fim=System.nanoTime()+TimeUnit.SECONDS.toNanos(8);while(System.nanoTime()<fim){if(condicao.call())return;Thread.sleep(25);}fail("Operação assíncrona não concluiu");}
    private record ProdutosTeste(List<Produto> dados) implements ProdutoEstoqueService {
        public List<Categoria> listarCategorias(){return List.of();} public List<Produto> listar(){return dados;}public List<Produto> listarEstoqueBaixo(){return List.of();}public Optional<Produto> buscarPorId(long id){return dados.stream().filter(p->p.getId()==id).findFirst();}
        public long cadastrar(Produto p){throw new UnsupportedOperationException();}public boolean atualizar(Produto p){throw new UnsupportedOperationException();}public boolean excluir(long id){throw new UnsupportedOperationException();}
        public void entradaLote(long p,long f,BigDecimal q,String o){throw new UnsupportedOperationException();}public void registrarPerda(long p,BigDecimal q,String o){throw new UnsupportedOperationException();}
    }
    private static class PedidosTeste implements PedidoVendaService {
        public long contarFilaCozinha(){return 0;}
        public List<PedidoItem> repetirUltimoPedido(UsuarioCliente u){return dados.stream().filter(p->p.getStatus()==StatusPedido.ENTREGUE).findFirst().orElseThrow().getItens();}
        final List<Pedido> dados;final java.util.concurrent.atomic.AtomicReference<Pedido> enviado=new java.util.concurrent.atomic.AtomicReference<>();final java.util.concurrent.atomic.AtomicReference<String> codigo=new java.util.concurrent.atomic.AtomicReference<>();
        PedidosTeste(List<Pedido> dados){this.dados=dados;}
        public List<PedidoFila> listarFila(){return List.of();} public long finalizarPdv(Pedido p,FormaPagamento f,BigDecimal r){throw new UnsupportedOperationException();}
        public List<Pedido> listarDoCliente(UsuarioCliente u){return dados;}public long finalizar(Pedido p){enviado.set(p);return 77;}public void transicionar(long id,StatusPedido s){throw new UnsupportedOperationException();}
        public com.salgaderia.app.service.desconto.ResultadoDesconto simular(Pedido p,String c){var cupom="UIKFOMI10".equals(c)?new com.salgaderia.app.service.desconto.Cupom(c,BigDecimal.TEN,BigDecimal.ZERO):null;return new com.salgaderia.app.service.desconto.CalculadoraDescontos().calcular(new com.salgaderia.app.service.desconto.ContextoDesconto(p.getItens(),0,cupom));}
        public long finalizar(Pedido p,String c,com.salgaderia.app.service.desconto.ResultadoDesconto d){codigo.set(c);return finalizar(p);}
        public long finalizarPdv(Pedido p,FormaPagamento f,BigDecimal r,String c,com.salgaderia.app.service.desconto.ResultadoDesconto d){return finalizarPdv(p,f,r);}
    }
    private static class NodeBusca {
        @SuppressWarnings("unchecked") static void preencherNota(DialogPane pane){
            javafx.scene.Node node=pane.lookup(".combo-box");if(node instanceof ComboBox<?> combo)((ComboBox<Integer>)combo).setValue(5);
        }
    }
    private static class AvaliacoesTeste implements AvaliacaoService {
        public List<AvaliacaoResumo> listar(){return List.of();}
        public List<AvaliacaoFoto> fotosRecentes(){return List.of();}
        final java.util.concurrent.atomic.AtomicReference<Avaliacao> salva=new java.util.concurrent.atomic.AtomicReference<>();
        public Map<Long,BigDecimal> mediasPorProduto(){return Map.of(1L,new BigDecimal("4.5"));}public long salvar(Avaliacao a,java.nio.file.Path foto){salva.set(a);return 1;}
        public boolean responder(Usuario u,long id,String r){throw new UnsupportedOperationException();}public boolean excluir(Usuario u,long id){throw new UnsupportedOperationException();}
    }
}
