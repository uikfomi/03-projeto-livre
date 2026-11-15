package com.salgaderia.app.controller;

import com.salgaderia.app.dao.ContatoDAO.FiltroContato;
import com.salgaderia.app.dao.impl.ContatoDAOImpl;
import com.salgaderia.app.exception.EstoqueInsuficienteException;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.*;
import com.salgaderia.app.service.impl.*;
import com.salgaderia.app.util.Navegacao;
import com.salgaderia.app.util.TelaAutenticada;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/** Painel coordena os serviços; nenhuma consulta SQL é executada pela interface. */
public class AdminDashboardController implements TelaAutenticada {
    @FXML private BorderPane raiz;
    @FXML private Label titulo, usuarioNome;
    @FXML private VBox conteudo;
    private final ProdutoEstoqueService produtos;
    private final PedidoVendaService pedidos;
    private final AgendaService agenda;
    private final RelatorioFinanceiroService financeiro;
    private final AvaliacaoService avaliacoes;
    private Usuario usuario;
    private long telaAtual;
    private final Map<Long,ItemVenda> carrinho = new LinkedHashMap<>();
    private static final Locale BR=Locale.forLanguageTag("pt-BR");
    private record ItemVenda(Produto produto,int quantidade) { }
    private record DadosPdv(List<Produto> produtos,List<Contato> contatos) { }
    private record DadosEstoque(List<Produto> produtos,List<Categoria> categorias,List<Contato> fornecedores) { }
    private record DadosFinanceiros(RelatorioFinanceiroService.Relatorio totais,Map<String,BigDecimal> produtos,Map<String,BigDecimal> categorias) { }

    public AdminDashboardController(){this(new ProdutoEstoqueServiceImpl(),new PedidoVendaServiceImpl(),new AgendaServiceImpl(new ContatoDAOImpl()),new RelatorioFinanceiroServiceImpl(),new AvaliacaoServiceImpl());}
    public AdminDashboardController(ProdutoEstoqueService produtos,PedidoVendaService pedidos,AgendaService agenda,RelatorioFinanceiroService financeiro,AvaliacaoService avaliacoes){
        this.produtos=Objects.requireNonNull(produtos);this.pedidos=Objects.requireNonNull(pedidos);this.agenda=Objects.requireNonNull(agenda);this.financeiro=Objects.requireNonNull(financeiro);this.avaliacoes=Objects.requireNonNull(avaliacoes);
    }
    @Override public void definirUsuario(Usuario usuario){
        if(usuario==null||!usuario.isAtivo()||!usuario.podeAcessarPainelGerencial())throw new SecurityException("Acesso gerencial restrito");
        this.usuario=usuario;usuarioNome.setText(usuario.getNome());pdv();
    }
    private long iniciarTela(String nome){titulo.setText(nome);conteudo.getChildren().setAll(estado("Carregando..."));return ++telaAtual;}
    private <T> void carregar(long versao,Supplier<T> operacao,Consumer<T> sucesso){
        Task<T> tarefa=new Task<>(){@Override protected T call(){return operacao.get();}};
        tarefa.setOnSucceeded(e->{if(versao==telaAtual)sucesso.accept(tarefa.getValue());});
        tarefa.setOnFailed(e->{if(versao==telaAtual)conteudo.getChildren().setAll(estado("Não foi possível carregar os dados. Verifique a conexão e tente novamente."));});
        Thread t=new Thread(tarefa,"admin-consulta");t.setDaemon(true);t.start();
    }
    private <T> void executar(Supplier<T> operacao,Consumer<T> sucesso){
        raiz.setDisable(true);Task<T> tarefa=new Task<>(){@Override protected T call(){return operacao.get();}};
        tarefa.setOnSucceeded(e->{raiz.setDisable(false);sucesso.accept(tarefa.getValue());});
        tarefa.setOnFailed(e->{raiz.setDisable(false);Throwable erro=tarefa.getException();
            if(erro instanceof EstoqueInsuficienteException estoque)avisar(Alert.AlertType.WARNING,"Estoque insuficiente",estoque.getNomeProduto()+": disponível "+estoque.getEstoqueDisponivel()+", solicitado "+estoque.getQuantidadeSolicitada()+".");
            else if(erro instanceof IllegalArgumentException || erro instanceof IllegalStateException)avisar(Alert.AlertType.WARNING,"Operação não concluída",erro.getMessage());
            else avisar(Alert.AlertType.ERROR,"Operação não concluída","Verifique os dados, vínculos e conexão com o banco.");});
        Thread t=new Thread(tarefa,"admin-operacao");t.setDaemon(true);t.start();
    }

