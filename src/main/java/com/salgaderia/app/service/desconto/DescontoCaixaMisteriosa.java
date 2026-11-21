package com.salgaderia.app.service.desconto;

import com.salgaderia.app.model.PedidoItem;
import com.salgaderia.app.service.PersonalizacaoService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Ajuste obrigatório que transforma o valor de catálogo de cada caixa em seu preço fechado. */
public final class DescontoCaixaMisteriosa {
    private DescontoCaixaMisteriosa(){ }
    public static BigDecimal calcularCaixas(List<PedidoItem> itens){
        return PersonalizacaoService.grupos(itens).values().stream()
                .filter(g->g.get(0).getPersonalizacao().tipo().orcamento()!=null)
                .map(g->PersonalizacaoService.subtotal(g).subtract(g.get(0).getPersonalizacao().tipo().orcamento()))
                .reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,RoundingMode.HALF_UP);
    }
}
