package com.salgaderia.app.service.desconto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class CalculadoraDescontos {
    private final List<EstrategiaDesconto> estrategias = List.of(
            new DescontoCupomPercentual(), new DescontoCentoSalgados(), new DescontoFidelidade());

    public ResultadoDesconto calcular(ContextoDesconto contexto) {
        BigDecimal subtotal = contexto.subtotal().setScale(2, RoundingMode.HALF_UP);
        BigDecimal melhor = BigDecimal.ZERO.setScale(2);
        String nome = "NENHUM";
        for (EstrategiaDesconto estrategia : estrategias) {
            BigDecimal valor = estrategia.calcular(contexto);
            if (valor.compareTo(melhor) > 0) { melhor = valor; nome = estrategia.nome(); }
        }
        return new ResultadoDesconto(subtotal, melhor, subtotal.subtract(melhor), nome,
                nome.equals("CUPOM") ? contexto.cupom().codigo() : null);
    }
}
