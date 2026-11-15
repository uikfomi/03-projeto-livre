package com.salgaderia.app.service.desconto;

import java.math.BigDecimal;
import java.util.Objects;

/** Cupom carregado do banco; percentual expresso de 0 a 100. */
public record Cupom(String codigo, BigDecimal percentual, BigDecimal minimo) {
    public Cupom {
        Objects.requireNonNull(codigo);
        Objects.requireNonNull(percentual);
        Objects.requireNonNull(minimo);
        if (codigo.isBlank() || percentual.signum() <= 0 || percentual.compareTo(new BigDecimal("100")) > 0 || minimo.signum() < 0)
            throw new IllegalArgumentException("Cupom inválido");
    }
}
