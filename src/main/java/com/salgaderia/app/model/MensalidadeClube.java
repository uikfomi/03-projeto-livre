package com.salgaderia.app.model;

import java.math.BigDecimal;

/** Valor e benefício congelados ao solicitar a mensalidade. */
public record MensalidadeClube(long id, long usuarioId, String cliente, long planoId, String plano,
                               BigDecimal valor, BigDecimal descontoPercentual) { }
