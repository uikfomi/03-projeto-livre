package com.salgaderia.app.service.desconto;

import com.salgaderia.app.model.PedidoItem;
import java.math.BigDecimal;
import java.util.List;

public record ContextoDesconto(List<PedidoItem> itens, long pedidosEntregues, Cupom cupom, BigDecimal percentualAssinante) {
    public ContextoDesconto(List<PedidoItem> itens,long pedidosEntregues,Cupom cupom) {
        this(itens,pedidosEntregues,cupom,BigDecimal.ZERO);
    }
    public ContextoDesconto {
        itens = List.copyOf(itens);
        if (pedidosEntregues < 0) throw new IllegalArgumentException("Quantidade inválida");
        if (percentualAssinante == null || percentualAssinante.signum()<0 || percentualAssinante.compareTo(BigDecimal.valueOf(100))>0)
            throw new IllegalArgumentException("Percentual de assinante inválido");
    }
    public BigDecimal subtotal() {
        return itens.stream().map(PedidoItem::calcularSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
