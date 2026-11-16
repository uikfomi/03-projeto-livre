package com.salgaderia.app.model.comanda;

import java.math.BigDecimal;
import java.util.List;

public record Comanda(Mesa mesa,long atendimentoId,List<ItemComanda> itens,List<PagamentoComanda> pagamentos) {
    public Comanda { itens=List.copyOf(itens);pagamentos=List.copyOf(pagamentos); }
    public BigDecimal total(){return itens.stream().map(ItemComanda::subtotal).reduce(BigDecimal.ZERO,BigDecimal::add);}
    public BigDecimal pago(){return pagamentos.stream().map(PagamentoComanda::valor).reduce(BigDecimal.ZERO,BigDecimal::add);}
    public BigDecimal saldo(){return total().subtract(pago());}
}
