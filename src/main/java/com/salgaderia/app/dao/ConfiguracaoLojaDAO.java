package com.salgaderia.app.dao;

import com.salgaderia.app.model.ConfiguracaoLoja;
import java.sql.Connection;
import java.sql.SQLException;

public interface ConfiguracaoLojaDAO {
    ConfiguracaoLoja buscar(Connection c, boolean bloquearAlteracoes) throws SQLException;
    void salvarExpediente(Connection c, ConfiguracaoLoja config) throws SQLException;
    void definirAberturaManual(Connection c, boolean aberta) throws SQLException;
}