    @FXML private void pdv(){
        long versao=iniciarTela("PDV rápido");
        carregar(versao,()->new DadosPdv(produtos.listar(),agenda.buscar(FiltroContato.CLIENTES,"")),dados->montarPdv(dados));
    }
    private void montarPdv(DadosPdv dados){
        conteudo.getChildren().clear();
        Label subtitulo=texto("Selecione os produtos e conclua a venda no balcão.","subtitle");
        FlowPane grade=new FlowPane(10,10);grade.setPrefWrapLength(520);
        VBox listaCarrinho=new VBox(8);Label total=new Label();total.getStyleClass().add("admin-valor");
        ComboBox<Contato> cliente=new ComboBox<>();cliente.setId("pdv-cliente");cliente.setPromptText("Cliente opcional");cliente.setMaxWidth(Double.MAX_VALUE);cliente.getItems().setAll(dados.contatos());
        cliente.setConverter(new StringConverter<>(){public String toString(Contato c){return c==null?"Venda sem identificação":c.getNome();}public Contato fromString(String s){return null;}});
        Button limparCliente=botao("Limpar cliente",()->cliente.getSelectionModel().clearSelection(),"button-outline");
        ComboBox<FormaPagamento> forma=new ComboBox<>();forma.setId("pdv-forma");forma.getItems().setAll(FormaPagamento.values());forma.setValue(FormaPagamento.DINHEIRO);forma.setMaxWidth(Double.MAX_VALUE);
        TextField recebido=new TextField();recebido.setId("pdv-recebido");recebido.setPromptText("Valor recebido em dinheiro");Label troco=new Label();troco.setId("pdv-troco");
        Runnable[] atualizador=new Runnable[1];
        Runnable atualizar=()->{
            listaCarrinho.getChildren().clear();BigDecimal soma=BigDecimal.ZERO;
            for(ItemVenda item:carrinho.values()){
                soma=soma.add(item.produto().getPrecoVenda().multiply(BigDecimal.valueOf(item.quantidade())));
                Label nome=new Label(item.produto().getNome()+" × "+item.quantidade());nome.setWrapText(true);HBox.setHgrow(nome,Priority.ALWAYS);
                Button menos=botao("−",()->{alterar(item.produto(),-1);atualizador[0].run();},"button-secondary");
                Button mais=botao("+",()->{alterar(item.produto(),1);atualizador[0].run();},"button-secondary");
                listaCarrinho.getChildren().add(new HBox(6,nome,menos,mais));
            }
            if(carrinho.isEmpty())listaCarrinho.getChildren().add(estado("Nenhum produto na venda."));
            total.setText(moeda(soma));recebido.setDisable(!forma.getValue().isPermiteTroco());
            try {BigDecimal valor=decimal(recebido.getText());troco.setText(forma.getValue().isPermiteTroco()?"Troco: "+moeda(valor.subtract(totalCarrinho()).max(BigDecimal.ZERO)):"Sem troco para esta forma.");}
            catch(RuntimeException e){troco.setText(forma.getValue().isPermiteTroco()?"Informe o valor recebido.":"Sem troco para esta forma.");}
        };
        atualizador[0]=atualizar;
        recebido.textProperty().addListener((o,a,n)->atualizar.run());forma.valueProperty().addListener((o,a,n)->atualizar.run());
        for(Produto produto:dados.produtos()){
            Button card=botao(produto.getNome()+"\n"+moeda(produto.getPrecoVenda())+"\nEstoque: "+produto.getEstoqueAtual().stripTrailingZeros().toPlainString(),()->{alterar(produto,1);atualizar.run();},"admin-produto");
            card.setId("pdv-produto-"+produto.getId());card.setPrefWidth(160);card.setWrapText(true);card.setDisable(produto.getEstoqueAtual().compareTo(BigDecimal.ONE)<0);grade.getChildren().add(card);
        }
        VBox produtosBox=new VBox(12,texto("Produtos disponíveis","admin-subtitulo"),grade.getChildren().isEmpty()?estado("Nenhum produto cadastrado."):grade);HBox.setHgrow(produtosBox,Priority.ALWAYS);
        Button concluir=botao("Finalizar venda",()->{
            if(carrinho.isEmpty()){avisar(Alert.AlertType.WARNING,"Carrinho vazio","Adicione produtos antes de finalizar.");return;}
            FormaPagamento selecionada=forma.getValue();BigDecimal valor;
            try{valor=selecionada.isPermiteTroco()?decimal(recebido.getText()):totalCarrinho();if(valor.compareTo(totalCarrinho())<0)throw new IllegalArgumentException();}
            catch(RuntimeException e){avisar(Alert.AlertType.WARNING,"Valor inválido","Informe um valor recebido suficiente para a venda.");return;}
            Cliente contato=cliente.getValue() instanceof Cliente c?c:null;
            List<PedidoItem> itens=carrinho.values().stream().map(i->new PedidoItem(null,i.produto(),BigDecimal.valueOf(i.quantidade()),i.produto().getPrecoVenda())).toList();
            Pedido pedido=new Pedido(null,usuario,contato,StatusPedido.PENDENTE,TipoEntrega.RETIRADA,null,LocalDateTime.now(),itens);
            executar(()->pedidos.finalizarPdv(pedido,selecionada,valor),id->{carrinho.clear();avisar(Alert.AlertType.INFORMATION,"Venda concluída","Venda #"+id+" registrada com baixa de estoque.");pdv();});
        },"button-primary");concluir.setId("pdv-concluir");concluir.setMaxWidth(Double.MAX_VALUE);
        VBox resumo=new VBox(10,texto("Venda atual","admin-subtitulo"),listaCarrinho,new Separator(),new Label("Cliente"),cliente,limparCliente,new Label("Pagamento"),forma,recebido,troco,new Label("Total"),total,concluir);resumo.getStyleClass().add("admin-card");resumo.setPrefWidth(290);
        HBox corpo=new HBox(16,produtosBox,resumo);conteudo.getChildren().addAll(subtitulo,corpo);atualizar.run();
    }
    private BigDecimal totalCarrinho(){return carrinho.values().stream().map(i->i.produto().getPrecoVenda().multiply(BigDecimal.valueOf(i.quantidade()))).reduce(BigDecimal.ZERO,BigDecimal::add);}
    private void alterar(Produto produto,int delta){ItemVenda item=carrinho.get(produto.getId());int qtd=(item==null?0:item.quantidade())+delta;
        if(qtd<=0)carrinho.remove(produto.getId());else if(produto.getEstoqueAtual().compareTo(BigDecimal.valueOf(qtd))>=0)carrinho.put(produto.getId(),new ItemVenda(produto,qtd));
        else avisar(Alert.AlertType.WARNING,"Estoque insuficiente","Não há mais unidades disponíveis.");}

