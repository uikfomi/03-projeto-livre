package com.salgaderia.app.service;
import com.salgaderia.app.model.Pedido;
public interface PedidoVendaService {
    java.util.List<Pedido> listarDoCliente(com.salgaderia.app.model.UsuarioCliente usuario);
    java.util.List<com.salgaderia.app.model.PedidoFila> listarFila();
    long finalizarPdv(Pedido pedido,com.salgaderia.app.model.enums.FormaPagamento forma,java.math.BigDecimal recebido);
    /** Compartilhado pelas vendas de PDV e vitrine; grava e baixa estoque atomicamente. */
    long finalizar(Pedido pedido);
    void transicionar(long pedidoId, com.salgaderia.app.model.enums.StatusPedido destino);
}
