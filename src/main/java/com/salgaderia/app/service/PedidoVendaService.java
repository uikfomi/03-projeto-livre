package com.salgaderia.app.service;
import com.salgaderia.app.model.Pedido;
public interface PedidoVendaService {
    /** Compartilhado pelas vendas de PDV e vitrine; grava e baixa estoque atomicamente. */
    long finalizar(Pedido pedido);
    void transicionar(long pedidoId, com.salgaderia.app.model.enums.StatusPedido destino);
}
