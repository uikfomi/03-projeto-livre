package com.salgaderia.app.util;

import com.salgaderia.app.model.PremioRoleta;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.function.Supplier;

/** Anima a roleta até o resultado persistido pelo serviço, sem sortear na interface. */
public final class RoletaUi {
    private RoletaUi() { }

    public static void abrir(Window dona,long pedidoId,boolean vip,Supplier<PremioRoleta> girar,Supplier<PremioRoleta> extra) {
        Dialog<ButtonType> dialog=new Dialog<>();dialog.initOwner(dona);dialog.setTitle(vip?"Roleta VIP":"Roleta de Premiação");
        dialog.setHeaderText("Pedido #"+pedidoId+" · um pedido por cliente a cada 24 horas");
        ButtonType iniciar=new ButtonType("Girar roleta",ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(iniciar,ButtonType.CLOSE);
        Canvas roda=new Canvas(288,288);pintar(roda,vip);
        Polygon ponteiro=new Polygon(0,0,20,0,10,18);
        ponteiro.setFill(Color.web("#2a1407"));StackPane.setAlignment(ponteiro,Pos.TOP_CENTER);
        Label resultado=new Label("Gire para descobrir seu prêmio!");resultado.setWrapText(true);
        resultado.setId("roleta-resultado");
        TextField cupom=new TextField();cupom.setEditable(false);cupom.setManaged(false);cupom.setVisible(false);
        VBox caixa=new VBox(12,new StackPane(roda,ponteiro),resultado,cupom);caixa.setAlignment(Pos.CENTER);caixa.setPrefWidth(320);
        dialog.getDialogPane().setContent(caixa);
        var botao=dialog.getDialogPane().lookupButton(iniciar);
        var fechar=dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        dialog.setOnCloseRequest(e->{if(fechar.isDisabled())e.consume();});
        boolean[] segundo={false};
        botao.addEventFilter(ActionEvent.ACTION,e->{
            e.consume();botao.setDisable(true);fechar.setDisable(true);resultado.setText("Consultando o sorteio…");
            boolean giroExtra=segundo[0];
            Task<PremioRoleta> tarefa=new Task<>(){@Override protected PremioRoleta call(){return (giroExtra?extra:girar).get();}};
            tarefa.setOnFailed(f->{
                Throwable erro=tarefa.getException();fechar.setDisable(false);botao.setDisable(false);
                resultado.setText(erro instanceof IllegalArgumentException?erro.getMessage():"Não foi possível consultar o prêmio. Tente novamente.");
            });
            tarefa.setOnSucceeded(s->{
                PremioRoleta premio=tarefa.getValue();
                pintar(roda,premio.vip());
                Runnable revelar=()->{
                    String detalhe=premio.giroExtra()?" · gire mais uma vez, sem ocupar outra vaga de 24h":
                            premio.cupom()==null?(premio.tipo().temCupom()?" · simulação, sem cupom resgatável":""):
                            " · mínimo de R$ 25,00, por até 5 dias. Confira a validade no Clube da Fome.";
                    resultado.setText((premio.tipo()==PremioRoleta.Tipo.SEM_PREMIO?"Tente na próxima! ":
                            premio.novo()?"Você ganhou: ":"Seu prêmio foi: ")+premio.tipo().rotulo()+detalhe);
                    if(premio.cupom()!=null){cupom.setText(premio.cupom());cupom.setVisible(true);cupom.setManaged(true);}
                    fechar.setDisable(false);
                    if(premio.giroExtra()){
                        segundo[0]=true;((Button)botao).setText("Girar de novo");botao.setDisable(false);
                    }
                };
                int setor=PremioRoleta.fatias(premio.vip()).indexOf(premio.tipo());
                int alvo=setor<0?0:(360-setor*60)%360;
                if(!premio.novo()){roda.setRotate(alvo);revelar.run();return;}
                resultado.setText("Girando…");
                var giro=new RotateTransition(Duration.seconds(2.7),roda);
                giro.setInterpolator(Interpolator.EASE_OUT);
                giro.setToAngle((Math.floor(roda.getRotate()/360)+5)*360+alvo);
                giro.setOnFinished(f->revelar.run());giro.play();
            });
            Thread thread=new Thread(tarefa,"roleta-premiacao");thread.setDaemon(true);thread.start();
        });
        dialog.showAndWait();
    }

    private static void pintar(Canvas roda,boolean vip){
        var desenho=roda.getGraphicsContext2D();desenho.clearRect(0,0,288,288);
        Color[] cores={Color.web("#9b431b"),Color.web("#38643c"),Color.web("#7b3f75"),Color.web("#244e70"),
                Color.web("#98491b"),Color.web("#644721")};
        var fatias=PremioRoleta.fatias(vip);
        for(int setor=0;setor<fatias.size();setor++){
            var tipo=fatias.get(setor);desenho.setFill(cores[setor]);
            desenho.fillArc(6,6,276,276,60-setor*60,60,ArcType.ROUND);
            double angulo=Math.toRadians(setor*60);
            desenho.setFill(Color.WHITE);desenho.setFont(Font.font(14));desenho.setTextAlign(TextAlignment.CENTER);
            String rotulo=switch(tipo){
                case BRINDE_MINI->"Mini porção";case COMBO_BRINDE->"Combo VIP";case PONTOS_DOBRO->"2x pontos";
                case CUPOM_10->"10% OFF";case CUPOM_15->"15% OFF";case FRETE_GRATIS->"Frete grátis";
                case GIRO_EXTRA->"Giro extra";case SEM_PREMIO->"Tente depois";default->tipo.rotulo();
            };
            desenho.fillText(rotulo,144+92*Math.sin(angulo),149-92*Math.cos(angulo),85);
        }
        desenho.setFill(Color.web(vip?"#ffcf57":"#fff4d8"));desenho.fillOval(132,132,24,24);
    }
}
