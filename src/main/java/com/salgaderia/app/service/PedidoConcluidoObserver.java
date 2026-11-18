package com.salgaderia.app.service;

import com.salgaderia.app.dao.GamificacaoDAO.Conta;
import java.sql.Connection;
import java.sql.SQLException;

/** Observador síncrono: grava efeitos na mesma transação JDBC que gerou o evento. */
@FunctionalInterface
public interface PedidoConcluidoObserver {
    enum Etapa { FINALIZADO, ENTREGUE, CANCELADO }
    record Evento(long pedidoId,Conta cliente,Etapa etapa,boolean vitrine) { }
    void aoAlterarPedido(Connection c,Evento evento) throws SQLException;
}
