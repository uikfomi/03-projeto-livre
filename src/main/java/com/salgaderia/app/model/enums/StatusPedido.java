package com.salgaderia.app.model.enums;

public enum StatusPedido {
    PENDENTE, PREPARANDO, PRONTO, ENTREGUE, CANCELADO;

    /** Pedidos podem avançar sequencialmente ou ser cancelados antes da entrega. */
    public boolean podeTransicionarPara(StatusPedido destino) {
        if (destino == null) {
            return false;
        }
        return switch (this) {
            case PENDENTE -> destino == PREPARANDO || destino == CANCELADO;
            case PREPARANDO -> destino == PRONTO || destino == CANCELADO;
            case PRONTO -> destino == ENTREGUE || destino == CANCELADO;
            case ENTREGUE, CANCELADO -> false;
        };
    }
}
