package com.salgaderia.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Vigência de mensalidade já paga; o limite é exclusivo. */
public record AssinaturaCliente(long usuarioId, String cliente, long planoId, String plano, LocalDate validaAte,
                                BigDecimal descontoVigente, boolean cancelada) {
    public boolean ativa(LocalDate hoje) { return !cancelada && validaAte != null && hoje.isBefore(validaAte); }
    public boolean renovavel(LocalDate hoje) { return !ativa(hoje) || !validaAte.isAfter(hoje.plusDays(7)); }
}
