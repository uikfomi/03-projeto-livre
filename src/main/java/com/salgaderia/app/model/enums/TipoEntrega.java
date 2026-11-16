package com.salgaderia.app.model.enums;

public enum TipoEntrega {
    RETIRADA, ENTREGA, LOCAL;

    public boolean exigeEndereco() { return this == ENTREGA; }
}
