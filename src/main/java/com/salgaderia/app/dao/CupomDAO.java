package com.salgaderia.app.dao;

import com.salgaderia.app.service.desconto.Cupom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface CupomDAO {
    Optional<Cupom> buscarAtivo(Connection conexao, String codigo) throws SQLException;
}
