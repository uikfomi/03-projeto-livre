package com.salgaderia.app.model;

import java.time.LocalTime;
import java.util.Objects;

public record ConfiguracaoLoja(boolean abertaManualmente, LocalTime abertura, LocalTime fechamento,
                               boolean respeitarHorarioAutomatico) {
    public ConfiguracaoLoja {
        Objects.requireNonNull(abertura);
        Objects.requireNonNull(fechamento);
        if (abertura.equals(fechamento)) throw new IllegalArgumentException("Abertura e fechamento devem ser diferentes.");
    }
}
