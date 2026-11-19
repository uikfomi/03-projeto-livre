package com.salgaderia.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CupomRoleta(String codigo,String descricao,BigDecimal minimo,LocalDateTime validoAte) { }
