package com.salgaderia.app.controller;

import com.salgaderia.app.dao.ContatoDAO.FiltroContato;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.CategoriaInsumo;
import com.salgaderia.app.service.AgendaService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named="SALGADERIA_TESTE_JAVAFX", matches="true")
class AgendaJavaFXTest {
    @BeforeAll static void iniciar() throws Exception {
        CountDownLatch pronto = new CountDownLatch(1);
        try { Platform.startup(() -> { Platform.setImplicitExit(false); pronto.countDown(); }); }
        catch (IllegalStateException jaIniciado) { Platform.runLater(pronto::countDown); }
        assertTrue(pronto.await(15, TimeUnit.SECONDS));
    }
    @Test void listaVaziaCadastroEdicaoFiltrosEPesquisa() throws Exception {
        AgendaMemoria agenda = new AgendaMemoria();
        AtomicReference<FXMLLoader> loaderRef = new AtomicReference<>();
        AtomicReference<Stage> stageRef = new AtomicReference<>();
        fx(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/agenda-contatos.fxml"));
            loader.setControllerFactory(c -> new AgendaController(agenda));
            Parent root = loader.load();
            Stage stage = new Stage(); stage.setScene(new Scene(root, 1000, 700)); stage.show();
            root.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm()); root.applyCss();
            ((AgendaController) loader.getController()).definirUsuario(new UsuarioAdmin(2L,"Admin","admin","senha",true));
            loaderRef.set(loader); stageRef.set(stage); return null;
        });
        try {
            FXMLLoader loader = loaderRef.get();
            aguardar(() -> fx(() -> ((Label) loader.getNamespace().get("tituloEstado")).getText().equals("Nenhum contato cadastrado na agenda")));
            fx(() -> {
                ((Button) loader.getNamespace().get("adicionarPrimeiro")).fire();
                assertTrue(((VBox) loader.getNamespace().get("camposCliente")).isVisible());
                ((TextField) loader.getNamespace().get("nome")).setText("Maria teste");
                ((TextField) loader.getNamespace().get("whatsapp")).setText("998877");
                ((TextField) loader.getNamespace().get("endereco")).setText("Rua teste");
                ((Button) loader.getNamespace().get("botaoSalvar")).fire(); return null;
            });
            aguardar(() -> fx(() -> ((ListView<?>)loader.getNamespace().get("lista")).getItems().size() == 1));
            fx(() -> {
                @SuppressWarnings("unchecked") ListView<Contato> lista = (ListView<Contato>) loader.getNamespace().get("lista");
                lista.getSelectionModel().select(0); ((Button)loader.getNamespace().get("editar")).fire();
                assertTrue(((ComboBox<?>)loader.getNamespace().get("tipo")).isDisabled());
                ((TextField)loader.getNamespace().get("nome")).setText("Maria editada");
                ((Button)loader.getNamespace().get("botaoSalvar")).fire(); return null;
            });
            aguardar(() -> agenda.dados.stream().anyMatch(c -> c.getNome().equals("Maria editada")));
            fx(() -> {
                ((Button)loader.getNamespace().get("botaoNovo")).fire();
                @SuppressWarnings("unchecked") ComboBox<String> tipo = (ComboBox<String>)loader.getNamespace().get("tipo"); tipo.setValue("Fornecedor");
                assertTrue(((VBox)loader.getNamespace().get("camposFornecedor")).isVisible());
                ((TextField)loader.getNamespace().get("nome")).setText("Empresa teste");
                ((TextField)loader.getNamespace().get("cnpj")).setText("12345678000199");
                ((TextField)loader.getNamespace().get("telefone")).setText("1234");
                @SuppressWarnings("unchecked") ComboBox<CategoriaInsumo> categoria = (ComboBox<CategoriaInsumo>)loader.getNamespace().get("categoria"); categoria.setValue(CategoriaInsumo.FARINHA);
                ((TextField)loader.getNamespace().get("prazo")).setText("2");
                ((Button)loader.getNamespace().get("botaoSalvar")).fire(); return null;
            });
            aguardar(() -> agenda.dados.size() == 2);
            fx(() -> { ((ToggleButton)loader.getNamespace().get("filtroFornecedores")).fire(); return null; });
            aguardar(() -> fx(() -> ((ListView<?>)loader.getNamespace().get("lista")).getItems().size() == 1 && ((ListView<?>)loader.getNamespace().get("lista")).isVisible()));
            assertTrue(fx(() -> ((ListView<?>)loader.getNamespace().get("lista")).getItems().get(0) instanceof Fornecedor));
            fx(() -> { ((TextField)loader.getNamespace().get("pesquisa")).setText("não existe"); return null; });
            aguardar(() -> fx(() -> ((Label)loader.getNamespace().get("tituloEstado")).getText().equals("Nenhum contato encontrado")));
            fx(() -> { ((Button)loader.getNamespace().get("botaoVoltar")).fire(); return null; });
            assertNotNull(fx(() -> stageRef.get().getScene().getRoot().lookup(".module-title")));
        } finally { fx(() -> { stageRef.get().close(); return null; }); }
    }
    private static <T> T fx(Callable<T> callable) throws Exception { FutureTask<T> task = new FutureTask<>(callable); Platform.runLater(task); return task.get(15,TimeUnit.SECONDS); }
    private static void aguardar(Callable<Boolean> condicao) throws Exception { long fim=System.nanoTime()+TimeUnit.SECONDS.toNanos(8); while(System.nanoTime()<fim) { if(condicao.call())return; Thread.sleep(25); } fail("Operação assíncrona não concluiu"); }
    private static class AgendaMemoria implements AgendaService {
        final List<Contato> dados = new CopyOnWriteArrayList<>();
        public long cadastrar(Contato contato) { contato.setId((long)dados.size()+1); dados.add(contato); return contato.getId(); }
        public boolean atualizar(Contato contato) { for(int i=0;i<dados.size();i++)if(dados.get(i).getId().equals(contato.getId())){dados.set(i,contato);return true;}return false; }
        public boolean excluir(long id) { return dados.removeIf(c -> c.getId()==id); }
        public Optional<Contato> buscarPorId(long id) { return dados.stream().filter(c -> c.getId()==id).findFirst(); }
        public List<Contato> buscar(FiltroContato filtro,String texto) { return dados.stream().filter(c -> filtro==FiltroContato.TODOS || (filtro==FiltroContato.CLIENTES && c instanceof Cliente) || (filtro==FiltroContato.FORNECEDORES && c instanceof Fornecedor)).filter(c -> c.getNome().toLowerCase().contains(texto.toLowerCase())).toList(); }
    }
}
