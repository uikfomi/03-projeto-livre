package com.salgaderia.app.service;

import com.salgaderia.app.model.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Monta um combo com duas escolhas distintas do cardápio e uma bebida. */
public final class ComboBuilder {
    private Produto principal, acompanhamento, bebida;
    public ComboBuilder principal(Produto p){principal=p;return this;}
    public ComboBuilder acompanhamento(Produto p){acompanhamento=p;return this;}
    public ComboBuilder bebida(Produto p){bebida=p;return this;}

    public List<PedidoItem> montar(){
        if(principal==null||acompanhamento==null||bebida==null||
                PersonalizacaoService.bebida(principal)||PersonalizacaoService.bebida(acompanhamento)||!PersonalizacaoService.bebida(bebida))
            throw new IllegalArgumentException("Escolha o principal, o acompanhamento e uma bebida do cardápio.");
        var grupo=new PersonalizacaoItem(UUID.randomUUID(),PersonalizacaoItem.Tipo.COMBO_SIMPLES,Set.of());
        return PersonalizacaoService.descrever(List.of(principal,acompanhamento,bebida).stream()
                .map(p->new PedidoItem(null,p,BigDecimal.ONE,p.getPrecoVenda(),null,grupo)).toList());
    }
}
