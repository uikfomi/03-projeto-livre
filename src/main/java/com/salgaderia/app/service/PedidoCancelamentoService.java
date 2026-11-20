package com.salgaderia.app.service;

import com.salgaderia.app.dao.*;
import com.salgaderia.app.dao.impl.CupomDAOImpl;
import com.salgaderia.app.model.MovimentacaoEstoque;
import com.salgaderia.app.model.Produto;
import com.salgaderia.app.model.enums.StatusPedido;
import com.salgaderia.app.model.enums.TipoMovimentacaoEstoque;
import com.salgaderia.app.service.PedidoConcluidoObserver.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

/** Reverte a reserva sob o bloqueio do pedido; compartilhado pelo cancelamento e pelo PIX rejeitado. */
public final class PedidoCancelamentoService {
    private final PedidoDAO pedidos;
    private final ProdutoDAO produtos;
    private final MovimentacaoEstoqueDAO movimentos;
    private final CupomDAO cupons=new CupomDAOImpl();
    private final BarramentoPedido eventos;

    public PedidoCancelamentoService(PedidoDAO pedidos,ProdutoDAO produtos,MovimentacaoEstoqueDAO movimentos,BarramentoPedido eventos){
        this.pedidos=pedidos;this.produtos=produtos;this.movimentos=movimentos;this.eventos=eventos;
    }

    public void cancelar(Connection c,long pedidoId)throws SQLException{
        for(var item:pedidos.quantidadesBaixadas(c,pedidoId).entrySet()){
            Produto p=produtos.buscarPorId(c,item.getKey(),true).orElseThrow(()->new IllegalStateException("Produto não encontrado"));
            p.adicionarEstoque(item.getValue());
            if(!produtos.atualizarEstoque(c,p.getId(),p.getEstoqueAtual()))throw new IllegalStateException("Produto não atualizado");
            movimentos.inserir(c,new MovimentacaoEstoque(null,p,TipoMovimentacaoEstoque.ENTRADA,item.getValue(),
                    "Cancelamento do pedido "+pedidoId,LocalDateTime.now()),null,"AJUSTE",p.getCustoProducao());
        }
        pedidos.atualizarStatus(c,pedidoId,StatusPedido.CANCELADO);
        cupons.liberarReserva(c,pedidoId);
        eventos.publicar(c,new Evento(pedidoId,null,Etapa.CANCELADO,false));
    }
}
