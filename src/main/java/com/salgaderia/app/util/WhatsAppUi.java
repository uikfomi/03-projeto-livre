package com.salgaderia.app.util;

import com.salgaderia.app.service.WhatsAppLinkService;
import java.net.URI;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

/** Feedback e alternativa manual quando o ambiente não oferece navegador ou clipboard. */
public final class WhatsAppUi {
    private WhatsAppUi() { }
    public static void abrir(Window owner, WhatsAppLinkService service, URI uri) {
        var resultado = service.abrir(uri);
        if (resultado == WhatsAppLinkService.ResultadoAbertura.ABERTO) return;
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        if (owner != null) alerta.initOwner(owner);
        alerta.setTitle("WhatsApp"); alerta.setHeaderText(null);
        if (resultado == WhatsAppLinkService.ResultadoAbertura.COPIADO) alerta.setContentText("Link copiado para a área de transferência.");
        else {
            alerta.setContentText("Não foi possível abrir nem copiar o link. Copie-o manualmente:");
            TextArea texto = new TextArea(uri.toASCIIString()); texto.setEditable(false); texto.setWrapText(true);
            alerta.getDialogPane().setExpandableContent(texto); alerta.getDialogPane().setExpanded(true);
        }
        alerta.showAndWait();
    }
}
