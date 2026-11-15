package com.salgaderia.app.controller;

import com.salgaderia.app.model.Usuario;
import com.salgaderia.app.util.Navegacao;
import com.salgaderia.app.util.TelaAutenticada;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SelecaoModulosController implements TelaAutenticada {
    @FXML private Label saudacao;
    private Usuario usuario;
    @Override public void definirUsuario(Usuario usuario) {
        if (usuario == null || !usuario.podeAcessarPainelGerencial()) throw new SecurityException("Acesso gerencial restrito");
        this.usuario = usuario;
        saudacao.setText("Olá, " + usuario.getNome());
    }
    @FXML private void abrirPdv() {
        try { Navegacao.abrirAdmin((Stage) saudacao.getScene().getWindow(), usuario); }
        catch (IOException e) { Alert alerta = new Alert(Alert.AlertType.ERROR, "Não foi possível abrir o painel."); alerta.showAndWait(); }
    }
    @FXML private void abrirAgenda() {
        try { Navegacao.abrirAgenda((Stage) saudacao.getScene().getWindow(), usuario); }
        catch (IOException e) { Alert alerta = new Alert(Alert.AlertType.ERROR, "Não foi possível abrir a agenda."); alerta.showAndWait(); }
    }
    private void indisponivel(String modulo) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.initOwner(saudacao.getScene().getWindow()); alerta.setTitle(modulo); alerta.setHeaderText(null);
        alerta.setContentText("Este módulo ainda não está disponível."); alerta.showAndWait();
    }
    @FXML private void sair() {
        try { Navegacao.abrirLogin((Stage) saudacao.getScene().getWindow()); }
        catch (IOException e) { Alert alerta = new Alert(Alert.AlertType.ERROR, "Não foi possível voltar ao login."); alerta.showAndWait(); }
    }
}
