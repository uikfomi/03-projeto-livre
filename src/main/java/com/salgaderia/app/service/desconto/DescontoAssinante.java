package com.salgaderia.app.service.desconto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class DescontoAssinante implements EstrategiaDesconto {
    @Override public BigDecimal calcular(ContextoDesconto contexto) {
        return contexto.subtotal().multiply(contexto.percentualAssinante()).movePointLeft(2).setScale(2,RoundingMode.HALF_UP);
    }
    @Override public String nome() { return "ASSINANTE"; }
}
