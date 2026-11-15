package com.salgaderia.app.controller;

import com.salgaderia.app.dao.ContatoDAO.FiltroContato;
import com.salgaderia.app.dao.impl.ContatoDAOImpl;
import com.salgaderia.app.model.Cliente;
import com.salgaderia.app.model.Contato;
import com.salgaderia.app.model.Fornecedor;
import com.salgaderia.app.model.Usuario;
import com.salgaderia.app.model.enums.CategoriaInsumo;
import com.salgaderia.app.service.AgendaService;
import com.salgaderia.app.service.WhatsAppLinkService;
import com.salgaderia.app.service.impl.AgendaServiceImpl;
import com.salgaderia.app.util.Navegacao;
import com.salgaderia.app.util.TelaAutenticada;
import com.salgaderia.app.util.WhatsAppUi;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Controller de interface: as operações de persistência permanecem no serviço/DAO. */
public class AgendaController implements TelaAutenticada {
    @FXML private BorderPane raiz;
    @FXML private TextField pesquisa;
    @FXML private ToggleButton filtroTodos, filtroClientes, filtroFornecedores;
    @FXML private ListView<Contato> lista;
    @FXML private VBox estado, formulario, camposCliente, camposFornecedor;
    @FXML private Label tituloEstado, descricaoEstado, tituloFormulario;
    @FXML private Button adicionarPrimeiro, tentarNovamente, editar, excluir, chamarWhatsapp;
    @FXML private ComboBox<String> tipo;
    @FXML private TextField nome, whatsapp, endereco, referencia, cnpj, telefone, prazo;
    @FXML private TextArea observacoes;
    @FXML private ComboBox<CategoriaInsumo> categoria;
    private final AgendaService agenda;
    private final ToggleGroup grupoFiltros = new ToggleGroup();
    private final PauseTransition debounce = new PauseTransition(Duration.millis(250));
    private Usuario usuario;
    private Contato emEdicao;
    private final WhatsAppLinkService whatsappService = new WhatsAppLinkService();
    private long versaoConsulta;

    public AgendaController() { this(new AgendaServiceImpl(new ContatoDAOImpl())); }
    public AgendaController(AgendaService agenda) { this.agenda = Objects.requireNonNull(agenda); }

    @FXML private void initialize() {
        filtroTodos.setToggleGroup(grupoFiltros); filtroClientes.setToggleGroup(grupoFiltros); filtroFornecedores.setToggleGroup(grupoFiltros);
        filtroTodos.setSelected(true);
        // Sempre existe um filtro selecionado.
        grupoFiltros.selectedToggleProperty().addListener((obs, anterior, atual) -> {
            if (atual == null) { grupoFiltros.selectToggle(anterior); return; }
            if (usuario != null) agendarConsulta();
        });
        pesquisa.textProperty().addListener((obs, antigo, atual) -> { if (usuario != null) agendarConsulta(); });
        debounce.setOnFinished(e -> carregar());
        tipo.getItems().setAll("Cliente", "Fornecedor");
        tipo.getSelectionModel().selectFirst();
        tipo.valueProperty().addListener((obs, anterior, atual) -> atualizarCampos());
        categoria.getItems().setAll(CategoriaInsumo.values());
        lista.setCellFactory(l -> new ListCell<>() {
            @Override protected void updateItem(Contato contato, boolean vazio) {
                super.updateItem(contato, vazio);
                if (vazio || contato == null) { setText(null); return; }
                if (contato instanceof Cliente c) setText(c.getNome() + "  ·  Cliente" + detalhe(c.getWhatsapp()));
                else if (contato instanceof Fornecedor f) setText(f.getNomeEmpresa() + "  ·  Fornecedor" + detalhe(f.getTelefone()));
            }
            private String detalhe(String valor) { return valor == null || valor.isBlank() ? "" : "\n" + valor; }
        });
        editar.disableProperty().bind(lista.getSelectionModel().selectedItemProperty().isNull().or(lista.visibleProperty().not()));
        excluir.disableProperty().bind(lista.getSelectionModel().selectedItemProperty().isNull().or(lista.visibleProperty().not()));
        chamarWhatsapp.disableProperty().bind(lista.getSelectionModel().selectedItemProperty().isNull().or(lista.visibleProperty().not()));
        mostrarFormulario(false); atualizarCampos();
        mostrarEstado("Carregando contatos...", "Aguarde um instante.", false, false);
    }

    @Override public void definirUsuario(Usuario usuario) {
        if (usuario == null || !usuario.podeAcessarPainelGerencial()) throw new SecurityException("Acesso gerencial restrito");
        this.usuario = usuario;
        carregar();
    }

