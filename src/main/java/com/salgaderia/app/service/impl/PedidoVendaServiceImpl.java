package com.salgaderia.app.service.impl;
import com.salgaderia.app.dao.*;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.PedidoVendaService;
import com.salgaderia.app.util.ConnectionFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
public class PedidoVendaServiceImpl implements PedidoVendaService {
    private final PedidoDAO pedidos;private final ProdutoDAO produtos;private final MovimentacaoEstoqueDAO movimentos;
    public PedidoVendaServiceImpl(){this(new PedidoDAOImpl(),new ProdutoDAOImpl(),new MovimentacaoEstoqueDAOImpl());}
    public PedidoVendaServiceImpl(PedidoDAO pedidos,ProdutoDAO produtos,MovimentacaoEstoqueDAO movimentos){this.pedidos=Objects.requireNonNull(pedidos);this.produtos=Objects.requireNonNull(produtos);this.movimentos=Objects.requireNonNull(movimentos);}
    @Override public List<Pedido> listarDoCliente(UsuarioCliente usuario){
        if(usuario==null || usuario.getId()==null || !usuario.isAtivo())throw new IllegalArgumentException("Cliente autenticado obrigatório");
        return ConnectionFactory.executarEmTransacao(c->pedidos.listarDoCliente(c,usuario));
    }
    @Override public long finalizar(Pedido pedido){
        Objects.requireNonNull(pedido);if(pedido.getId()!=null||pedido.getStatus()!=StatusPedido.PENDENTE||pedido.getItens().isEmpty())throw new IllegalArgumentException("Pedido deve ser novo, pendente e conter itens");
        if(pedido.getUsuario()!=null && pedido.getUsuario().getId()==null)throw new IllegalArgumentException("Usuário sem ID");
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
            Map<Long,Produto> bloqueados=new LinkedHashMap<>();Map<Long,BigDecimal> custos=new HashMap<>();
            for(var entrada:quantidades.entrySet()){
                Produto p=produtos.buscarPorId(c,entrada.getKey(),true).orElseThrow(()->new IllegalArgumentException("Produto não encontrado"));
                p.removerEstoque(entrada.getValue());bloqueados.put(entrada.getKey(),p);custos.put(entrada.getKey(),p.getCustoProducao());
            }
            for(PedidoItem item:pedido.getItens())if(item.getPrecoUnitario().compareTo(bloqueados.get(item.getProduto().getId()).getPrecoVenda())!=0)throw new IllegalArgumentException("Preço do pedido diverge do preço atual");
            long pedidoId=pedidos.inserir(c,pedido,custos);
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
