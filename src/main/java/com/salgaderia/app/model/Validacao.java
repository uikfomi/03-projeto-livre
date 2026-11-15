package com.salgaderia.app.model;

import java.math.BigDecimal;
import java.util.Objects;

final class Validacao {
    private Validacao() { }
    static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(campo + " é obrigatório");
        return valor.trim();
    }
    static Long id(Long valor) {
        if (valor != null && valor <= 0) throw new IllegalArgumentException("id deve ser positivo");
        return valor;
    }
    static BigDecimal naoNegativo(BigDecimal valor, String campo) {
        Objects.requireNonNull(valor, campo + " é obrigatório");
        if (valor.signum() < 0) throw new IllegalArgumentException(campo + " não pode ser negativo");
        return valor;
    }
    static BigDecimal positivo(BigDecimal valor, String campo) {
        naoNegativo(valor, campo);
        if (valor.signum() == 0) throw new IllegalArgumentException(campo + " deve ser positivo");
        return valor;
    }
    static <T> T obrigatorio(T valor, String campo) {
        return Objects.requireNonNull(valor, campo + " é obrigatório");
    }
}
