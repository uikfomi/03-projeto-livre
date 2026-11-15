package com.salgaderia.app.controller;

import com.salgaderia.app.model.Usuario;
import com.salgaderia.app.model.enums.PerfilUsuario;
import com.salgaderia.app.util.Navegacao;
import com.salgaderia.app.util.TelaAutenticada;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ClienteVitrineController implements TelaAutenticada {
    @FXML private Label saudacao;
    @Override public void definirUsuario(Usuario usuario) {
        if (usuario == null || usuario.getPerfil() != PerfilUsuario.CLIENTE) throw new SecurityException("Acesso restrito a clientes");
        saudacao.setText("Olá, " + usuario.getNome());
    }
    @FXML private void sair() {
        try { Navegacao.abrirLogin((Stage) saudacao.getScene().getWindow()); }
        catch (IOException e) { Alert alerta = new Alert(Alert.AlertType.ERROR, "Não foi possível voltar ao login."); alerta.showAndWait(); }
    }
}
