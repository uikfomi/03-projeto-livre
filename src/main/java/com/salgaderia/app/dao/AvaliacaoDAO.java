package com.salgaderia.app.dao;
import com.salgaderia.app.model.Avaliacao;
import java.sql.*;
public interface AvaliacaoDAO {
    long inserir(Connection c,Avaliacao avaliacao) throws SQLException;
    boolean responder(Connection c,long id,String resposta) throws SQLException;
    boolean excluir(Connection c,long id) throws SQLException;
}
