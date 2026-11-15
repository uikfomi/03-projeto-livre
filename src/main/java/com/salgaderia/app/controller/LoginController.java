package com.salgaderia.app.controller;

import com.salgaderia.app.exception.AutenticacaoInvalidaException;
import com.salgaderia.app.model.Cliente;
import com.salgaderia.app.model.Usuario;
import com.salgaderia.app.service.AutenticacaoService;
import com.salgaderia.app.service.impl.AutenticacaoServiceImpl;
import com.salgaderia.app.util.Navegacao;
import java.io.IOException;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TabPane abas;
    @FXML private TextField login;
    @FXML private PasswordField senha;
    @FXML private TextField senhaVisivel;
    @FXML private CheckBox mostrarSenha;
    @FXML private TextField cadastroNome;
    @FXML private TextField cadastroLogin;
    @FXML private PasswordField cadastroSenha;
    @FXML private TextField cadastroWhatsapp;
    @FXML private TextField cadastroEndereco;
    private final AutenticacaoService autenticacao;

    public LoginController() { this(new AutenticacaoServiceImpl()); }
    public LoginController(AutenticacaoService autenticacao) { this.autenticacao = java.util.Objects.requireNonNull(autenticacao); }

    @FXML private void initialize() {
        senhaVisivel.textProperty().bindBidirectional(senha.textProperty());
        senhaVisivel.visibleProperty().bind(mostrarSenha.selectedProperty());
        senhaVisivel.managedProperty().bind(mostrarSenha.selectedProperty());
        senha.visibleProperty().bind(mostrarSenha.selectedProperty().not());
        senha.managedProperty().bind(mostrarSenha.selectedProperty().not());
    }

    @FXML private void entrar() {
        String nomeLogin = login.getText() == null ? "" : login.getText().trim();
        String senhaInformada = senha.getText();
        if (nomeLogin.isEmpty() || senhaInformada == null || senhaInformada.isBlank()) {
            avisar(Alert.AlertType.WARNING, "Campos obrigatórios", "Informe login e senha para entrar.");
            return;
        }
        executar(new Task<Usuario>() {
            @Override protected Usuario call() { return autenticacao.login(nomeLogin, senhaInformada); }
        }, usuario -> {
            try {
                Navegacao.abrirTelaInicial((Stage) login.getScene().getWindow(), usuario);
            } catch (IOException e) {
                avisar(Alert.AlertType.ERROR, "Não foi possível abrir a tela", "Tente novamente ou contate o suporte.");
            }
        });
    }

    @FXML private void cadastrar() {
        String nome = cadastroNome.getText().trim();
        String novoLogin = cadastroLogin.getText().trim();
        String novaSenha = cadastroSenha.getText();
        String whatsapp = cadastroWhatsapp.getText().trim();
        String endereco = cadastroEndereco.getText().trim();
        if (nome.isEmpty() || novoLogin.isEmpty() || novaSenha == null || novaSenha.isBlank() || whatsapp.isEmpty() || endereco.isEmpty()) {
            avisar(Alert.AlertType.WARNING, "Campos obrigatórios", "Preencha todos os campos para criar sua conta.");
            return;
        }
        executar(new Task<Usuario>() {
            @Override protected Usuario call() {
                Cliente cliente = new Cliente(null, nome, null, null, whatsapp, endereco, null);
                return autenticacao.cadastrar(nome, novoLogin, novaSenha, cliente);
            }
        }, usuario -> {
            cadastroNome.clear(); cadastroLogin.clear(); cadastroSenha.clear(); cadastroWhatsapp.clear(); cadastroEndereco.clear();
            login.setText(novoLogin); senha.clear(); abas.getSelectionModel().select(0);
            avisar(Alert.AlertType.INFORMATION, "Cadastro concluído", "Sua conta de cliente foi criada. Entre com seu login e senha.");
        });
    }

    private <T> void executar(Task<T> tarefa, java.util.function.Consumer<T> sucesso) {
        abas.setDisable(true);
        tarefa.setOnSucceeded(event -> { abas.setDisable(false); sucesso.accept(tarefa.getValue()); });
        tarefa.setOnFailed(event -> {
            abas.setDisable(false);
            Throwable erro = tarefa.getException();
            if (erro instanceof AutenticacaoInvalidaException) {
                avisar(Alert.AlertType.WARNING, "Não foi possível entrar", "Login ou senha inválidos, ou conta inativa.");
            } else if (erro instanceof IllegalArgumentException) {
                avisar(Alert.AlertType.WARNING, "Confira os dados", "Verifique os dados informados e tente novamente.");
            } else {
                avisar(Alert.AlertType.ERROR, "Não foi possível concluir", "Verifique sua conexão ou se o login já está em uso e tente novamente.");
            }
        });
        Thread thread = new Thread(tarefa, "autenticacao");
        thread.setDaemon(true);
        thread.start();
    }

    private void avisar(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.initOwner(login.getScene().getWindow());
        alerta.setTitle(titulo); alerta.setHeaderText(null); alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
