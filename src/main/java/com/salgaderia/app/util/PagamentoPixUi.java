package com.salgaderia.app.util;

import com.salgaderia.app.service.pix.PixPagamentoService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.function.Supplier;

/** Diálogo que consulta o serviço a cada três segundos, sem bloquear a thread JavaFX. */
public final class PagamentoPixUi {
    private PagamentoPixUi(){ }

    public static boolean abrir(Window dona,long pedidoId,Supplier<PixPagamentoService.Consulta> consultar,
                                 Supplier<PixPagamentoService.Consulta> confirmarOffline){
        var dialog=new Dialog<ButtonType>();dialog.initOwner(dona);dialog.setTitle("PIX do pedido #"+pedidoId);
        dialog.setHeaderText("Pague apenas uma vez, após conferir o valor e o destinatário no seu banco.");
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.CLOSE);
        var painel=new PixPainel();painel.exibirCodigo(null,"Gerando a cobrança PIX…");
        var consultarAgora=new Button("Consultar agora");
        var offline=new Button("Confirmar Pagamento (Modo Offline)");offline.getStyleClass().add("button-outline");
        var roleta=new Button("Abrir Roleta de Premiação");
        offline.setId("pix-confirmar-offline");roleta.setId("pix-abrir-roleta");
        visivel(offline,false);visivel(roleta,false);
        VBox conteudo=new VBox(10,painel,consultarAgora,offline,roleta);conteudo.setPrefWidth(385);
        dialog.getDialogPane().setContent(conteudo);

        boolean[] desenhando={false},girando={false};
        var timeline=new Timeline();timeline.setCycleCount(Timeline.INDEFINITE);
        java.util.function.Consumer<Supplier<PixPagamentoService.Consulta>> carregar=operacao->{
            if(!dialog.isShowing()||desenhando[0])return;
            desenhando[0]=true;consultarAgora.setDisable(true);offline.setDisable(true);
            Task<PixPagamentoService.Consulta> tarefa=new Task<>() {
                @Override protected PixPagamentoService.Consulta call(){return operacao.get();}
            };
            tarefa.setOnSucceeded(e->{
                desenhando[0]=false;if(!dialog.isShowing())return;
                var valor=tarefa.getValue();
                String moeda=java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("pt-BR")).format(valor.valor());
                painel.exibirCodigo(valor.payload(),moeda+" · "+valor.mensagem());
                boolean terminou=valor.estado()==PixPagamentoService.Estado.PAGO||valor.estado()==PixPagamentoService.Estado.CANCELADO;
                if(terminou||valor.confirmarDemo())timeline.stop();else if(timeline.getStatus()!=javafx.animation.Animation.Status.RUNNING)timeline.play();
                visivel(consultarAgora,!terminou&&!valor.confirmarDemo());
                visivel(offline,valor.confirmarDemo());
                offline.setDisable(false);consultarAgora.setDisable(false);
                visivel(roleta,valor.estado()==PixPagamentoService.Estado.PAGO&&valor.valor().signum()>0);
            });
            tarefa.setOnFailed(e->{
                desenhando[0]=false;if(!dialog.isShowing())return;timeline.stop();
                Throwable falha=tarefa.getException();
                painel.exibirCodigo(null,falha instanceof IllegalArgumentException||
                        falha instanceof com.salgaderia.app.service.pix.PixGatewayService.RespostaInvalida?falha.getMessage():
                        "Não foi possível consultar o PIX. Nenhum pagamento foi confirmado nesta tentativa.");
                visivel(offline,false);visivel(consultarAgora,true);consultarAgora.setDisable(false);
            });
            Thread thread=new Thread(tarefa,"consulta-pagamento-pix");thread.setDaemon(true);thread.start();
        };
        timeline.getKeyFrames().setAll(new KeyFrame(Duration.seconds(3),e->carregar.accept(consultar)));
        consultarAgora.setOnAction(e->carregar.accept(consultar));
        offline.setOnAction(e->carregar.accept(confirmarOffline));
        roleta.setOnAction(e->{girando[0]=true;dialog.close();});
        dialog.setOnShown(e->{timeline.playFromStart();carregar.accept(consultar);});
        dialog.setOnHidden(e->timeline.stop());
        dialog.showAndWait();return girando[0];
    }

    private static void visivel(javafx.scene.Node no,boolean v){no.setManaged(v);no.setVisible(v);}
}
