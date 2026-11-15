package com.salgaderia.app.service.desconto;

import com.salgaderia.app.model.PedidoItem;
import java.math.BigDecimal;
import java.util.List;

public record ContextoDesconto(List<PedidoItem> itens, long pedidosEntregues, Cupom cupom) {
    public ContextoDesconto {
        itens = List.copyOf(itens);
        if (pedidosEntregues < 0) throw new IllegalArgumentException("Quantidade inválida");
    }
    public BigDecimal subtotal() {
        return itens.stream().map(PedidoItem::calcularSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
