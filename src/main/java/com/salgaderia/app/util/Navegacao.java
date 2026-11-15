package com.salgaderia.app.util;

import com.salgaderia.app.model.Usuario;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Navegacao {
    private Navegacao() { }

    public static void abrirLogin(Stage stage) throws IOException {
        abrir(stage, "/fxml/login.fxml", null);
    }

    public static void abrirTelaInicial(Stage stage, Usuario usuario) throws IOException {
        Objects.requireNonNull(usuario, "usuário");
        abrir(stage, usuario.getTelaInicialFxml(), usuario);
    }

    public static void abrirAdmin(Stage stage, Usuario usuario) throws IOException {
        Objects.requireNonNull(usuario, "usuário");
        if (!usuario.podeAcessarPainelGerencial()) throw new SecurityException("Acesso gerencial restrito");
        abrir(stage, "/fxml/admin-dashboard.fxml", usuario);
    }

    public static void abrirAgenda(Stage stage, Usuario usuario) throws IOException {
        Objects.requireNonNull(usuario, "usuário");
        if (!usuario.podeAcessarPainelGerencial()) throw new SecurityException("Acesso gerencial restrito");
        abrir(stage, "/fxml/agenda-contatos.fxml", usuario);
    }

    private static void abrir(Stage stage, String caminho, Usuario usuario) throws IOException {
        URL recurso = Navegacao.class.getResource(caminho);
        if (recurso == null) throw new IOException("Tela não encontrada: " + caminho);
        FXMLLoader loader = new FXMLLoader(recurso);
        Parent raiz = loader.load();
        if (usuario != null) {
            if (!(loader.getController() instanceof TelaAutenticada controller)) {
                throw new IOException("Tela não aceita usuário autenticado: " + caminho);
            }
            controller.definirUsuario(usuario);
        }
        Scene cena = new Scene(raiz, 1000, 700);
        URL css = Navegacao.class.getResource("/css/global.css");
        if (css == null) throw new IOException("Estilos da aplicação não encontrados");
        cena.getStylesheets().add(css.toExternalForm());
        stage.setScene(cena);
    }
}
