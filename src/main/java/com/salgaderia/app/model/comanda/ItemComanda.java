package com.salgaderia.app.model.comanda;

import java.math.BigDecimal;

public record ItemComanda(long id,long produtoId,String produto,BigDecimal quantidade,BigDecimal precoUnitario,BigDecimal quantidadePaga) {
    public BigDecimal subtotal(){return quantidade.multiply(precoUnitario);}
    public BigDecimal quantidadeDisponivel(){return quantidade.subtract(quantidadePaga);}
}
