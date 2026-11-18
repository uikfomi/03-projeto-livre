package com.salgaderia.app.model.comanda;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ItemComanda(long id,long produtoId,String produto,BigDecimal quantidade,BigDecimal precoUnitario,BigDecimal quantidadePaga) {
    public BigDecimal subtotal(){return quantidade.multiply(precoUnitario);}
    public BigDecimal quantidadeDisponivel(){return quantidade.subtract(quantidadePaga);}
    public BigDecimal cotarPagamento(BigDecimal unidades){
        if(unidades==null||unidades.signum()<=0||unidades.stripTrailingZeros().scale()>0||unidades.compareTo(quantidadeDisponivel())>0)
            throw new IllegalArgumentException("Informe unidades inteiras que ainda não foram pagas.");
        return precoUnitario.multiply(unidades).setScale(2,RoundingMode.HALF_UP);
    }
}
