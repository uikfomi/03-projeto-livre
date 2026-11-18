package com.salgaderia.app.service.desconto;

import com.salgaderia.app.model.FichaVitrine;
import com.salgaderia.app.model.PedidoItem;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class DescontoCentoSalgados implements EstrategiaDesconto {
    @Override public BigDecimal calcular(ContextoDesconto contexto) {
        var elegiveis = contexto.itens().stream().filter(i -> FichaVitrine.categoriaSalgado(i.getProduto().getCategoria())).toList();
        BigDecimal quantidade = elegiveis.stream().map(PedidoItem::getQuantidade).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (quantidade.compareTo(new BigDecimal("100")) < 0) return BigDecimal.ZERO;
        return elegiveis.stream().map(PedidoItem::calcularSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
    }
    @Override public String nome() { return "CENTO"; }
}
