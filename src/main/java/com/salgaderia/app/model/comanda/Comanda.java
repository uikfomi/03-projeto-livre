package com.salgaderia.app.model.comanda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record Comanda(Mesa mesa,long atendimentoId,List<ItemComanda> itens,List<PagamentoComanda> pagamentos) {
    public Comanda { itens=List.copyOf(itens);pagamentos=List.copyOf(pagamentos); }
    public BigDecimal total(){return itens.stream().map(ItemComanda::subtotal).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,RoundingMode.HALF_UP);}
    public BigDecimal pago(){return pagamentos.stream().map(PagamentoComanda::valor).reduce(BigDecimal.ZERO,BigDecimal::add);}
    public BigDecimal saldo(){return total().subtract(pago());}
    public boolean aceitaPagamento(BigDecimal valor){
        return valor!=null&&valor.scale()<=2&&valor.signum()>0&&valor.compareTo(saldo())<=0;
    }
}
