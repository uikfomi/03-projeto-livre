package com.salgaderia.app.model.comanda;

public enum StatusMesa {
    LIVRE, OCUPADA;
    public boolean podeAbrir(){return this==LIVRE;}
    public boolean permiteLancamento(){return this==OCUPADA;}
}
