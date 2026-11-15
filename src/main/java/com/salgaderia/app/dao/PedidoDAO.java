package com.salgaderia.app.dao;
import com.salgaderia.app.model.Pedido;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;
public interface PedidoDAO {
    long inserir(Connection c, Pedido pedido, Map<Long,BigDecimal> custos) throws SQLException;
    com.salgaderia.app.model.enums.StatusPedido bloquearStatus(Connection c,long id) throws SQLException;
    Map<Long,BigDecimal> quantidades(Connection c,long id) throws SQLException;
    void atualizarStatus(Connection c,long id,com.salgaderia.app.model.enums.StatusPedido status) throws SQLException;
    boolean possuiPedidoEntregue(Connection c,long usuarioId,long produtoId) throws SQLException;
    BigDecimal[] somarVendasEntregues(Connection c,LocalDateTime inicio,LocalDateTime fim) throws SQLException;
}
