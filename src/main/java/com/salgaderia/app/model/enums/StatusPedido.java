package com.salgaderia.app.model.enums;

public enum StatusPedido {
    PENDENTE, PREPARANDO, PRONTO, ENTREGUE, CANCELADO, SAIU_ENTREGA;

    public StatusPedido proximo(TipoEntrega modalidade) {
        return switch (this) {
            case PENDENTE -> PREPARANDO;
            case PREPARANDO -> PRONTO;
            case PRONTO -> modalidade == TipoEntrega.ENTREGA ? SAIU_ENTREGA : ENTREGUE;
            case SAIU_ENTREGA -> modalidade == TipoEntrega.ENTREGA ? ENTREGUE : null;
            case ENTREGUE, CANCELADO -> null;
        };
    }

    public boolean podeTransicionarPara(StatusPedido destino, TipoEntrega modalidade) {
        return destino != null && (destino == proximo(modalidade) ||
                (destino == CANCELADO && proximo(modalidade) != null));
    }
}
