package com.salgaderia.app.dao.impl;
import com.salgaderia.app.dao.MovimentacaoEstoqueDAO;
import com.salgaderia.app.model.MovimentacaoEstoque;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
public class MovimentacaoEstoqueDAOImpl implements MovimentacaoEstoqueDAO {
    @Override public long inserir(Connection c,MovimentacaoEstoque m,Long fornecedorId,String motivo,BigDecimal custo) throws SQLException {
        if(m.getProduto().getId()==null)throw new IllegalArgumentException("Produto sem ID");
        try(PreparedStatement ps=c.prepareStatement("INSERT INTO movimentacoes_estoque(produto_id,tipo,quantidade,observacao,criado_em,fornecedor_id,motivo,custo_unitario) VALUES (?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
            ps.setLong(1,m.getProduto().getId());ps.setString(2,m.getTipo().name());ps.setBigDecimal(3,m.getQuantidade());ps.setString(4,m.getObservacao());ps.setTimestamp(5,Timestamp.valueOf(m.getCriadoEm()));
            if(fornecedorId==null)ps.setNull(6,Types.BIGINT);else ps.setLong(6,fornecedorId); ps.setString(7,motivo); ps.setBigDecimal(8,custo);
            ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){if(!rs.next())throw new SQLException("ID de movimentação não retornado");return rs.getLong(1);}
        }
    }
    @Override public BigDecimal calcularPerdas(Connection c,LocalDateTime inicio,LocalDateTime fim) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT COALESCE(SUM(quantidade*custo_unitario),0), SUM(custo_unitario IS NULL) FROM movimentacoes_estoque WHERE motivo='PERDA' AND criado_em>=? AND criado_em<?")){
            ps.setTimestamp(1,Timestamp.valueOf(inicio));ps.setTimestamp(2,Timestamp.valueOf(fim));try(ResultSet rs=ps.executeQuery()){rs.next();if(rs.getLong(2)>0)throw new SQLException("Perdas sem custo histórico");return rs.getBigDecimal(1);}
        }
    }
}
