package com.salgaderia.app.dao;
import com.salgaderia.app.model.MovimentacaoEstoque;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
public interface MovimentacaoEstoqueDAO {
    long inserir(Connection c, MovimentacaoEstoque movimento, Long fornecedorId, String motivo, BigDecimal custoUnitario) throws SQLException;
    BigDecimal calcularPerdas(Connection c, LocalDateTime inicio, LocalDateTime fim) throws SQLException;
}
