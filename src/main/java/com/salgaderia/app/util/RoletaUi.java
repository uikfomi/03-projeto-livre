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

    public static void abrir(Window dona,long pedidoId,Supplier<PremioRoleta> girar) {
        Dialog<ButtonType> dialog=new Dialog<>();dialog.initOwner(dona);dialog.setTitle("Roleta de Premiação");
        dialog.setHeaderText("Pedido #"+pedidoId+" · um prêmio por pedido");
        ButtonType iniciar=new ButtonType("Girar roleta",ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(iniciar,ButtonType.CLOSE);
        Canvas roda=roda();
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
        botao.addEventFilter(ActionEvent.ACTION,e->{
            e.consume();botao.setDisable(true);fechar.setDisable(true);resultado.setText("Consultando o sorteio…");
            Task<PremioRoleta> tarefa=new Task<>(){@Override protected PremioRoleta call(){return girar.get();}};
            tarefa.setOnFailed(f->{
                Throwable erro=tarefa.getException();fechar.setDisable(false);botao.setDisable(false);
                resultado.setText(erro instanceof IllegalArgumentException?erro.getMessage():"Não foi possível consultar o prêmio. Tente novamente.");
            });
            tarefa.setOnSucceeded(s->{
                PremioRoleta premio=tarefa.getValue();
                Runnable revelar=()->{
                    resultado.setText((premio.novo()?"Você ganhou: ":"Seu prêmio foi: ")+premio.tipo().rotulo()
                            +(premio.cupom()==null?"":premio.novo()?" · exclusivo para a próxima compra na vitrine":" · consulte a validade no Clube da Fome"));
                    if(premio.cupom()!=null){cupom.setText(premio.cupom());cupom.setVisible(true);cupom.setManaged(true);}
                    fechar.setDisable(false);
                };
                int alvo=(360-premio.tipo().ordinal()*90)%360;
                if(!premio.novo()){roda.setRotate(alvo);revelar.run();return;}
                resultado.setText("Girando…");
                var giro=new RotateTransition(Duration.seconds(2.7),roda);
                giro.setInterpolator(Interpolator.EASE_OUT);giro.setToAngle(360*5+alvo);
                giro.setOnFinished(f->revelar.run());giro.play();
            });
            Thread thread=new Thread(tarefa,"roleta-premiacao");thread.setDaemon(true);thread.start();
        });
        dialog.showAndWait();
    }

    private static Canvas roda(){
        Canvas roda=new Canvas(240,240);var desenho=roda.getGraphicsContext2D();
        Color[] cores={Color.web("#9b431b"),Color.web("#38643c"),Color.web("#7b3f75"),Color.web("#244e70")};
        for(var tipo:PremioRoleta.Tipo.values()){
            int setor=tipo.ordinal();desenho.setFill(cores[setor]);
            desenho.fillArc(6,6,228,228,45-setor*90,90,ArcType.ROUND);
            double angulo=Math.toRadians(setor*90);
            desenho.setFill(Color.WHITE);desenho.setFont(Font.font(14));desenho.setTextAlign(TextAlignment.CENTER);
            desenho.fillText(tipo.rotulo(),120+76*Math.sin(angulo),125-76*Math.cos(angulo));
        }
        desenho.setFill(Color.web("#fff4d8"));desenho.fillOval(108,108,24,24);return roda;
    }
}
