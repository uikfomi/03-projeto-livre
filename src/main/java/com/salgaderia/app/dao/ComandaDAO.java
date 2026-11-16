package com.salgaderia.app.dao;

import com.salgaderia.app.model.comanda.*;
import com.salgaderia.app.model.enums.FormaPagamento;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ComandaDAO {
    List<Mesa> listarMesas(Connection c) throws SQLException;
    Mesa bloquearMesa(Connection c,long id) throws SQLException;
    long abrir(Connection c,long mesaId) throws SQLException;
    Optional<Comanda> buscarAberta(Connection c,Mesa mesa) throws SQLException;
    void inserirItem(Connection c,long atendimento,long produto,BigDecimal quantidade,BigDecimal preco,BigDecimal custo) throws SQLException;
    void inserirPagamento(Connection c,long atendimento,String pessoa,FormaPagamento forma,BigDecimal valor,Long item,BigDecimal quantidade) throws SQLException;
    long concluirVenda(Connection c,long atendimento,long usuarioId) throws SQLException;
    void fechar(Connection c,long mesa,long atendimento,Long pedido) throws SQLException;
}
