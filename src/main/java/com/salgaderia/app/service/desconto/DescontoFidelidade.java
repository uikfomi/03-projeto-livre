package com.salgaderia.app.service.desconto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class DescontoFidelidade implements EstrategiaDesconto {
    @Override public BigDecimal calcular(ContextoDesconto contexto) {
        if (contexto.pedidosEntregues() < 3) return BigDecimal.ZERO;
        return contexto.subtotal().multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
    }
    @Override public String nome() { return "FIDELIDADE"; }
}
