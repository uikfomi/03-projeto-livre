package com.salgaderia.app.dao.impl;

import com.salgaderia.app.dao.CupomDAO;
import com.salgaderia.app.service.desconto.Cupom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public final class CupomDAOImpl implements CupomDAO {
    @Override public Optional<Cupom> buscarAtivo(Connection conexao, String codigo) throws SQLException {
        try (var ps = conexao.prepareStatement("SELECT codigo,percentual,valor_minimo FROM cupons WHERE codigo=? AND ativo=TRUE")) {
            ps.setString(1, codigo);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(new Cupom(rs.getString(1), rs.getBigDecimal(2), rs.getBigDecimal(3))) : Optional.empty();
            }
        }
    }
}
