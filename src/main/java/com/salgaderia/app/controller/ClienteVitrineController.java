package com.salgaderia.app.controller;

import com.salgaderia.app.exception.AvaliacaoNaoPermitidaException;
import com.salgaderia.app.exception.EstoqueInsuficienteException;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.StatusPedido;
import com.salgaderia.app.model.enums.TipoEntrega;
import com.salgaderia.app.model.enums.FormaPagamento;
import com.salgaderia.app.service.*;
import com.salgaderia.app.service.impl.*;
import com.salgaderia.app.service.desconto.ResultadoDesconto;
import com.salgaderia.app.service.cozinha.EtapaCozinha;
import com.salgaderia.app.util.Navegacao;
import com.salgaderia.app.util.TelaAutenticada;
import com.salgaderia.app.util.WhatsAppUi;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.text.NumberFormat;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.concurrent.Task;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ClienteVitrineController implements TelaAutenticada {
    @FXML private BorderPane raiz;
    @FXML private Label saudacao, mensagemProdutos, mensagemPedidos, endereco, total, contador;
    @FXML private TextField busca, cupom;
    @FXML private Label mensagemDesconto;
    @FXML private ComboBox<FiltroCategoria> categorias;
    @FXML private ComboBox<FormaPagamento> formaPagamento;
    @FXML private FlowPane cards;
    @FXML private VBox painelVitrine, painelPedidos, listaPedidos, itensCarrinho;
    @FXML private ScrollPane rolagemProdutos;
    @FXML private RadioButton retirada, entrega, local;
    @FXML private CheckBox agendar;
    @FXML private DatePicker dataAgendamento;
    @FXML private TextField horaAgendamento;
    @FXML private Button finalizar;
    private final ProdutoEstoqueService produtosService;
    private final PedidoVendaService pedidosService;
    private final AvaliacaoService avaliacoesService;
    private final Map<Long,ItemCarrinho> carrinho = new LinkedHashMap<>();
    private List<Produto> produtos = List.of();
    private Map<Long,BigDecimal> medias = Map.of();
    private UsuarioCliente usuario;
    private long geracaoProdutos;
    private long geracaoPedidos;
    private boolean carregouProdutos;
    private final WhatsAppLinkService whatsapp = new WhatsAppLinkService();
    private final Timeline atualizacaoPedidos = new Timeline(new KeyFrame(Duration.seconds(5), e -> atualizarPedidos(false)));
    private boolean consultaPedidosEmAndamento;
    private long geracaoDesconto;
    private ResultadoDesconto descontoAtual;
    private static final Locale BR = Locale.forLanguageTag("pt-BR");

    public ClienteVitrineController() { this(new ProdutoEstoqueServiceImpl(),new PedidoVendaServiceImpl(),new AvaliacaoServiceImpl()); }
    public ClienteVitrineController(ProdutoEstoqueService produtos,PedidoVendaService pedidos,AvaliacaoService avaliacoes) {
        this.produtosService=Objects.requireNonNull(produtos);this.pedidosService=Objects.requireNonNull(pedidos);this.avaliacoesService=Objects.requireNonNull(avaliacoes);
    }
    private record FiltroCategoria(Long id,String nome) { }
    private record ItemCarrinho(Produto produto,int quantidade) { }
    private record Catalogo(List<Produto> produtos,Map<Long,BigDecimal> medias) { }
    private record Historico(List<Pedido> pedidos,long fila) { }

    @FXML private void initialize() {
        atualizacaoPedidos.setCycleCount(Timeline.INDEFINITE);
        formaPagamento.getItems().setAll(FormaPagamento.PIX,FormaPagamento.DINHEIRO,FormaPagamento.CARTAO);
        formaPagamento.setConverter(new StringConverter<>() {
            public String toString(FormaPagamento forma){return forma==null?"":forma.descricao();}
            public FormaPagamento fromString(String valor){return null;}
        });
        formaPagamento.valueProperty().addListener((o,a,n)->finalizar.setDisable(descontoAtual==null||n==null));
        raiz.sceneProperty().addListener((obs, anterior, cena)->{
            if(cena==null){atualizacaoPedidos.stop();geracaoPedidos++;return;}
            cena.windowProperty().addListener((o, velha, janela)->{
                if(janela==null){atualizacaoPedidos.stop();geracaoPedidos++;}
                else janela.showingProperty().addListener((x,era,visivel)->{if(!visivel){atualizacaoPedidos.stop();geracaoPedidos++;}});
            });
        });
        ToggleGroup grupo=new ToggleGroup();retirada.setToggleGroup(grupo);entrega.setToggleGroup(grupo);local.setToggleGroup(grupo);retirada.setSelected(true);
        dataAgendamento.disableProperty().bind(agendar.selectedProperty().not());
        horaAgendamento.disableProperty().bind(agendar.selectedProperty().not());
        grupo.selectedToggleProperty().addListener((o,a,n)->{if(n==null)grupo.selectToggle(a);atualizarEndereco();});
        categorias.setConverter(new StringConverter<>() {
            @Override public String toString(FiltroCategoria filtro) { return filtro==null?"Todas as categorias":filtro.nome(); }
            @Override public FiltroCategoria fromString(String texto) { return null; }
        });
        categorias.valueProperty().addListener((o,a,n)->filtrar());
        busca.textProperty().addListener((o,a,n)->filtrar());
        cupom.textProperty().addListener((o,a,n)->atualizarCarrinho());
        cards.prefWrapLengthProperty().bind(rolagemProdutos.widthProperty().subtract(28));
        mostrarPainel(painelVitrine);atualizarCarrinho();
    }
    @Override public void definirUsuario(Usuario usuario) {
        if (!(usuario instanceof UsuarioCliente cliente) || !cliente.isAtivo()) throw new SecurityException("Acesso restrito a clientes ativos");
        this.usuario=cliente;saudacao.setText("Olá, "+usuario.getNome()+"!");atualizarEndereco();atualizarCarrinho();carregarProdutos();
    }
    @FXML private void mostrarVitrine(){atualizacaoPedidos.stop();geracaoPedidos++;mostrarPainel(painelVitrine);}
    @FXML private void mostrarPedidos(){mostrarPainel(painelPedidos);carregarPedidos();atualizacaoPedidos.playFromStart();}
    private void mostrarPainel(VBox visivel){
        painelVitrine.setVisible(visivel==painelVitrine);painelVitrine.setManaged(visivel==painelVitrine);
        painelPedidos.setVisible(visivel==painelPedidos);painelPedidos.setManaged(visivel==painelPedidos);
    }
    @FXML private void carregarProdutos(){
        long geracao=++geracaoProdutos;carregouProdutos=false;cards.getChildren().clear();exibir(mensagemProdutos,"Carregando produtos...");
        Task<Catalogo> tarefa=new Task<>() { @Override protected Catalogo call(){return new Catalogo(produtosService.listar(),avaliacoesService.mediasPorProduto());} };
        tarefa.setOnSucceeded(e->{
            if(geracao!=geracaoProdutos)return;
            produtos=List.copyOf(tarefa.getValue().produtos());medias=Map.copyOf(tarefa.getValue().medias());carregouProdutos=true;
            Long selecionada=categorias.getValue()==null?null:categorias.getValue().id();
            List<FiltroCategoria> filtros=new ArrayList<>();filtros.add(new FiltroCategoria(null,"Todas as categorias"));
            produtos.stream().map(Produto::getCategoria).filter(Objects::nonNull).collect(java.util.stream.Collectors.toMap(Categoria::getId,c->c,(a,b)->a,LinkedHashMap::new)).values().stream()
                    .sorted(Comparator.comparing(Categoria::getNome,String.CASE_INSENSITIVE_ORDER)).forEach(c->filtros.add(new FiltroCategoria(c.getId(),c.getNome())));
            categorias.getItems().setAll(filtros);categorias.setValue(filtros.stream().filter(f->Objects.equals(f.id(),selecionada)).findFirst().orElse(filtros.get(0)));
            Map<Long,Produto> atuais=new HashMap<>();produtos.forEach(p->atuais.put(p.getId(),p));
            carrinho.replaceAll((id,item)->new ItemCarrinho(atuais.getOrDefault(id,item.produto()),item.quantidade()));
            atualizarCarrinho();filtrar();
        });
        tarefa.setOnFailed(e->{if(geracao!=geracaoProdutos)return;exibir(mensagemProdutos,"Não foi possível carregar os produtos. Verifique a conexão e tente novamente.");});iniciar(tarefa);
    }
    private void filtrar(){
        if(!carregouProdutos)return;
        String termo=normalizar(busca.getText());FiltroCategoria filtro=categorias.getValue();
        List<Produto> selecionados=produtos.stream().filter(p->filtro==null||filtro.id()==null||(p.getCategoria()!=null&&Objects.equals(filtro.id(),p.getCategoria().getId())))
                .filter(p->normalizar(p.getNome()).contains(termo)||normalizar(p.getDescricao()).contains(termo)).toList();
        cards.getChildren().setAll(selecionados.stream().map(this::cardProduto).toList());
        if(produtos.isEmpty())exibir(mensagemProdutos,"Nenhum salgado cadastrado ainda. Volte em breve!");
        else if(selecionados.isEmpty())exibir(mensagemProdutos,"Nenhum salgado encontrado para esta busca ou categoria.");
        else ocultar(mensagemProdutos);
    }
    private Node cardProduto(Produto produto){
        VBox card=new VBox(9);card.getStyleClass().add("produto-card");card.setPrefWidth(220);card.setMinWidth(200);card.setMaxWidth(240);
        StackPane imagem=new StackPane();imagem.getStyleClass().add("produto-imagem");imagem.setPrefHeight(132);imagem.setMinHeight(132);
        Label fallback=new Label("SALGADERIA");fallback.getStyleClass().add("imagem-fallback");imagem.getChildren().add(fallback);
        String url=produto.getUrlImagem();
        if(url!=null&&!url.isBlank()){
            try {
                URI uri=URI.create(url.trim());String esquema=uri.getScheme();
                if(esquema!=null && (esquema.equalsIgnoreCase("http")||esquema.equalsIgnoreCase("https")||esquema.equalsIgnoreCase("file"))){
                    Image foto=new Image(uri.toString(),440,264,true,true,true);ImageView view=new ImageView(foto);view.setFitWidth(200);view.setFitHeight(132);view.setPreserveRatio(true);view.setVisible(false);
                    foto.progressProperty().addListener((o,a,n)->{if(n.doubleValue()>=1&&!foto.isError())view.setVisible(true);});
                    if(foto.getProgress()>=1&&!foto.isError())view.setVisible(true);imagem.getChildren().add(view);
                }
            } catch(IllegalArgumentException ignored) { /* Mantém fallback para URL inválida. */ }
        }
        Label nome=new Label(produto.getNome());nome.setWrapText(true);nome.getStyleClass().add("produto-nome");
        Label descricao=new Label(produto.getDescricao()==null||produto.getDescricao().isBlank()?"Sem descrição":produto.getDescricao());descricao.setWrapText(true);descricao.setMaxHeight(48);descricao.getStyleClass().add("produto-descricao");
        Label preco=new Label(moeda(produto.getPrecoVenda()));preco.getStyleClass().add("produto-preco");
        BigDecimal media=medias.get(produto.getId());Label estrelas=new Label(media==null?"Sem avaliações":"★ "+String.format(BR,"%.1f",media));estrelas.getStyleClass().add("produto-estrelas");
        boolean disponivel=produto.getEstoqueAtual().compareTo(BigDecimal.ONE)>=0;
        Label estoque=new Label(disponivel?(produto.isEstoqueBaixo()?"Últimas unidades":"Disponível"):"Esgotado");estoque.getStyleClass().add(disponivel?"estoque-disponivel":"estoque-esgotado");
        Button adicionar=new Button("Adicionar +");adicionar.setId("adicionar-"+produto.getId());adicionar.getStyleClass().add("button-primary");adicionar.setMaxWidth(Double.MAX_VALUE);adicionar.setDisable(!disponivel);adicionar.setOnAction(e->adicionar(produto));
        HBox info=new HBox(8,estrelas,estoque);info.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().addAll(imagem,nome,descricao,preco,info,adicionar);return card;
    }
    private void adicionar(Produto produto){
        ItemCarrinho existente=carrinho.get(produto.getId());int quantidade=existente==null?1:existente.quantidade()+1;
        if(produto.getEstoqueAtual().compareTo(BigDecimal.valueOf(quantidade))<0){avisar(Alert.AlertType.WARNING,"Estoque insuficiente","Não há mais unidades disponíveis deste produto.");return;}
        carrinho.put(produto.getId(),new ItemCarrinho(produto,quantidade));atualizarCarrinho();
    }
    private void alterarQuantidade(long id,int delta){
        ItemCarrinho item=carrinho.get(id);if(item==null)return;int nova=item.quantidade()+delta;
        if(nova<=0)carrinho.remove(id);else if(item.produto().getEstoqueAtual().compareTo(BigDecimal.valueOf(nova))>=0)carrinho.put(id,new ItemCarrinho(item.produto(),nova));
        else {avisar(Alert.AlertType.WARNING,"Estoque insuficiente","Não há mais unidades disponíveis deste produto.");return;}
        atualizarCarrinho();
    }
    private void atualizarCarrinho(){
        itensCarrinho.getChildren().clear();BigDecimal soma=BigDecimal.ZERO;int qtd=0;
        for(ItemCarrinho item:carrinho.values()){
            Produto p=item.produto();qtd+=item.quantidade();soma=soma.add(p.getPrecoVenda().multiply(BigDecimal.valueOf(item.quantidade())));
            Label nome=new Label(p.getNome());nome.setWrapText(true);nome.getStyleClass().add("carrinho-nome");
            Label subtotal=new Label(moeda(p.getPrecoVenda().multiply(BigDecimal.valueOf(item.quantidade()))));subtotal.getStyleClass().add("subtitle");
            VBox detalhes=new VBox(4,nome,subtotal);HBox.setHgrow(detalhes,Priority.ALWAYS);
            Button menos=new Button("−");menos.getStyleClass().add("quantidade-botao");menos.setAccessibleText("Remover uma unidade de "+p.getNome());menos.setOnAction(e->alterarQuantidade(p.getId(),-1));
            Button mais=new Button("+");mais.getStyleClass().add("quantidade-botao");mais.setAccessibleText("Adicionar uma unidade de "+p.getNome());mais.setOnAction(e->alterarQuantidade(p.getId(),1));
            Label quantidade=new Label(Integer.toString(item.quantidade()));HBox linha=new HBox(7,detalhes,menos,quantidade,mais);linha.setAlignment(Pos.CENTER_LEFT);linha.getStyleClass().add("carrinho-item");itensCarrinho.getChildren().add(linha);
        }
        if(carrinho.isEmpty()){Label vazio=new Label("Seu carrinho está vazio. Escolha seus salgados favoritos.");vazio.setWrapText(true);vazio.getStyleClass().add("subtitle");itensCarrinho.getChildren().add(vazio);}
        total.setText(moeda(soma));contador.setText(qtd+ (qtd==1?" item":" itens"));
        simularDesconto();
    }
    private Pedido pedidoAtual(){
        TipoEntrega tipo=modalidade();
        String enderecoEntrega=tipo==TipoEntrega.ENTREGA?usuario.getCliente().getEndereco():null;
        if(tipo==TipoEntrega.ENTREGA&&(enderecoEntrega==null||enderecoEntrega.isBlank())) tipo=TipoEntrega.RETIRADA;
        List<PedidoItem> itens=carrinho.values().stream().map(i->new PedidoItem(null,i.produto(),BigDecimal.valueOf(i.quantidade()),i.produto().getPrecoVenda())).toList();
        return new Pedido(null,usuario,usuario.getCliente(),StatusPedido.PENDENTE,tipo,enderecoEntrega,LocalDateTime.now(),itens);
    }
    private void simularDesconto(){
        long geracao=++geracaoDesconto;descontoAtual=null;finalizar.setDisable(true);
        if(usuario==null||carrinho.isEmpty()){mensagemDesconto.setText("");return;}
        mensagemDesconto.setText("Calculando melhor desconto...");
        Pedido pedido=pedidoAtual();String codigo=cupom.getText();
        Task<ResultadoDesconto> tarefa=new Task<>(){@Override protected ResultadoDesconto call(){return pedidosService.simular(pedido,codigo);}};
        tarefa.setOnSucceeded(e->{if(geracao!=geracaoDesconto)return;descontoAtual=tarefa.getValue();total.setText(moeda(descontoAtual.total()));
            mensagemDesconto.setText("Desconto: "+moeda(descontoAtual.desconto())+" ("+descontoAtual.estrategia()+")");finalizar.setDisable(formaPagamento.getValue()==null);});
        tarefa.setOnFailed(e->{if(geracao!=geracaoDesconto)return;Throwable erro=tarefa.getException();mensagemDesconto.setText(erro instanceof IllegalArgumentException?erro.getMessage():"Não foi possível calcular o desconto. Tente novamente.");});
        iniciar(tarefa);
    }
    private void atualizarEndereco(){
        if(usuario==null)return;
        String valor=usuario.getCliente().getEndereco();
        endereco.setText(entrega.isSelected()?(valor==null||valor.isBlank()?"Nenhum endereço cadastrado. Cadastre um endereço antes de pedir entrega.":valor):local.isSelected()?"Consuma seu pedido no local.":"Retire seu pedido no balcão.");
    }
    private TipoEntrega modalidade(){return entrega.isSelected()?TipoEntrega.ENTREGA:local.isSelected()?TipoEntrega.LOCAL:TipoEntrega.RETIRADA;}
    @FXML private void finalizar(){
        if(carrinho.isEmpty()||usuario==null||descontoAtual==null||formaPagamento.getValue()==null)return;
        TipoEntrega tipo=modalidade();
        String enderecoEntrega=tipo==TipoEntrega.ENTREGA?usuario.getCliente().getEndereco():null;
        if(tipo==TipoEntrega.ENTREGA&&(enderecoEntrega==null||enderecoEntrega.isBlank())){avisar(Alert.AlertType.WARNING,"Endereço necessário","Seu cadastro não possui endereço. Escolha retirada ou atualize seu endereço antes de pedir entrega.");return;}
        List<PedidoItem> itens=carrinho.values().stream().map(i->new PedidoItem(null,i.produto(),BigDecimal.valueOf(i.quantidade()),i.produto().getPrecoVenda())).toList();
        Pedido pedido=new Pedido(null,usuario,usuario.getCliente(),StatusPedido.PENDENTE,tipo,enderecoEntrega,LocalDateTime.now(),itens);
        pedido.setFormaPagamento(formaPagamento.getValue());
        if(agendar.isSelected()){
            try {
                LocalDateTime data=LocalDateTime.of(Objects.requireNonNull(dataAgendamento.getValue()),LocalTime.parse(horaAgendamento.getText().trim()));
                if(!data.isAfter(LocalDateTime.now()))throw new IllegalArgumentException("Data deve estar no futuro");
                pedido.setAgendadoPara(data);
            }catch(RuntimeException erro){avisar(Alert.AlertType.WARNING,"Agendamento inválido","Informe uma data e horário futuro no formato HH:mm.");return;}
        }
        raiz.setDisable(true);
        ResultadoDesconto esperado=descontoAtual;String codigo=cupom.getText();
        Task<Long> tarefa=new Task<>() { @Override protected Long call(){return pedidosService.finalizar(pedido,codigo,esperado);} };
        tarefa.setOnSucceeded(e->{raiz.setDisable(false);if(pedido.getId()==null)pedido.setId(tarefa.getValue());pedido.setDesconto(esperado.desconto());carrinho.clear();cupom.clear();formaPagamento.setValue(null);agendar.setSelected(false);dataAgendamento.setValue(null);horaAgendamento.clear();atualizarCarrinho();
            Alert alerta=new Alert(Alert.AlertType.INFORMATION);alerta.initOwner(raiz.getScene().getWindow());alerta.setTitle("Pedido enviado");alerta.setHeaderText(null);alerta.setContentText("Pedido #"+tarefa.getValue()+" recebido com sucesso.");
            ButtonType acompanhar=new ButtonType("Acompanhar no WhatsApp");alerta.getButtonTypes().add(acompanhar);
            if(alerta.showAndWait().orElse(ButtonType.OK)==acompanhar)abrirWhatsApp(pedido);
            carregarProdutos();mostrarPedidos();});
        tarefa.setOnFailed(e->{raiz.setDisable(false);Throwable erro=tarefa.getException();
            if(erro instanceof EstoqueInsuficienteException estoque)avisar(Alert.AlertType.WARNING,"Estoque insuficiente",estoque.getNomeProduto()+": disponível "+estoque.getEstoqueDisponivel()+", solicitado "+estoque.getQuantidadeSolicitada()+". Revise seu carrinho.");
            else if(erro instanceof IllegalArgumentException)avisar(Alert.AlertType.WARNING,"Pedido não finalizado",erro.getMessage());
            else avisar(Alert.AlertType.ERROR,"Não foi possível finalizar","Verifique sua conexão e tente novamente. Seu carrinho foi preservado.");carregarProdutos();});iniciar(tarefa);
    }
    @FXML private void carregarPedidos(){atualizarPedidos(true);}
    private void atualizarPedidos(boolean inicial){
        if(usuario==null||consultaPedidosEmAndamento)return;consultaPedidosEmAndamento=true;long geracao=++geracaoPedidos;
        if(inicial){listaPedidos.getChildren().clear();exibir(mensagemPedidos,"Carregando seus pedidos...");}
        Task<Historico> tarefa=new Task<>() { @Override protected Historico call(){return new Historico(pedidosService.listarDoCliente(usuario),pedidosService.contarFilaCozinha());} };
        tarefa.setOnSucceeded(e->{consultaPedidosEmAndamento=false;if(geracao!=geracaoPedidos)return;List<Pedido> pedidos=tarefa.getValue().pedidos();
            if(pedidos.isEmpty()){exibir(mensagemPedidos,"Você ainda não fez nenhum pedido.");return;}
            ocultar(mensagemPedidos);listaPedidos.getChildren().setAll(pedidos.stream().map(p->cardPedido(p,tarefa.getValue().fila())).toList());
        });
        tarefa.setOnFailed(e->{consultaPedidosEmAndamento=false;if(geracao==geracaoPedidos)exibir(mensagemPedidos,"Não foi possível atualizar seus pedidos. Verifique a conexão e tente novamente.");});iniciar(tarefa);
    }
    private Node cardPedido(Pedido pedido,long fila){
        VBox card=new VBox(10);card.getStyleClass().add("pedido-card");
        Label numero=new Label("Pedido #"+pedido.getId()+"  ·  "+pedido.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));numero.getStyleClass().add("pedido-titulo");
        Label status=new Label(pedido.getStatus().name());status.getStyleClass().addAll("status-pedido","status-"+pedido.getStatus().name().toLowerCase(Locale.ROOT));
        Region espaco=new Region();HBox.setHgrow(espaco,Priority.ALWAYS);HBox topo=new HBox(8,numero,espaco,status);topo.setAlignment(Pos.CENTER_LEFT);card.getChildren().add(topo);
        var etapa=EtapaCozinha.de(pedido.getStatus());
        ProgressBar termometro=new ProgressBar(etapa.progresso());termometro.setMaxWidth(Double.MAX_VALUE);
        boolean futuro=pedido.getAgendadoPara()!=null&&pedido.getAgendadoPara().isAfter(LocalDateTime.now());
        String estimativa=etapa.aguardaCozinha()?(futuro?" · aguardando horário agendado":" · estimativa: "+(Math.max(1,Math.min(fila,100000))*5)+" min (fila atual)"):"";
        card.getChildren().addAll(new Label("🌡 Termômetro da Cozinha — "+etapa.mensagem(pedido.getTipoEntrega())+estimativa),termometro);
        if(pedido.getAgendadoPara()!=null)card.getChildren().add(new Label("Agendado: "+pedido.getAgendadoPara().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        for(PedidoItem item:pedido.getItens()){
            Label descricao=new Label(item.getQuantidade().stripTrailingZeros().toPlainString()+" × "+item.getProduto().getNome()+"  ·  "+moeda(item.calcularSubtotal()));descricao.setWrapText(true);HBox.setHgrow(descricao,Priority.ALWAYS);
            Button avaliar=new Button("Avaliar produto");avaliar.setId("avaliar-"+pedido.getId()+"-"+item.getProduto().getId());avaliar.getStyleClass().add("button-secondary");avaliar.setDisable(pedido.getStatus()!=StatusPedido.ENTREGUE);avaliar.setOnAction(e->avaliar(item.getProduto()));
            HBox linha=new HBox(8,descricao,avaliar);linha.setAlignment(Pos.CENTER_LEFT);card.getChildren().add(linha);
        }
        Label totalPedido=new Label("Total: "+moeda(pedido.calcularTotal()));totalPedido.getStyleClass().add("produto-preco");
        Button whatsappPedido=new Button("Acompanhar no WhatsApp");whatsappPedido.setId("whatsapp-pedido-"+pedido.getId());whatsappPedido.getStyleClass().add("button-secondary");whatsappPedido.setOnAction(e->abrirWhatsApp(pedido));
        card.getChildren().addAll(totalPedido,whatsappPedido);return card;
    }
    @FXML private void pedirDeSempre(){
        if(usuario==null)return;raiz.setDisable(true);
        Task<List<PedidoItem>> tarefa=new Task<>(){@Override protected List<PedidoItem> call(){return pedidosService.repetirUltimoPedido(usuario);}};
        tarefa.setOnSucceeded(e->{raiz.setDisable(false);carrinho.clear();for(PedidoItem i:tarefa.getValue())carrinho.put(i.getProduto().getId(),new ItemCarrinho(i.getProduto(),i.getQuantidade().intValueExact()));atualizarCarrinho();mostrarVitrine();});
        tarefa.setOnFailed(e->{raiz.setDisable(false);Throwable erro=tarefa.getException();avisar(Alert.AlertType.WARNING,"Não foi possível repetir",erro instanceof IllegalArgumentException?erro.getMessage():"Verifique a disponibilidade e a conexão.");});
        iniciar(tarefa);
    }
    private void abrirWhatsApp(Pedido pedido){WhatsAppUi.abrir(raiz.getScene().getWindow(),whatsapp,whatsapp.acompanhar(pedido));}
    private void avaliar(Produto produto){
        Dialog<ButtonType> dialog=new Dialog<>();dialog.initOwner(raiz.getScene().getWindow());dialog.setTitle("Avaliar produto");dialog.setHeaderText(produto.getNome());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.OK);
        ComboBox<Integer> nota=new ComboBox<>();nota.getItems().setAll(1,2,3,4,5);nota.setPromptText("Selecione de 1 a 5 estrelas");nota.setMaxWidth(Double.MAX_VALUE);
        nota.setConverter(new StringConverter<>() {public String toString(Integer n){return n==null?"":"★".repeat(n)+" ("+n+")";}public Integer fromString(String s){return null;}});
        TextArea comentario=new TextArea();comentario.setPromptText("Conte como foi sua experiência (opcional)");comentario.setWrapText(true);comentario.setPrefRowCount(3);
        VBox conteudo=new VBox(10,new Label("Sua nota"),nota,new Label("Comentário"),comentario);conteudo.setPadding(new Insets(10,0,0,0));dialog.getDialogPane().setContent(conteudo);
        Node confirmar=dialog.getDialogPane().lookupButton(ButtonType.OK);confirmar.disableProperty().bind(nota.valueProperty().isNull());
        if(dialog.showAndWait().orElse(ButtonType.CANCEL)!=ButtonType.OK)return;
        Avaliacao avaliacao=new Avaliacao(null,usuario,produto,nota.getValue(),comentario.getText(),null,LocalDateTime.now());
        raiz.setDisable(true);Task<Long> tarefa=new Task<>() { @Override protected Long call(){return avaliacoesService.salvar(avaliacao);} };
        tarefa.setOnSucceeded(e->{raiz.setDisable(false);avisar(Alert.AlertType.INFORMATION,"Avaliação enviada","Obrigado por compartilhar sua experiência!");carregarProdutos();});
        tarefa.setOnFailed(e->{raiz.setDisable(false);if(tarefa.getException() instanceof AvaliacaoNaoPermitidaException)avisar(Alert.AlertType.WARNING,"Avaliação não permitida","Você precisa ter recebido um pedido com este produto para avaliá-lo.");else avisar(Alert.AlertType.ERROR,"Não foi possível avaliar","Verifique sua conexão e tente novamente.");});iniciar(tarefa);
    }
    @FXML private void sair(){
        atualizacaoPedidos.stop();geracaoProdutos++;geracaoPedidos++;
        try {Navegacao.abrirLogin((Stage)raiz.getScene().getWindow());}
        catch(IOException e){avisar(Alert.AlertType.ERROR,"Não foi possível sair","Tente novamente.");}
    }
    private static String moeda(BigDecimal valor){return NumberFormat.getCurrencyInstance(BR).format(valor);}
    private static String normalizar(String texto){return texto==null?"":Normalizer.normalize(texto,Normalizer.Form.NFD).replaceAll("\\p{M}+","").toLowerCase(BR).trim();}
    private static void exibir(Label label,String texto){label.setText(texto);label.setVisible(true);label.setManaged(true);}
    private static void ocultar(Label label){label.setVisible(false);label.setManaged(false);}
    private static void iniciar(Task<?> tarefa){Thread thread=new Thread(tarefa,"vitrine-banco");thread.setDaemon(true);thread.start();}
    private void avisar(Alert.AlertType tipo,String titulo,String mensagem){Alert alerta=new Alert(tipo);alerta.initOwner(raiz.getScene().getWindow());alerta.setTitle(titulo);alerta.setHeaderText(null);alerta.setContentText(mensagem);alerta.showAndWait();}
}
