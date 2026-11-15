package com.salgaderia.app.dao.impl;
import com.salgaderia.app.dao.PedidoDAO;
import com.salgaderia.app.model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;
public class PedidoDAOImpl implements PedidoDAO {
    @Override public long inserir(Connection c,Pedido pedido,Map<Long,BigDecimal> custos) throws SQLException {
        long id;
        try(PreparedStatement ps=c.prepareStatement("INSERT INTO pedidos(usuario_id,contato_id,status,tipo_entrega,endereco_entrega,criado_em) VALUES (?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
            if(pedido.getUsuario()==null)ps.setNull(1,Types.BIGINT);else ps.setLong(1,pedido.getUsuario().getId());
            if(pedido.getCliente()==null)ps.setNull(2,Types.BIGINT);else ps.setLong(2,pedido.getCliente().getId());
            ps.setString(3,pedido.getStatus().name());ps.setString(4,pedido.getTipoEntrega().name());ps.setString(5,pedido.getEnderecoEntrega());ps.setTimestamp(6,Timestamp.valueOf(pedido.getCriadoEm()));ps.executeUpdate();
            try(ResultSet rs=ps.getGeneratedKeys()){if(!rs.next())throw new SQLException("ID de pedido não retornado");id=rs.getLong(1);}
        }
        try(PreparedStatement ps=c.prepareStatement("INSERT INTO pedido_itens(pedido_id,produto_id,quantidade,preco_unitario,custo_unitario) VALUES (?,?,?,?,?)")){
            for(PedidoItem item:pedido.getItens()){
                long produtoId=item.getProduto().getId();BigDecimal custo=custos.get(produtoId);if(custo==null)throw new SQLException("Custo do produto não informado");
                ps.setLong(1,id);ps.setLong(2,produtoId);ps.setBigDecimal(3,item.getQuantidade());ps.setBigDecimal(4,item.getPrecoUnitario());ps.setBigDecimal(5,custo);ps.addBatch();
            } ps.executeBatch();
        } return id;
    }
    @Override public com.salgaderia.app.model.enums.StatusPedido bloquearStatus(Connection c,long id) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT status FROM pedidos WHERE id=? FOR UPDATE")) { ps.setLong(1,id);try(ResultSet rs=ps.executeQuery()){if(!rs.next())throw new IllegalArgumentException("Pedido não encontrado");return com.salgaderia.app.model.enums.StatusPedido.valueOf(rs.getString(1));} }
    }
    @Override public Map<Long,BigDecimal> quantidades(Connection c,long id) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT produto_id,SUM(quantidade) FROM pedido_itens WHERE pedido_id=? GROUP BY produto_id ORDER BY produto_id")) {
            ps.setLong(1,id);try(ResultSet rs=ps.executeQuery()){Map<Long,BigDecimal> resultado=new java.util.LinkedHashMap<>();while(rs.next())resultado.put(rs.getLong(1),rs.getBigDecimal(2));return resultado;}
        }
    }
    @Override public void atualizarStatus(Connection c,long id,com.salgaderia.app.model.enums.StatusPedido status) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE pedidos SET status=? WHERE id=?")){ps.setString(1,status.name());ps.setLong(2,id);if(ps.executeUpdate()!=1)throw new SQLException("Pedido não atualizado");}
    }
    @Override public boolean possuiPedidoEntregue(Connection c,long usuarioId,long produtoId) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT 1 FROM pedidos p JOIN pedido_itens i ON i.pedido_id=p.id WHERE p.usuario_id=? AND p.status='ENTREGUE' AND i.produto_id=? LIMIT 1")){
            ps.setLong(1,usuarioId);ps.setLong(2,produtoId);try(ResultSet rs=ps.executeQuery()){return rs.next();}
        }
    }
    @Override public BigDecimal[] somarVendasEntregues(Connection c,LocalDateTime inicio,LocalDateTime fim) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT COALESCE(SUM(i.quantidade*i.preco_unitario),0),COALESCE(SUM(i.quantidade*i.custo_unitario),0),SUM(i.custo_unitario IS NULL) FROM pedidos p JOIN pedido_itens i ON p.id=i.pedido_id WHERE p.status='ENTREGUE' AND p.criado_em>=? AND p.criado_em<?")){
            ps.setTimestamp(1,Timestamp.valueOf(inicio));ps.setTimestamp(2,Timestamp.valueOf(fim));try(ResultSet rs=ps.executeQuery()){rs.next();if(rs.getLong(3)>0)throw new SQLException("Vendas sem custo histórico");return new BigDecimal[]{rs.getBigDecimal(1),rs.getBigDecimal(2)};}
        }
    }
}
