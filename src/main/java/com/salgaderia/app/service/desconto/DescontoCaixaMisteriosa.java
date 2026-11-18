package com.salgaderia.app.service.desconto;

import com.salgaderia.app.model.PedidoItem;
import com.salgaderia.app.service.PersonalizacaoService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Garante o desconto da caixa como candidato não cumulativo, com arredondamento por caixa. */
public final class DescontoCaixaMisteriosa implements EstrategiaDesconto {
    @Override public BigDecimal calcular(ContextoDesconto contexto) { return calcularCaixas(contexto.itens()); }

    public static BigDecimal calcularCaixas(List<PedidoItem> itens) {
        return PersonalizacaoService.grupos(itens).values().stream()
                .filter(grupo -> grupo.get(0).getPersonalizacao().tipo().caixa())
                .map(grupo -> grupo.stream().map(PedidoItem::calcularSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override public String nome() { return "CAIXA_MISTERIOSA"; }
}