    private void agendarConsulta() {
        // Invalida imediatamente resultados antigos, inclusive durante o debounce.
        versaoConsulta++;
        debounce.playFromStart();
    }
    private FiltroContato filtroAtual() {
        if (filtroClientes.isSelected()) return FiltroContato.CLIENTES;
        if (filtroFornecedores.isSelected()) return FiltroContato.FORNECEDORES;
        return FiltroContato.TODOS;
    }
    @FXML private void carregar() {
        if (usuario == null) return;
        debounce.stop();
        long versao = ++versaoConsulta;
        FiltroContato filtro = filtroAtual();
        String texto = pesquisa.getText() == null ? "" : pesquisa.getText().trim();
        mostrarEstado("Carregando contatos...", "Aguarde um instante.", false, false);
        Task<List<Contato>> tarefa = new Task<>() {
            @Override protected List<Contato> call() { return agenda.buscar(filtro, texto); }
        };
        tarefa.setOnSucceeded(e -> {
            if (versao != versaoConsulta) return;
            List<Contato> contatos = tarefa.getValue();
            lista.getItems().setAll(contatos);
            if (contatos.isEmpty()) {
                if (filtro == FiltroContato.TODOS && texto.isBlank()) {
                    mostrarEstado("Nenhum contato cadastrado na agenda", "Adicione seu primeiro cliente ou fornecedor.", true, false);
                } else {
                    mostrarEstado("Nenhum contato encontrado", "Experimente outra pesquisa ou outro filtro.", false, false);
                }
            } else {
                estado.setVisible(false); estado.setManaged(false);
                lista.setVisible(true); lista.setManaged(true);
            }
        });
        tarefa.setOnFailed(e -> {
            if (versao != versaoConsulta) return;
            lista.getItems().clear();
            mostrarEstado("Não foi possível carregar a agenda", "Verifique a conexão com o banco e tente novamente.", false, true);
        });
        iniciar(tarefa);
    }
    private void mostrarEstado(String titulo, String descricao, boolean adicionar, boolean repetir) {
        tituloEstado.setText(titulo); descricaoEstado.setText(descricao);
        adicionarPrimeiro.setVisible(adicionar); adicionarPrimeiro.setManaged(adicionar);
        tentarNovamente.setVisible(repetir); tentarNovamente.setManaged(repetir);
        estado.setVisible(true); estado.setManaged(true); lista.setVisible(false); lista.setManaged(false);
    }

