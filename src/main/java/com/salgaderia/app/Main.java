package com.salgaderia.app;

import com.salgaderia.app.util.ConnectionFactory;
import com.salgaderia.app.util.Navegacao;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Salgaderia Pro");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        Navegacao.abrirLogin(stage);
        stage.show();
    }

    @Override
    public void stop() {
        ConnectionFactory.fechar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
