package com.salgaderia.app.dao.impl;
import com.salgaderia.app.dao.AvaliacaoDAO;
import com.salgaderia.app.model.*;
import java.sql.*;
public class AvaliacaoDAOImpl implements AvaliacaoDAO {
    @Override public java.util.Map<Long,java.math.BigDecimal> mediasPorProduto(Connection c) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT produto_id,AVG(nota) AS media FROM avaliacoes GROUP BY produto_id");ResultSet rs=ps.executeQuery()){
            java.util.Map<Long,java.math.BigDecimal> medias=new java.util.HashMap<>();
            while(rs.next())medias.put(rs.getLong("produto_id"),rs.getBigDecimal("media"));return medias;
        }
    }
    @Override public java.util.List<com.salgaderia.app.model.AvaliacaoResumo> listar(Connection c) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT a.id,u.nome cliente,p.nome produto,a.nota,a.comentario,a.resposta_admin,a.foto_url FROM avaliacoes a JOIN usuarios u ON u.id=a.usuario_id JOIN produtos p ON p.id=a.produto_id ORDER BY a.criado_em DESC,a.id DESC");ResultSet rs=ps.executeQuery()){
            java.util.List<AvaliacaoResumo> resultado=new java.util.ArrayList<>();while(rs.next())resultado.add(new AvaliacaoResumo(rs.getLong("id"),rs.getString("cliente"),rs.getString("produto"),rs.getInt("nota"),rs.getString("comentario"),rs.getString("resposta_admin"),rs.getString("foto_url")));return resultado;
        }
    }
    @Override public java.util.List<AvaliacaoFoto> fotosRecentes(Connection c) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT a.id,p.id produto_id,p.nome,a.nota,a.foto_url FROM avaliacoes a JOIN produtos p ON p.id=a.produto_id WHERE a.foto_url IS NOT NULL ORDER BY a.criado_em DESC,a.id DESC LIMIT 12");ResultSet rs=ps.executeQuery()){
            java.util.List<AvaliacaoFoto> resultado=new java.util.ArrayList<>();
            while(rs.next())resultado.add(new AvaliacaoFoto(rs.getLong("id"),rs.getLong("produto_id"),rs.getString("nome"),rs.getInt("nota"),rs.getString("foto_url")));
            return resultado;
        }
    }
    @Override public String bloquearFoto(Connection c,long id) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT foto_url FROM avaliacoes WHERE id=? FOR UPDATE")){
            ps.setLong(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getString(1):null;}
        }
    }
    @Override public long inserir(Connection c,Avaliacao a) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("INSERT INTO avaliacoes(usuario_id,produto_id,nota,comentario,criado_em,foto_url) VALUES (?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
            ps.setLong(1,a.getUsuario().getId());ps.setLong(2,a.getProduto().getId());ps.setInt(3,a.getNota());ps.setString(4,a.getComentario());ps.setTimestamp(5,Timestamp.valueOf(a.getCriadoEm()));ps.setString(6,a.getFotoUrl());ps.executeUpdate();
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
