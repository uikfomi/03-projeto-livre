package com.salgaderia.app.dao.impl;
import com.salgaderia.app.dao.AvaliacaoDAO;
import com.salgaderia.app.model.Avaliacao;
import java.sql.*;
public class AvaliacaoDAOImpl implements AvaliacaoDAO {
    @Override public long inserir(Connection c,Avaliacao a) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("INSERT INTO avaliacoes(usuario_id,produto_id,nota,comentario,criado_em) VALUES (?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
            ps.setLong(1,a.getUsuario().getId());ps.setLong(2,a.getProduto().getId());ps.setInt(3,a.getNota());ps.setString(4,a.getComentario());ps.setTimestamp(5,Timestamp.valueOf(a.getCriadoEm()));ps.executeUpdate();
            try(ResultSet rs=ps.getGeneratedKeys()){if(!rs.next())throw new SQLException("ID de avaliação não retornado");return rs.getLong(1);}
        }
    }
    @Override public boolean responder(Connection c,long id,String resposta) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE avaliacoes SET resposta_admin=? WHERE id=?")){ps.setString(1,resposta);ps.setLong(2,id);return ps.executeUpdate()==1;}
    }
    @Override public boolean excluir(Connection c,long id) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("DELETE FROM avaliacoes WHERE id=?")){ps.setLong(1,id);return ps.executeUpdate()==1;}
    }
}
