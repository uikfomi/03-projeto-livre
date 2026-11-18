package com.salgaderia.app.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class BarramentoPedido {
    private final List<PedidoConcluidoObserver> observadores;
    public BarramentoPedido(PedidoConcluidoObserver... observadores) { this.observadores=List.of(observadores); }
    public void publicar(Connection c,PedidoConcluidoObserver.Evento evento)throws SQLException {
        for(PedidoConcluidoObserver observador:observadores)observador.aoAlterarPedido(c,evento);
    }
}