    @FXML private void fila(){
        long versao=iniciarTela("Fila de pedidos");carregar(versao,pedidos::listarFila,lista->{
            conteudo.getChildren().clear();conteudo.getChildren().add(texto("Pedidos da vitrine aguardando preparo ou entrega.","subtitle"));
            if(lista.isEmpty()){conteudo.getChildren().add(estado("Nenhum pedido aguardando atendimento."));return;}
            for(PedidoFila p:lista){
                Label info=new Label("#"+p.id()+"  ·  "+p.cliente()+"  ·  "+p.status()+"  ·  "+p.entrega()+"  ·  "+moeda(p.total())+"\n"+p.itens()+(p.enderecoEntrega()==null||p.enderecoEntrega().isBlank()?"":"\nEndereço: "+p.enderecoEntrega()));info.setWrapText(true);HBox.setHgrow(info,Priority.ALWAYS);
                StatusPedido proximo=switch(p.status()){case PENDENTE->StatusPedido.PREPARANDO;case PREPARANDO->StatusPedido.PRONTO;case PRONTO->StatusPedido.ENTREGUE;default->null;};
                Button avancar=botao("Avançar para "+proximo,()->executar(()->{pedidos.transicionar(p.id(),proximo);return null;},x->fila()),"button-primary");
                Button cancelar=botao("Cancelar",()->{if(confirmar("Cancelar pedido #"+p.id()+"? O estoque será devolvido."))executar(()->{pedidos.transicionar(p.id(),StatusPedido.CANCELADO);return null;},x->fila());},"button-outline");
                HBox linha=new HBox(10,info,avancar,cancelar);linha.setAlignment(Pos.CENTER_LEFT);linha.getStyleClass().add("admin-card");conteudo.getChildren().add(linha);
            }
        });
    }

