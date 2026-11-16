package com.salgaderia.app.model.comanda;

import com.salgaderia.app.model.enums.FormaPagamento;
import java.math.BigDecimal;

public record PagamentoComanda(long id,String pessoa,FormaPagamento forma,BigDecimal valor,Long itemId,BigDecimal quantidade) { }
