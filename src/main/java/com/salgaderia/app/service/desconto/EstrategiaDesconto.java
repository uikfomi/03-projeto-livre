package com.salgaderia.app.service.desconto;

import java.math.BigDecimal;

public interface EstrategiaDesconto {
    BigDecimal calcular(ContextoDesconto contexto);
    String nome();
}
