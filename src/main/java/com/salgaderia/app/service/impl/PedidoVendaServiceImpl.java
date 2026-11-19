package com.salgaderia.app.service.impl;
import com.salgaderia.app.dao.*;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.PedidoVendaService;
import com.salgaderia.app.service.PersonalizacaoService;
import com.salgaderia.app.service.BarramentoPedido;
import com.salgaderia.app.service.ConquistasObserver;
import com.salgaderia.app.service.PedidoCancelamentoService;
import com.salgaderia.app.service.PedidoConcluidoObserver.*;
import com.salgaderia.app.exception.EstoqueInsuficienteException;
import com.salgaderia.app.service.desconto.*;
import com.salgaderia.app.service.pix.PixConfiguracao;
import com.salgaderia.app.service.pix.PixPagamentoService;
import com.salgaderia.app.util.ConnectionFactory;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
public class PedidoVendaServiceImpl implements PedidoVendaService {
    private final PedidoDAO pedidos;private final ProdutoDAO produtos;private final MovimentacaoEstoqueDAO movimentos;
    private final CupomDAO cupons = new CupomDAOImpl();
    private final ClubeDAO clube = new ClubeDAOImpl();
    private final GamificacaoDAO jogo = new GamificacaoDAOImpl();
    private final UsuarioDAO usuarios = new UsuarioDAOImpl(new ContatoDAOImpl());
    private final BarramentoPedido eventos = new BarramentoPedido(new ConquistasObserver(jogo));
    private final CalculadoraDescontos calculadora = new CalculadoraDescontos();
    private final PixPagamentoService pix;
    private final PixPagamentoDAO pixDados=new PixPagamentoDAOImpl();
    private final PedidoCancelamentoService cancelamento;
    private record Venda(long id,ResultadoDesconto desconto,StatusPagamento pagamento,boolean demo) { }
    public PedidoVendaServiceImpl(){this(new PedidoDAOImpl(),new ProdutoDAOImpl(),new MovimentacaoEstoqueDAOImpl());}
    public PedidoVendaServiceImpl(PixPagamentoService pix){this(new PedidoDAOImpl(),new ProdutoDAOImpl(),new MovimentacaoEstoqueDAOImpl(),pix);}
    public PedidoVendaServiceImpl(PedidoDAO pedidos,ProdutoDAO produtos,MovimentacaoEstoqueDAO movimentos){
        this(pedidos,produtos,movimentos,new PixPagamentoService());
    }
    public PedidoVendaServiceImpl(PedidoDAO pedidos,ProdutoDAO produtos,MovimentacaoEstoqueDAO movimentos,PixPagamentoService pix){
        this.pedidos=Objects.requireNonNull(pedidos);this.produtos=Objects.requireNonNull(produtos);this.movimentos=Objects.requireNonNull(movimentos);
        this.pix=Objects.requireNonNull(pix);this.cancelamento=new PedidoCancelamentoService(pedidos,produtos,movimentos,eventos);
    }
    @Override public List<Pedido> listarDoCliente(UsuarioCliente usuario){
        if(usuario==null || usuario.getId()==null || !usuario.isAtivo())throw new IllegalArgumentException("Cliente autenticado obrigatório");
        return ConnectionFactory.executarEmTransacao(c->pedidos.listarDoCliente(c,usuario));
    }
    @Override public List<PedidoFila> listarFila(){return ConnectionFactory.executarEmTransacao(pedidos::listarFila);}
    @Override public long contarFilaCozinha(){return ConnectionFactory.executarEmTransacao(pedidos::contarFilaCozinha);}
    @Override public List<PedidoItem> repetirUltimoPedido(UsuarioCliente usuario){
        if(usuario==null||usuario.getId()==null||!usuario.isAtivo())throw new IllegalArgumentException("Cliente autenticado obrigatório");
        return ConnectionFactory.executarEmTransacao(c->{
            Pedido anterior=pedidos.listarDoCliente(c,usuario).stream().filter(p->p.getStatus()==StatusPedido.ENTREGUE).findFirst().orElseThrow(()->new IllegalArgumentException("Você ainda não possui pedido concluído."));
            Map<Long,BigDecimal> quantidades=PersonalizacaoService.quantidades(anterior.getItens());
            Map<Long,Produto> atuais=new LinkedHashMap<>();
            List<PedidoItem> resultado=new ArrayList<>();
            for(var entrada:quantidades.entrySet()){
                Produto atual=produtos.buscarPorId(c,entrada.getKey(),false).orElseThrow(()->new IllegalArgumentException("Um produto do pedido anterior não está mais disponível."));
                if(atual.getEstoqueAtual().compareTo(entrada.getValue())<0)throw new EstoqueInsuficienteException(atual.getNome(),atual.getEstoqueAtual(),entrada.getValue());
                try{entrada.getValue().intValueExact();}catch(ArithmeticException e){throw new IllegalArgumentException("O pedido anterior contém quantidade não inteira.",e);}
                atuais.put(entrada.getKey(),atual);
            }
            Map<Long,BigDecimal> avulsos=new LinkedHashMap<>();
            for(PedidoItem item:anterior.getItens()){
                Produto atual=atuais.get(item.getProduto().getId());
                if(item.getPersonalizacao()==null) {
                    try{item.getQuantidade().intValueExact();}catch(ArithmeticException e){throw new IllegalArgumentException("O pedido anterior contém quantidade não inteira.",e);}
                    avulsos.merge(atual.getId(),item.getQuantidade(),BigDecimal::add);
                }else resultado.add(item.comProdutoAtual(atual));
            }
            avulsos.forEach((id,q)->resultado.add(new PedidoItem(null,atuais.get(id),q,atuais.get(id).getPrecoVenda())));
            return PersonalizacaoService.descrever(resultado);
        });
    }
    @Override public long finalizar(Pedido pedido){ return finalizarInterno(pedido,null,null,null,null); }
    @Override public long finalizar(Pedido pedido,String codigo,ResultadoDesconto esperado){
        return finalizarInterno(pedido,null,null,codigo,Objects.requireNonNull(esperado));
    }
    @Override public long finalizarPdv(Pedido pedido,FormaPagamento forma,BigDecimal recebido){
        Objects.requireNonNull(pedido,"pedido");Objects.requireNonNull(forma,"forma de pagamento");
        if(!(pedido.getUsuario() instanceof UsuarioAdmin) || pedido.getUsuario().getId()==null || !pedido.getUsuario().isAtivo())throw new IllegalArgumentException("Venda PDV exige administrador ativo");
        return finalizarInterno(pedido,forma,recebido,null,null);
    }
    @Override public long finalizarPdv(Pedido pedido,FormaPagamento forma,BigDecimal recebido,String codigo,ResultadoDesconto esperado){
        Objects.requireNonNull(forma,"forma de pagamento");
        return finalizarInterno(pedido,forma,recebido,codigo,Objects.requireNonNull(esperado));
    }
    @Override public ResultadoDesconto simular(Pedido pedido,String codigo) {
        Objects.requireNonNull(pedido);
        if(pedido.getItens().isEmpty())throw new IllegalArgumentException("Carrinho vazio");
        return ConnectionFactory.executarEmTransacao(c->{
            Map<Long,Produto> atuais=new HashMap<>();
            for(PedidoItem item:pedido.getItens()) {
                Long id=item.getProduto().getId();
                if(id==null)throw new IllegalArgumentException("Produto sem ID");
                Produto p=atuais.get(id);
                if(p==null)p=produtos.buscarPorId(c,id,false).orElseThrow(()->new IllegalArgumentException("Produto não encontrado"));
                if(p.getPrecoVenda().compareTo(item.getPrecoUnitario())!=0)throw new IllegalArgumentException("Preço do produto mudou");
                atuais.put(id,p);
            }
            return calcular(c,pedido,codigo,atuais);
        });
    }
    private ResultadoDesconto calcular(Connection c,Pedido pedido,String codigo,Map<Long,Produto> atuais) throws SQLException {
        List<PedidoItem> itens=new ArrayList<>();
        for(PedidoItem item:pedido.getItens())itens.add(item.comProdutoAtual(atuais.get(item.getProduto().getId())));
        PersonalizacaoService.validarComposicao(itens);
        long entregues=pedido.getCliente()==null||pedido.getCliente().getId()==null?0:pedidos.contarEntreguesDoCliente(c,pedido.getCliente().getId());
        Long contaVitrine=pedido.getUsuario() instanceof UsuarioCliente u?u.getId():null;
        BigDecimal taxa=contaVitrine!=null&&pedido.getTipoEntrega()==TipoEntrega.ENTREGA?PixConfiguracao.taxaEntrega():BigDecimal.ZERO;
        Cupom cupom=null;
        if(codigo!=null&&!codigo.isBlank()){
            String normalizado=codigo.trim().toUpperCase(Locale.ROOT);
            cupom=cupons.buscarAtivo(c,normalizado,contaVitrine).orElseThrow(()->new IllegalArgumentException("Cupom inválido, indisponível ou inativo"));
            BigDecimal subtotal=itens.stream().map(PedidoItem::calcularSubtotal).reduce(BigDecimal.ZERO,BigDecimal::add);
            if(subtotal.compareTo(cupom.minimo())<0)throw new IllegalArgumentException("Valor mínimo do cupom: R$ "+cupom.minimo());
            if(cupom.beneficio()==Cupom.Beneficio.FRETE&&taxa.signum()==0)
                throw new IllegalArgumentException("O cupom de frete grátis só vale para pedidos com entrega e taxa.");
        }
        BigDecimal percentual=contaVitrine==null?BigDecimal.ZERO:clube.assinatura(c,contaVitrine,false)
                .filter(a->a.ativa(LocalDate.now())).map(AssinaturaCliente::descontoVigente).orElse(BigDecimal.ZERO);
        return calculadora.calcular(new ContextoDesconto(itens,entregues,cupom,percentual,taxa));
    }
    private long finalizarInterno(Pedido pedido,FormaPagamento forma,BigDecimal recebido,String codigo,ResultadoDesconto esperado){
        Objects.requireNonNull(pedido);if(pedido.getId()!=null||pedido.getStatus()!=StatusPedido.PENDENTE||pedido.getItens().isEmpty())throw new IllegalArgumentException("Pedido deve ser novo, pendente e conter itens");
        if(pedido.getAgendadoPara()!=null&&!pedido.getAgendadoPara().isAfter(LocalDateTime.now()))throw new IllegalArgumentException("O agendamento deve estar no futuro");
        if(pedido.getUsuario()!=null && pedido.getUsuario().getId()==null)throw new IllegalArgumentException("Usuário sem ID");
        if(forma!=null && (!(pedido.getUsuario() instanceof UsuarioAdmin) || pedido.getUsuario().getId()==null || !pedido.getUsuario().isAtivo()))
            throw new IllegalArgumentException("Venda PDV exige administrador ativo");
        if(pedido.getCliente()!=null && pedido.getCliente().getId()==null)throw new IllegalArgumentException("Cliente sem ID");
        if(pedido.getUsuario() instanceof UsuarioCliente u && (pedido.getCliente()==null || !Objects.equals(u.getCliente().getId(),pedido.getCliente().getId())))throw new IllegalArgumentException("Cliente não corresponde ao usuário");
        if(pedido.getUsuario() instanceof UsuarioCliente && (pedido.getFormaPagamento()==null ||
                (pedido.getFormaPagamento()!=FormaPagamento.PIX && pedido.getFormaPagamento()!=FormaPagamento.DINHEIRO && pedido.getFormaPagamento()!=FormaPagamento.CARTAO)))
            throw new IllegalArgumentException("Selecione PIX, dinheiro ou cartão");
        Map<Long,BigDecimal> quantidades=new TreeMap<>();
        for(PedidoItem item:pedido.getItens()){
            Long id=item.getProduto().getId();if(id==null)throw new IllegalArgumentException("Produto sem ID");
            ProdutoEstoqueServiceImpl.validarQuantidade(item.getQuantidade());
            quantidades.merge(id,item.getQuantidade(),BigDecimal::add);
            ProdutoEstoqueServiceImpl.validarQuantidade(quantidades.get(id));
        }
        Venda venda=ConnectionFactory.executarEmTransacao(c->{
            if(forma!=null){
                Usuario responsavel=usuarios.buscarPorId(c,pedido.getUsuario().getId()).orElseThrow(()->new IllegalArgumentException("Administrador não encontrado"));
                if(!(responsavel instanceof UsuarioAdmin)||!responsavel.isAtivo())throw new IllegalArgumentException("Venda PDV exige administrador ativo");
            }
            boolean vitrine=pedido.getUsuario() instanceof UsuarioCliente;
            GamificacaoDAO.Conta conta=null;
            if(vitrine){
                conta=new GamificacaoDAO.Conta(pedido.getUsuario().getId(),pedido.getCliente().getId());
                if(!usuarios.bloquearClienteAtivo(c,conta.usuarioId(),conta.contatoId()))
                    throw new IllegalArgumentException("Cliente ativo obrigatório.");
            }else if(forma!=null&&pedido.getCliente()!=null){
                conta=bloquearConta(c,jogo.contaPorContato(c,pedido.getCliente().getId()).orElse(null));
            }
            Map<Long,Produto> bloqueados=new LinkedHashMap<>();Map<Long,BigDecimal> custos=new HashMap<>();
            for(var entrada:quantidades.entrySet()){
                Produto p=produtos.buscarPorId(c,entrada.getKey(),true).orElseThrow(()->new IllegalArgumentException("Produto não encontrado"));
                p.removerEstoque(entrada.getValue());bloqueados.put(entrada.getKey(),p);custos.put(entrada.getKey(),p.getCustoProducao());
            }
            for(PedidoItem item:pedido.getItens())if(item.getPrecoUnitario().compareTo(bloqueados.get(item.getProduto().getId()).getPrecoVenda())!=0)throw new IllegalArgumentException("Preço do pedido diverge do preço atual");
            ResultadoDesconto desconto=calcular(c,pedido,codigo,bloqueados);
            if(esperado!=null && (desconto.subtotal().compareTo(esperado.subtotal())!=0 || desconto.desconto().compareTo(esperado.desconto())!=0 ||
                    desconto.taxaEntrega().compareTo(esperado.taxaEntrega())!=0 || !desconto.estrategia().equals(esperado.estrategia()) ||
                    !Objects.equals(desconto.cupom(),esperado.cupom())||!Objects.equals(desconto.brinde(),esperado.brinde())))
                throw new IllegalArgumentException("Desconto alterado; recalcule e confirme o pedido");
            if(forma!=null && forma.isPermiteTroco() && (recebido==null || recebido.scale()>2 || recebido.compareTo(desconto.total())<0 || recebido.compareTo(new BigDecimal("99999999.99"))>0))
                throw new IllegalArgumentException("Valor recebido insuficiente ou inválido");
            long pedidoId=pedidos.inserir(c,pedido,custos);
            pedidos.registrarDesconto(c,pedidoId,desconto);
            boolean pixVitrine=vitrine&&pedido.getFormaPagamento()==FormaPagamento.PIX;
            if(pixVitrine)pix.registrar(c,pedidoId,pedido,desconto.total());
            if(forma!=null) pedidos.registrarPdv(c,pedidoId,forma,forma.isPermiteTroco()?recebido:desconto.total(),forma.isPermiteTroco()?recebido.subtract(desconto.total()):BigDecimal.ZERO);
            for(var entrada:bloqueados.entrySet()){
                Produto p=entrada.getValue();if(!produtos.atualizarEstoque(c,entrada.getKey(),p.getEstoqueAtual()))throw new IllegalStateException("Produto não atualizado");
                movimentos.inserir(c,new MovimentacaoEstoque(null,p,TipoMovimentacaoEstoque.SAIDA,quantidades.get(entrada.getKey()),"Pedido "+pedidoId,LocalDateTime.now()),null,"VENDA",p.getCustoProducao());
            }
            if(vitrine&&desconto.cupom()!=null)cupons.reservar(c,conta.usuarioId(),desconto.cupom(),pedidoId);
            eventos.publicar(c,new Evento(pedidoId,conta,forma==null?Etapa.FINALIZADO:Etapa.ENTREGUE,vitrine));
            boolean pendente=pixVitrine&&desconto.total().signum()>0;
            StatusPagamento pagamento=pendente?StatusPagamento.AGUARDANDO_PIX:forma!=null||pixVitrine?StatusPagamento.PAGO:StatusPagamento.NA_ENTREGA;
            return new Venda(pedidoId,desconto,pagamento,pendente&&!pix.usaMercadoPago());
        });
        pedido.setId(venda.id());pedido.setRoletaLiberada(false);pedido.setStatusPagamento(venda.pagamento());pedido.setPixDemonstracao(venda.demo());
        pedido.setTaxaEntrega(venda.desconto().taxaEntrega());pedido.setDesconto(venda.desconto().desconto());pedido.setBrinde(venda.desconto().brinde());
        return venda.id();
    }
    @Override public void transicionar(long pedidoId,StatusPedido destino){
        ConnectionFactory.executarEmTransacao(c->{
            var conta=bloquearConta(c,jogo.contaDoPedido(c,pedidoId).orElse(null));
            StatusPedido atual=pedidos.bloquearStatus(c,pedidoId);
            if(!atual.podeTransicionarPara(destino))throw new IllegalStateException("Transição inválida: "+atual+" -> "+destino);
            if(destino!=StatusPedido.CANCELADO&&pedidos.statusPagamento(c,pedidoId)==StatusPagamento.AGUARDANDO_PIX)
                throw new IllegalStateException("Aguarde a confirmação PIX antes de preparar o pedido.");
            if(destino==StatusPedido.CANCELADO){
                var cobranca=pixDados.buscar(c,pedidoId,true).orElse(null);
                if(cobranca!=null&&cobranca.provedor()==PixPagamentoDAO.Provedor.MERCADO_PAGO&&cobranca.tentativa()!=null)
                    throw new IllegalStateException("PIX real iniciado: confira e trate a cobrança no Mercado Pago. O aplicativo não emite estornos.");
                cancelamento.cancelar(c,pedidoId);return null;
            }
            pedidos.atualizarStatus(c,pedidoId,destino);
            if(destino==StatusPedido.ENTREGUE)eventos.publicar(c,new Evento(pedidoId,conta,Etapa.ENTREGUE,false));
            return null;
        });
    }
    private GamificacaoDAO.Conta bloquearConta(Connection c,GamificacaoDAO.Conta conta)throws SQLException{
        return conta!=null&&usuarios.bloquearClienteAtivo(c,conta.usuarioId(),conta.contatoId())?conta:null;
    }

}
