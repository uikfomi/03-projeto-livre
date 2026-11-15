package com.salgaderia.app.model;
import com.salgaderia.app.model.enums.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record PedidoFila(long id, String cliente, StatusPedido status, TipoEntrega entrega,
                         String enderecoEntrega, LocalDateTime criadoEm, BigDecimal total, String itens) { }
