package com.salgaderia.app.service.desconto;

import java.math.BigDecimal;

public record ResultadoDesconto(BigDecimal subtotal, BigDecimal desconto, BigDecimal total, String estrategia, String cupom,
                                BigDecimal taxaEntrega,String brinde,boolean freteGratisVip) {
    public ResultadoDesconto(BigDecimal subtotal,BigDecimal desconto,BigDecimal total,String estrategia,String cupom){
        this(subtotal,desconto,total,estrategia,cupom,BigDecimal.ZERO,null,false);
    }
}
