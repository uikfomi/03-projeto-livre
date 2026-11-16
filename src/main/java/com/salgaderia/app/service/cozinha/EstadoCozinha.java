package com.salgaderia.app.service.cozinha;

import com.salgaderia.app.model.enums.TipoEntrega;

/** Estado visual do pedido; a estimativa é apenas informativa. */
public interface EstadoCozinha {
    String mensagem(TipoEntrega modalidade);
    double progresso();
    boolean aguardaCozinha();
}