    @FXML private void novo() {
        emEdicao = null; limpar(); tipo.setDisable(false); tipo.getSelectionModel().selectFirst();
        tituloFormulario.setText("Novo contato"); mostrarFormulario(true); nome.requestFocus();
    }
    @FXML private void editar() {
        Contato contato = lista.getSelectionModel().getSelectedItem();
        if (contato == null) return;
        emEdicao = contato; limpar();
        if (contato instanceof Cliente c) {
            tipo.setValue("Cliente"); nome.setText(c.getNome()); whatsapp.setText(c.getWhatsapp());
            endereco.setText(c.getEndereco()); referencia.setText(c.getPontoReferencia()); observacoes.setText(c.getObservacoes());
        } else if (contato instanceof Fornecedor f) {
            tipo.setValue("Fornecedor"); nome.setText(f.getNomeEmpresa()); cnpj.setText(f.getCnpj());
            telefone.setText(f.getTelefone()); categoria.setValue(f.getCategoriaInsumo()); prazo.setText(Integer.toString(f.getPrazoEntregaDias()));
        }
        tipo.setDisable(true); tituloFormulario.setText("Editar contato"); mostrarFormulario(true); nome.requestFocus();
    }
    @FXML private void cancelar() { emEdicao = null; mostrarFormulario(false); }
    @FXML private void chamarWhatsapp() {
        Contato contato=lista.getSelectionModel().getSelectedItem();if(contato==null)return;
        String numero=contato instanceof Cliente c && c.getWhatsapp()!=null && !c.getWhatsapp().isBlank()?c.getWhatsapp():contato.getTelefone();
        try { WhatsAppUi.abrir(raiz.getScene().getWindow(),whatsappService,whatsappService.contato(numero,contato.getNome())); }
        catch(IllegalArgumentException e){avisar(Alert.AlertType.WARNING,"Telefone indisponível",e.getMessage());}
    }
    private void mostrarFormulario(boolean mostrar) { formulario.setVisible(mostrar); formulario.setManaged(mostrar); }
    private void atualizarCampos() {
        boolean cliente = "Cliente".equals(tipo.getValue());
        camposCliente.setVisible(cliente); camposCliente.setManaged(cliente);
        camposFornecedor.setVisible(!cliente); camposFornecedor.setManaged(!cliente);
    }
    private void limpar() {
        nome.clear(); whatsapp.clear(); endereco.clear(); referencia.clear(); observacoes.clear();
        cnpj.clear(); telefone.clear(); categoria.getSelectionModel().clearSelection(); prazo.clear();
    }
    @FXML private void salvar() {
        final Contato contato;
        try { contato = montarContato(); }
        catch (IllegalArgumentException | NullPointerException e) {
            avisar(Alert.AlertType.WARNING, "Dados inválidos", "Informe nome, CNPJ com 14 dígitos, categoria e prazo não negativo para fornecedor. Para cliente, informe o nome.");
            return;
        }
        boolean editando = emEdicao != null;
        versaoConsulta++; debounce.stop(); raiz.setDisable(true);
        Task<Boolean> tarefa = new Task<>() {
            @Override protected Boolean call() {
                if (editando) return agenda.atualizar(contato);
                agenda.cadastrar(contato); return true;
            }
        };
        tarefa.setOnSucceeded(e -> {
            raiz.setDisable(false);
            if (!tarefa.getValue()) { avisar(Alert.AlertType.WARNING, "Contato não encontrado", "O contato pode ter sido removido. Atualize a lista."); carregar(); return; }
            cancelar(); carregar();
        });
        tarefa.setOnFailed(e -> { raiz.setDisable(false); avisar(Alert.AlertType.ERROR, "Não foi possível salvar", "Verifique os dados e a conexão com o banco e tente novamente."); carregar(); });
        iniciar(tarefa);
    }
    private Contato montarContato() {
        String nomeInformado = nome.getText();
        if ("Cliente".equals(tipo.getValue())) {
            Cliente anterior = emEdicao instanceof Cliente c ? c : null;
            return new Cliente(anterior == null ? null : anterior.getId(), nomeInformado,
                    anterior == null ? null : anterior.getTelefone(), observacoes.getText(), whatsapp.getText(), endereco.getText(), referencia.getText());
        }
        if (!"Fornecedor".equals(tipo.getValue())) throw new IllegalArgumentException("Tipo obrigatório");
        if (categoria.getValue() == null) throw new IllegalArgumentException("Categoria obrigatória");
        int dias = Integer.parseInt(prazo.getText().trim());
        Fornecedor anterior = emEdicao instanceof Fornecedor f ? f : null;
        String nomeContato = anterior == null ? nomeInformado : anterior.getNome();
        return new Fornecedor(anterior == null ? null : anterior.getId(), nomeContato, telefone.getText(),
                anterior == null ? null : anterior.getObservacoes(), nomeInformado, cnpj.getText(), categoria.getValue(), dias);
    }
    @FXML private void excluir() {
        Contato contato = lista.getSelectionModel().getSelectedItem();
        if (contato == null) return;
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este contato?", ButtonType.CANCEL, ButtonType.OK);
        confirmacao.initOwner(raiz.getScene().getWindow()); confirmacao.setTitle("Excluir contato"); confirmacao.setHeaderText(contato.getNome());
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        versaoConsulta++; debounce.stop(); raiz.setDisable(true);
        Task<Boolean> tarefa = new Task<>() { @Override protected Boolean call() { return agenda.excluir(contato.getId()); } };
        tarefa.setOnSucceeded(e -> { raiz.setDisable(false); if (!tarefa.getValue()) avisar(Alert.AlertType.WARNING, "Contato não encontrado", "O contato já foi removido."); cancelar(); carregar(); });
        tarefa.setOnFailed(e -> { raiz.setDisable(false); avisar(Alert.AlertType.ERROR, "Não foi possível excluir", "Contatos vinculados a usuários ou registros não podem ser excluídos. Verifique também a conexão com o banco."); carregar(); });
        iniciar(tarefa);
    }
    @FXML private void voltar() {
        debounce.stop(); versaoConsulta++;
        try { Navegacao.abrirTelaInicial((Stage) raiz.getScene().getWindow(), usuario); }
        catch (IOException e) { avisar(Alert.AlertType.ERROR, "Não foi possível voltar", "Tente novamente."); }
    }
    private static void iniciar(Task<?> tarefa) { Thread thread = new Thread(tarefa, "agenda-banco"); thread.setDaemon(true); thread.start(); }
    private void avisar(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo); alerta.initOwner(raiz.getScene().getWindow()); alerta.setTitle(titulo); alerta.setHeaderText(null); alerta.setContentText(mensagem); alerta.showAndWait();
    }
}
