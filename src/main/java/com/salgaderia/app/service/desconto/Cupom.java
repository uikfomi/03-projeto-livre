package com.salgaderia.app.service.desconto;

import java.math.BigDecimal;
import java.util.Objects;

/** Cupom carregado do banco; percentual expresso de 0 a 100. */
public record Cupom(String codigo, BigDecimal percentual, BigDecimal minimo, Beneficio beneficio,
                    String brinde, BigDecimal valorBeneficio) {
    public enum Beneficio { PERCENTUAL, FRETE, BRINDE, PONTOS_DOBRO }
    public Cupom(String codigo,BigDecimal percentual,BigDecimal minimo){
        this(codigo,percentual,minimo,Beneficio.PERCENTUAL,null,BigDecimal.ZERO);
    }
    public Cupom {
        Objects.requireNonNull(codigo);
        Objects.requireNonNull(percentual);
        Objects.requireNonNull(minimo);
        Objects.requireNonNull(beneficio);
        Objects.requireNonNull(valorBeneficio);
        if (codigo.isBlank() || minimo.signum() < 0 || valorBeneficio.signum()<0 ||
                (beneficio==Beneficio.PERCENTUAL ? percentual.signum()<=0||percentual.compareTo(BigDecimal.valueOf(100))>0 : percentual.signum()!=0) ||
                (beneficio==Beneficio.BRINDE && (brinde==null||brinde.isBlank()||valorBeneficio.signum()<=0)))
            throw new IllegalArgumentException("Cupom inválido");
    }
}
