package com.salgaderia.app.service.desconto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Aplica o percentual de um cupom ativo e elegível recebido do DAO. */
public final class DescontoCupomPercentual implements EstrategiaDesconto {
    @Override public BigDecimal calcular(ContextoDesconto contexto) {
        Cupom cupom = contexto.cupom();
        if (cupom == null || cupom.beneficio()!=Cupom.Beneficio.PERCENTUAL || contexto.subtotal().compareTo(cupom.minimo()) < 0) return BigDecimal.ZERO;
        return contexto.subtotal().multiply(cupom.percentual()).movePointLeft(2).setScale(2, RoundingMode.HALF_UP);
    }
    @Override public String nome() { return "CUPOM"; }
}