    @FXML private void estoque(){
        long versao=iniciarTela("Produtos e estoque");
        carregar(versao,()->new DadosEstoque(produtos.listar(),produtos.listarCategorias(),agenda.buscar(FiltroContato.FORNECEDORES,"")),dados->montarEstoque(dados));
    }
    private void montarEstoque(DadosEstoque dados){
        conteudo.getChildren().clear();TableView<Produto> tabela=new TableView<>();tabela.setId("tabela-produtos");tabela.setPlaceholder(estado("Nenhum produto cadastrado."));
        coluna(tabela,"Produto",p->p.getNome(),180);coluna(tabela,"Preço",p->moeda(p.getPrecoVenda()),100);coluna(tabela,"Custo",p->moeda(p.getCustoProducao()),100);
        coluna(tabela,"Estoque",p->p.getEstoqueAtual().stripTrailingZeros().toPlainString(),90);coluna(tabela,"Mínimo",p->p.getEstoqueMinimo().stripTrailingZeros().toPlainString(),90);
        tabela.setRowFactory(t->new TableRow<>(){@Override protected void updateItem(Produto p,boolean vazio){super.updateItem(p,vazio);getStyleClass().remove("estoque-baixo");if(!vazio&&p!=null&&p.isEstoqueBaixo())getStyleClass().add("estoque-baixo");}});
        tabela.getItems().setAll(dados.produtos());tabela.setPrefHeight(380);
        Button novo=botao("Novo produto",()->formularioProduto(null,dados.categorias()),"button-primary");
        Button editar=botao("Editar",()->formularioProduto(tabela.getSelectionModel().getSelectedItem(),dados.categorias()),"button-secondary");
        Button excluir=botao("Excluir",()->{Produto p=tabela.getSelectionModel().getSelectedItem();if(p!=null&&confirmar("Excluir "+p.getNome()+"?"))executar(()->produtos.excluir(p.getId()),ok->{if(!ok)avisar(Alert.AlertType.WARNING,"Produto não encontrado","O produto pode ter sido removido.");estoque();});},"button-outline");
        Button lote=botao("Entrada de lote",()->entradaLote(tabela.getSelectionModel().getSelectedItem(),dados.fornecedores()),"button-secondary");
        Button perda=botao("Registrar perda",()->perda(tabela.getSelectionModel().getSelectedItem()),"button-outline");
        for(Button b:List.of(editar,excluir,lote,perda))b.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());
        conteudo.getChildren().addAll(texto("Estoque atual é alterado por entradas, perdas e vendas.","subtitle"),tabela,new HBox(8,novo,editar,excluir,lote,perda));
    }
    private void formularioProduto(Produto atual,List<Categoria> categorias){
        Dialog<ButtonType> dialog=dialog(atual==null?"Novo produto":"Editar produto");
        TextField nome=campo(atual==null?"":atual.getNome(),"Nome");TextField descricao=campo(atual==null?"":atual.getDescricao(),"Descrição");
        TextField preco=campo(atual==null?"":atual.getPrecoVenda().toPlainString(),"Preço de venda");TextField custo=campo(atual==null?"":atual.getCustoProducao().toPlainString(),"Custo de produção");
        TextField minimo=campo(atual==null?"0":atual.getEstoqueMinimo().toPlainString(),"Estoque mínimo");TextField imagem=campo(atual==null?"":atual.getUrlImagem(),"URL ou arquivo da imagem");
        Button escolher=botao("Selecionar imagem",()->{FileChooser fc=new FileChooser();fc.setTitle("Selecionar imagem");fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens","*.png","*.jpg","*.jpeg","*.gif"));File arquivo=fc.showOpenDialog(raiz.getScene().getWindow());if(arquivo!=null)imagem.setText(arquivo.toURI().toString());},"button-outline");
        ComboBox<Categoria> categoria=new ComboBox<>();categoria.getItems().setAll(categorias);categoria.setPromptText("Categoria (opcional)");categoria.setConverter(new StringConverter<>(){public String toString(Categoria c){return c==null?"Sem categoria":c.getNome();}public Categoria fromString(String s){return null;}});
        if(atual!=null&&atual.getCategoria()!=null)categorias.stream().filter(c->Objects.equals(c.getId(),atual.getCategoria().getId())).findFirst().ifPresent(categoria::setValue);
        VBox campos=new VBox(8,new Label("Nome"),nome,new Label("Descrição"),descricao,new Label("Categoria"),categoria,new Label("Preço de venda"),preco,new Label("Custo de produção"),custo,new Label("Estoque atual: "+(atual==null?"0 (entrada por lote)":atual.getEstoqueAtual())),new Label("Estoque mínimo"),minimo,new Label("Imagem"),imagem,escolher);campos.setPadding(new Insets(10));
        ScrollPane scroll=new ScrollPane(campos);scroll.setFitToWidth(true);scroll.setPrefViewportHeight(470);dialog.getDialogPane().setContent(scroll);
        if(dialog.showAndWait().orElse(ButtonType.CANCEL)!=ButtonType.OK)return;
        try{
            Produto p=new Produto(atual==null?null:atual.getId(),categoria.getValue(),nome.getText(),descricao.getText(),decimal(preco.getText()),decimal(custo.getText()),atual==null?BigDecimal.ZERO:atual.getEstoqueAtual(),decimal(minimo.getText()),imagem.getText().isBlank()?null:imagem.getText());
            executar(()->{if(atual==null)produtos.cadastrar(p);else if(!produtos.atualizar(p))throw new IllegalStateException("Produto não encontrado");return null;},x->estoque());
        }catch(RuntimeException e){avisar(Alert.AlertType.WARNING,"Dados inválidos","Confira nome, valores e estoque mínimo.");}
    }
    private void entradaLote(Produto produto,List<Contato> contatos){
        if(produto==null)return;List<Fornecedor> fornecedores=contatos.stream().filter(Fornecedor.class::isInstance).map(Fornecedor.class::cast).toList();
        if(fornecedores.isEmpty()){avisar(Alert.AlertType.INFORMATION,"Nenhum fornecedor","Cadastre um fornecedor na agenda antes de lançar um lote.");return;}
        Dialog<ButtonType> dialog=dialog("Entrada de lote — "+produto.getNome());ComboBox<Fornecedor> fornecedor=new ComboBox<>();fornecedor.getItems().setAll(fornecedores);fornecedor.setPromptText("Selecione o fornecedor");
        fornecedor.setConverter(new StringConverter<>(){public String toString(Fornecedor f){return f==null?"":f.getNomeEmpresa();}public Fornecedor fromString(String s){return null;}});
        TextField quantidade=campo("","Quantidade");TextField observacao=campo("","Observação (opcional)");dialog.getDialogPane().setContent(new VBox(10,new Label("Fornecedor"),fornecedor,new Label("Quantidade"),quantidade,observacao));
        if(dialog.showAndWait().orElse(ButtonType.CANCEL)!=ButtonType.OK)return;
        try{Fornecedor f=Objects.requireNonNull(fornecedor.getValue());BigDecimal qtd=decimal(quantidade.getText());executar(()->{produtos.entradaLote(produto.getId(),f.getId(),qtd,observacao.getText());return null;},x->estoque());}
        catch(RuntimeException e){avisar(Alert.AlertType.WARNING,"Dados inválidos","Selecione um fornecedor e informe a quantidade.");}
    }
    private void perda(Produto produto){
        if(produto==null)return;Dialog<ButtonType> dialog=dialog("Registrar perda — "+produto.getNome());TextField quantidade=campo("","Quantidade");TextField motivo=campo("","Motivo da perda");dialog.getDialogPane().setContent(new VBox(10,new Label("Quantidade"),quantidade,new Label("Motivo"),motivo));
        if(dialog.showAndWait().orElse(ButtonType.CANCEL)!=ButtonType.OK)return;
        try{BigDecimal qtd=decimal(quantidade.getText());if(motivo.getText().isBlank())throw new IllegalArgumentException();executar(()->{produtos.registrarPerda(produto.getId(),qtd,motivo.getText());return null;},x->estoque());}
        catch(RuntimeException e){avisar(Alert.AlertType.WARNING,"Dados inválidos","Informe quantidade e motivo.");}
    }

    @FXML private void financeiro(){
        long versao=iniciarTela("Financeiro e lucro líquido");
        LocalDateTime inicio=LocalDate.now().withDayOfMonth(1).atStartOfDay(),fim=LocalDate.now().plusDays(1).atStartOfDay();
        carregar(versao,()->new DadosFinanceiros(financeiro.calcular(inicio,fim),financeiro.produtosMaisVendidos(inicio,fim),financeiro.receitaPorCategoria(inicio,fim)),dados->{
            conteudo.getChildren().clear();conteudo.getChildren().add(texto("Mês atual · pedidos entregues pela data de criação; perdas pela data da movimentação.","subtitle"));
            var r=dados.totais();FlowPane indicadores=new FlowPane(10,10,indicador("Faturamento bruto",r.faturamentoBruto()),indicador("Custos de produção",r.custoTotalProducao()),indicador("Perdas",r.perdas()),indicador("Lucro líquido real",r.lucroLiquidoReal()));
            conteudo.getChildren().add(indicadores);
            if(dados.produtos().isEmpty())conteudo.getChildren().add(estado("Nenhuma venda entregue no período para o gráfico de produtos."));
            else{CategoryAxis eixoX=new CategoryAxis();NumberAxis eixoY=new NumberAxis();eixoY.setLabel("Quantidade");BarChart<String,Number> grafico=new BarChart<>(eixoX,eixoY);grafico.setTitle("Produtos mais vendidos");grafico.setLegendVisible(false);XYChart.Series<String,Number> serie=new XYChart.Series<>();dados.produtos().forEach((nome,qtd)->serie.getData().add(new XYChart.Data<>(nome,qtd)));grafico.getData().add(serie);conteudo.getChildren().add(grafico);}
            if(dados.categorias().isEmpty())conteudo.getChildren().add(estado("Nenhuma receita por categoria no período."));
            else{PieChart grafico=new PieChart();grafico.setTitle("Receita por categoria");dados.categorias().forEach((nome,valor)->grafico.getData().add(new PieChart.Data(nome,valor.doubleValue())));conteudo.getChildren().add(grafico);}
        });
    }
    private Node indicador(String nome,BigDecimal valor){VBox caixa=new VBox(8,texto(nome,"subtitle"),texto(moeda(valor),"admin-valor"));caixa.getStyleClass().add("admin-card");caixa.setPrefWidth(190);return caixa;}

    @FXML private void moderacao(){
        long versao=iniciarTela("Moderação de avaliações");carregar(versao,avaliacoes::listar,lista->{
            conteudo.getChildren().clear();TableView<AvaliacaoResumo> tabela=new TableView<>();tabela.setId("tabela-avaliacoes");tabela.setPlaceholder(estado("Nenhuma avaliação cadastrada."));
            coluna(tabela,"Cliente",AvaliacaoResumo::cliente,120);coluna(tabela,"Produto",AvaliacaoResumo::produto,120);coluna(tabela,"Nota",a->a.nota()+" / 5",65);coluna(tabela,"Comentário",a->nulo(a.comentario()),220);coluna(tabela,"Resposta oficial",a->nulo(a.resposta()),220);
            tabela.getItems().setAll(lista);tabela.setPrefHeight(400);
            Button responder=botao("Responder oficialmente",()->{AvaliacaoResumo a=tabela.getSelectionModel().getSelectedItem();if(a==null)return;TextInputDialog d=new TextInputDialog(nulo(a.resposta()));d.initOwner(raiz.getScene().getWindow());d.setTitle("Resposta oficial");d.setHeaderText("Resposta para "+a.cliente());d.showAndWait().ifPresent(resposta->{if(resposta.isBlank()){avisar(Alert.AlertType.WARNING,"Resposta vazia","Escreva uma resposta antes de salvar.");return;}executar(()->avaliacoes.responder(usuario,a.id(),resposta),ok->{if(!ok)avisar(Alert.AlertType.WARNING,"Avaliação não encontrada","Atualize a listagem.");moderacao();});});},"button-primary");
            Button excluir=botao("Excluir avaliação",()->{AvaliacaoResumo a=tabela.getSelectionModel().getSelectedItem();if(a!=null&&confirmar("Excluir esta avaliação?"))executar(()->avaliacoes.excluir(usuario,a.id()),ok->{if(!ok)avisar(Alert.AlertType.WARNING,"Avaliação não encontrada","Atualize a listagem.");moderacao();});},"button-outline");
            responder.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());excluir.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());
            conteudo.getChildren().addAll(tabela,new HBox(10,responder,excluir));
        });
    }
    @FXML private void voltar(){telaAtual++;try{Navegacao.abrirTelaInicial((Stage)raiz.getScene().getWindow(),usuario);}catch(IOException e){avisar(Alert.AlertType.ERROR,"Não foi possível voltar","Tente novamente.");}}
    private static <T> void coluna(TableView<T> tabela,String titulo,java.util.function.Function<T,String> valor,int largura){TableColumn<T,String> c=new TableColumn<>(titulo);c.setCellValueFactory(d->new SimpleStringProperty(valor.apply(d.getValue())));c.setPrefWidth(largura);tabela.getColumns().add(c);}
    private static Label texto(String texto,String estilo){Label l=new Label(texto);l.setWrapText(true);l.getStyleClass().add(estilo);return l;}
    private static Label estado(String texto){Label l=texto(texto,"admin-empty");l.setMaxWidth(Double.MAX_VALUE);return l;}
    private static String nulo(String s){return s==null?"":s;}
    private static String moeda(BigDecimal n){return NumberFormat.getCurrencyInstance(BR).format(n);}
    private static BigDecimal decimal(String s){return new BigDecimal(s.trim().replace(',','.'));}
    private static TextField campo(String valor,String prompt){TextField t=new TextField(nulo(valor));t.setPromptText(prompt);return t;}
    private static Button botao(String texto,Runnable acao,String estilo){Button b=new Button(texto);b.getStyleClass().add(estilo);b.setOnAction(e->acao.run());return b;}
    private Dialog<ButtonType> dialog(String titulo){Dialog<ButtonType> d=new Dialog<>();d.initOwner(raiz.getScene().getWindow());d.setTitle(titulo);d.setHeaderText(titulo);d.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.OK);return d;}
    private boolean confirmar(String mensagem){Alert a=new Alert(Alert.AlertType.CONFIRMATION,mensagem,ButtonType.CANCEL,ButtonType.OK);a.initOwner(raiz.getScene().getWindow());a.setHeaderText(null);return a.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK;}
    private void avisar(Alert.AlertType tipo,String titulo,String mensagem){Alert a=new Alert(tipo);a.initOwner(raiz.getScene().getWindow());a.setTitle(titulo);a.setHeaderText(null);a.setContentText(mensagem);a.showAndWait();}
}
