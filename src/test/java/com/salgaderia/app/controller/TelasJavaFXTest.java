package com.salgaderia.app.controller;

import com.salgaderia.app.model.*;
import com.salgaderia.app.util.Navegacao;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/** Teste opcional: exige um servidor gráfico disponível. */
@EnabledIfEnvironmentVariable(named="SALGADERIA_TESTE_JAVAFX", matches="true")
class TelasJavaFXTest {
    @BeforeAll static void iniciarJavaFX() throws Exception {
        CountDownLatch iniciado = new CountDownLatch(1);
        try { Platform.startup(() -> { Platform.setImplicitExit(false); iniciado.countDown(); }); }
        catch (IllegalStateException jaIniciado) { Platform.runLater(iniciado::countDown); }
        assertTrue(iniciado.await(15, TimeUnit.SECONDS));
    }
    @Test void carregaTelasRoteiaPorPerfilEMostraSenha() throws Exception {
        FutureTask<Void> teste = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent login = loader.load();
            assertNotNull(login);
            PasswordField senha = (PasswordField) loader.getNamespace().get("senha");
            TextField visivel = (TextField) loader.getNamespace().get("senhaVisivel");
            CheckBox mostrar = (CheckBox) loader.getNamespace().get("mostrarSenha");
            senha.setText("abc"); assertFalse(visivel.isVisible());
            mostrar.setSelected(true); assertTrue(visivel.isVisible()); assertFalse(senha.isManaged());
            assertEquals("abc", visivel.getText()); visivel.setText("nova"); assertEquals("nova", senha.getText());
            Stage stage = new Stage();
            try {
                Navegacao.abrirLogin(stage); stage.show(); stage.getScene().getRoot().applyCss();
                Cliente cliente = new Cliente(null,"Cliente",null,null,null,null,null);
                Navegacao.abrirTelaInicial(stage, new UsuarioCliente(null,"Cliente","login","senha",true,cliente));
                stage.getScene().getRoot().applyCss();
                assertEquals("Vitrine de pedidos", ((javafx.scene.control.Label) stage.getScene().getRoot().lookup(".vitrine-titulo")).getText());
                Navegacao.abrirTelaInicial(stage, new UsuarioAdmin(null,"Admin","login","senha",true));
                stage.getScene().getRoot().applyCss();
                assertNotNull(stage.getScene().getRoot().lookup(".module-title"));
            } finally { stage.close(); }
            return null;
        });
        Platform.runLater(teste);
        teste.get(20, TimeUnit.SECONDS);
    }
}
