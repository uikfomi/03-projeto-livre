package com.salgaderia.app.util;

import com.salgaderia.app.service.PixPayloadService;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/** Componente reaproveitado no checkout, PDV, comanda e mensalidade. */
public final class PixPainel extends VBox {
    private final ImageView qr = new ImageView();
    private final Label aviso = new Label();
    private final Button copiar = new Button("Copiar Código PIX Copia e Cola");
    private final TextArea manual = new TextArea();
    private PixPayloadService pix;
    private String payload;
    private BigDecimal ultimoValor;
    private String ultimaReferencia;

    public PixPainel() {
        setSpacing(6); setAlignment(Pos.CENTER); getStyleClass().add("pix-painel");
        aviso.setWrapText(true);aviso.setId("pix-aviso");qr.setAccessibleText("QR Code PIX");
        copiar.setId("pix-copiar");
        manual.setEditable(false);manual.setWrapText(true);manual.setPrefRowCount(3);visivel(manual,false);
        copiar.setOnAction(e -> {
            if (payload == null) return;
            try {
                ClipboardContent conteudo = new ClipboardContent();conteudo.putString(payload);
                if (Clipboard.getSystemClipboard().setContent(conteudo)) { copiar.setText("Código PIX copiado"); return; }
            } catch (RuntimeException ignorada) { /* Permite cópia manual. */ }
            manual.setText(payload);visivel(manual,true);manual.selectAll();
        });
        getChildren().setAll(aviso, qr, copiar, manual); visivel(this,false);
    }

    public void atualizar(boolean selecionado, BigDecimal valor, String referencia, String orientacao) {
        if (!selecionado) { visivel(this,false); limpar(); return; }
        visivel(this,true);
        if (valor == null || valor.signum() <= 0) {
            limpar();
            aviso.setText(valor == null ? orientacao : "Nenhum valor a pagar por PIX.");
            return;
        }
        try {
            if (pix == null) pix = PixPayloadService.carregar();
            if (payload == null || ultimoValor==null || ultimoValor.compareTo(valor) != 0 || !Objects.equals(ultimaReferencia, referencia)) {
                limpar();
                String novo = pix.gerarPayload(valor, referencia);
                qr.setImage(pix.gerarImagem(novo, 208)); payload=novo;
                ultimoValor=valor;ultimaReferencia=referencia;
            }
            String total=NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
            aviso.setText((pix.demonstracao()?"PIX de demonstração — não pague. ":"")+"PIX "+total+" · "+orientacao);
            visivel(qr,true);visivel(copiar,true);
        } catch (IllegalArgumentException | IllegalStateException erro) {
            limpar();aviso.setText(erro.getMessage());
        }
    }

    /** Desenha exatamente o Pix Copia e Cola recebido do provedor, sem reconstruir o payload. */
    public void exibirCodigo(String codigo,String orientacao){
        visivel(this,true);aviso.setText(orientacao);
        if(codigo==null||codigo.isBlank()){limpar();return;}
        if(!codigo.equals(payload)){
            limpar();qr.setImage(new PixPayloadService(null,null,null).gerarImagem(codigo,242));payload=codigo;
        }
        visivel(qr,true);visivel(copiar,true);
    }

    private void limpar() {
        payload=null;ultimoValor=null;ultimaReferencia=null;qr.setImage(null);manual.clear();
        copiar.setText("Copiar Código PIX Copia e Cola");
        visivel(qr,false);visivel(copiar,false);visivel(manual,false);
    }

    private static void visivel(javafx.scene.Node no, boolean valor) { no.setManaged(valor);no.setVisible(valor); }
}
