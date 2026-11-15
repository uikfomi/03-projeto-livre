package com.salgaderia.app.service.impl;
import com.salgaderia.app.dao.*;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.PedidoVendaService;
import com.salgaderia.app.service.desconto.*;
import com.salgaderia.app.util.ConnectionFactory;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
public class PedidoVendaServiceImpl implements PedidoVendaService {
    private final PedidoDAO pedidos;private final ProdutoDAO produtos;private final MovimentacaoEstoqueDAO movimentos;
    private final CupomDAO cupons = new CupomDAOImpl();
    private final CalculadoraDescontos calculadora = new CalculadoraDescontos();
    public PedidoVendaServiceImpl(){this(new PedidoDAOImpl(),new ProdutoDAOImpl(),new MovimentacaoEstoqueDAOImpl());}
    public PedidoVendaServiceImpl(PedidoDAO pedidos,ProdutoDAO produtos,MovimentacaoEstoqueDAO movimentos){this.pedidos=Objects.requireNonNull(pedidos);this.produtos=Objects.requireNonNull(produtos);this.movimentos=Objects.requireNonNull(movimentos);}
    @Override public List<Pedido> listarDoCliente(UsuarioCliente usuario){
        if(usuario==null || usuario.getId()==null || !usuario.isAtivo())throw new IllegalArgumentException("Cliente autenticado obrigatório");
        return ConnectionFactory.executarEmTransacao(c->pedidos.listarDoCliente(c,usuario));
    }
    @Override public List<PedidoFila> listarFila(){return ConnectionFactory.executarEmTransacao(pedidos::listarFila);}
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
                Produto p=produtos.buscarPorId(c,id,false).orElseThrow(()->new IllegalArgumentException("Produto não encontrado"));
                if(p.getPrecoVenda().compareTo(item.getPrecoUnitario())!=0)throw new IllegalArgumentException("Preço do produto mudou");
                atuais.put(id,p);
            }
            return calcular(c,pedido,codigo,atuais);
        });
    }
    private ResultadoDesconto calcular(Connection c,Pedido pedido,String codigo,Map<Long,Produto> atuais) throws SQLException {
        List<PedidoItem> itens=new ArrayList<>();
        for(PedidoItem item:pedido.getItens())itens.add(new PedidoItem(null,atuais.get(item.getProduto().getId()),item.getQuantidade(),item.getPrecoUnitario()));
        long entregues=pedido.getCliente()==null||pedido.getCliente().getId()==null?0:pedidos.contarEntreguesDoCliente(c,pedido.getCliente().getId());
        Cupom cupom=null;
        if(codigo!=null&&!codigo.isBlank()){
            String normalizado=codigo.trim().toUpperCase(Locale.ROOT);
            cupom=cupons.buscarAtivo(c,normalizado).orElseThrow(()->new IllegalArgumentException("Cupom inválido ou inativo"));
            BigDecimal subtotal=itens.stream().map(PedidoItem::calcularSubtotal).reduce(BigDecimal.ZERO,BigDecimal::add);
            if(subtotal.compareTo(cupom.minimo())<0)throw new IllegalArgumentException("Valor mínimo do cupom: R$ "+cupom.minimo());
        }
        return calculadora.calcular(new ContextoDesconto(itens,entregues,cupom));
    }
    private long finalizarInterno(Pedido pedido,FormaPagamento forma,BigDecimal recebido,String codigo,ResultadoDesconto esperado){
        Objects.requireNonNull(pedido);if(pedido.getId()!=null||pedido.getStatus()!=StatusPedido.PENDENTE||pedido.getItens().isEmpty())throw new IllegalArgumentException("Pedido deve ser novo, pendente e conter itens");
        if(pedido.getUsuario()!=null && pedido.getUsuario().getId()==null)throw new IllegalArgumentException("Usuário sem ID");
        if(forma!=null && (!(pedido.getUsuario() instanceof UsuarioAdmin) || pedido.getUsuario().getId()==null || !pedido.getUsuario().isAtivo()))
            throw new IllegalArgumentException("Venda PDV exige administrador ativo");
        if(pedido.getCliente()!=null && pedido.getCliente().getId()==null)throw new IllegalArgumentException("Cliente sem ID");
        if(pedido.getUsuario() instanceof UsuarioCliente u && (pedido.getCliente()==null || !Objects.equals(u.getCliente().getId(),pedido.getCliente().getId())))throw new IllegalArgumentException("Cliente não corresponde ao usuário");
        Map<Long,BigDecimal> quantidades=new TreeMap<>();
        for(PedidoItem item:pedido.getItens()){
            Long id=item.getProduto().getId();if(id==null)throw new IllegalArgumentException("Produto sem ID");
            ProdutoEstoqueServiceImpl.validarQuantidade(item.getQuantidade());
            quantidades.merge(id,item.getQuantidade(),BigDecimal::add);
            ProdutoEstoqueServiceImpl.validarQuantidade(quantidades.get(id));
        }
        long id=ConnectionFactory.executarEmTransacao(c->{
            if(forma!=null){
                Usuario responsavel=new UsuarioDAOImpl(new ContatoDAOImpl()).buscarPorId(c,pedido.getUsuario().getId()).orElseThrow(()->new IllegalArgumentException("Administrador não encontrado"));
                if(!(responsavel instanceof UsuarioAdmin)||!responsavel.isAtivo())throw new IllegalArgumentException("Venda PDV exige administrador ativo");
            }
            Map<Long,Produto> bloqueados=new LinkedHashMap<>();Map<Long,BigDecimal> custos=new HashMap<>();
            for(var entrada:quantidades.entrySet()){
                Produto p=produtos.buscarPorId(c,entrada.getKey(),true).orElseThrow(()->new IllegalArgumentException("Produto não encontrado"));
                p.removerEstoque(entrada.getValue());bloqueados.put(entrada.getKey(),p);custos.put(entrada.getKey(),p.getCustoProducao());
            }
            for(PedidoItem item:pedido.getItens())if(item.getPrecoUnitario().compareTo(bloqueados.get(item.getProduto().getId()).getPrecoVenda())!=0)throw new IllegalArgumentException("Preço do pedido diverge do preço atual");
            ResultadoDesconto desconto=calcular(c,pedido,codigo,bloqueados);
            if(esperado!=null && (desconto.subtotal().compareTo(esperado.subtotal())!=0 || desconto.desconto().compareTo(esperado.desconto())!=0 || !desconto.estrategia().equals(esperado.estrategia()) || !Objects.equals(desconto.cupom(),esperado.cupom())))
                throw new IllegalArgumentException("Desconto alterado; recalcule e confirme o pedido");
            if(forma!=null && forma.isPermiteTroco() && (recebido==null || recebido.scale()>2 || recebido.compareTo(desconto.total())<0 || recebido.compareTo(new BigDecimal("99999999.99"))>0))
                throw new IllegalArgumentException("Valor recebido insuficiente ou inválido");
            long pedidoId=pedidos.inserir(c,pedido,custos);
            pedidos.registrarDesconto(c,pedidoId,desconto);
            if(forma!=null) pedidos.registrarPdv(c,pedidoId,forma,forma.isPermiteTroco()?recebido:desconto.total(),forma.isPermiteTroco()?recebido.subtract(desconto.total()):BigDecimal.ZERO);
            for(var entrada:bloqueados.entrySet()){
                Produto p=entrada.getValue();if(!produtos.atualizarEstoque(c,entrada.getKey(),p.getEstoqueAtual()))throw new IllegalStateException("Produto não atualizado");
                movimentos.inserir(c,new MovimentacaoEstoque(null,p,TipoMovimentacaoEstoque.SAIDA,quantidades.get(entrada.getKey()),"Pedido "+pedidoId,LocalDateTime.now()),null,"VENDA",p.getCustoProducao());
            }return pedidoId;
        });pedido.setId(id);return id;
    }
    @Override public void transicionar(long pedidoId,StatusPedido destino){
        ConnectionFactory.executarEmTransacao(c->{
            StatusPedido atual=pedidos.bloquearStatus(c,pedidoId);
            if(!atual.podeTransicionarPara(destino))throw new IllegalStateException("Transição inválida: "+atual+" -> "+destino);
            if(destino==StatusPedido.CANCELADO){
                for(var item:pedidos.quantidades(c,pedidoId).entrySet()){
                    Produto p=produtos.buscarPorId(c,item.getKey(),true).orElseThrow(()->new IllegalStateException("Produto não encontrado"));
                    p.adicionarEstoque(item.getValue());
                    if(!produtos.atualizarEstoque(c,p.getId(),p.getEstoqueAtual()))throw new IllegalStateException("Produto não atualizado");
                    movimentos.inserir(c,new MovimentacaoEstoque(null,p,TipoMovimentacaoEstoque.ENTRADA,item.getValue(),"Cancelamento do pedido "+pedidoId,LocalDateTime.now()),null,"AJUSTE",p.getCustoProducao());
                }
            }
            pedidos.atualizarStatus(c,pedidoId,destino);return null;
        });
    }

}
