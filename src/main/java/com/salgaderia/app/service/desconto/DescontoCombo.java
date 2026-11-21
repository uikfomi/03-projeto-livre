package com.salgaderia.app.service.desconto;

import com.salgaderia.app.model.PersonalizacaoItem.Tipo;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Aplica 10% sobre os itens dos combos simples, como opção não cumulativa. */
public final class DescontoCombo implements EstrategiaDesconto {
    @Override public BigDecimal calcular(ContextoDesconto contexto){
        return contexto.itens().stream().filter(i->i.getPersonalizacao()!=null&&i.getPersonalizacao().tipo()==Tipo.COMBO_SIMPLES)
                .map(i->i.calcularSubtotal()).reduce(BigDecimal.ZERO,BigDecimal::add)
                .multiply(new BigDecimal("0.10")).setScale(2,RoundingMode.HALF_UP);
    }
    @Override public String nome(){return "COMBO";}
}
