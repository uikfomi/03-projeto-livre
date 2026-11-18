package com.salgaderia.app.dao.impl;

import com.salgaderia.app.dao.CupomDAO;
import com.salgaderia.app.service.desconto.Cupom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public final class CupomDAOImpl implements CupomDAO {
    @Override public Optional<Cupom> buscarAtivo(Connection conexao,String codigo,Long usuarioVitrine) throws SQLException {
        try (var ps = conexao.prepareStatement("""
                SELECT codigo,percentual,valor_minimo FROM cupons WHERE codigo=? AND ativo=TRUE
                  AND (usuario_exclusivo_id IS NULL OR usuario_exclusivo_id=?)
                """)) {
            ps.setString(1, codigo);
            ps.setObject(2,usuarioVitrine,java.sql.Types.BIGINT);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(new Cupom(rs.getString(1), rs.getBigDecimal(2), rs.getBigDecimal(3))) : Optional.empty();
            }
        }
    }
    @Override public void encerrarCuponsExclusivos(Connection c,long usuarioVitrine)throws SQLException{
        try(var ps=c.prepareStatement("UPDATE cupons SET ativo=FALSE WHERE usuario_exclusivo_id=? AND ativo")){
            ps.setLong(1,usuarioVitrine);ps.executeUpdate();
        }
    }
}
