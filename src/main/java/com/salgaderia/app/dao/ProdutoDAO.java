package com.salgaderia.app.dao;
import com.salgaderia.app.model.Produto;
import java.sql.*;
import java.util.*;
public interface ProdutoDAO {
    long inserir(Connection conexao, Produto produto) throws SQLException;
    boolean atualizar(Connection conexao, Produto produto) throws SQLException;
    boolean excluir(Connection conexao, long id) throws SQLException;
    Optional<Produto> buscarPorId(Connection conexao, long id, boolean bloquear) throws SQLException;
    List<Produto> listar(Connection conexao) throws SQLException;
    boolean atualizarEstoque(Connection conexao, long id, java.math.BigDecimal quantidade) throws SQLException;
}
