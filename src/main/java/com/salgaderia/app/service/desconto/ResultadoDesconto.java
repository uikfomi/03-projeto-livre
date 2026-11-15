package com.salgaderia.app.service.desconto;

import java.math.BigDecimal;

public record ResultadoDesconto(BigDecimal subtotal, BigDecimal desconto, BigDecimal total, String estrategia, String cupom) { }
