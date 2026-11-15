package com.salgaderia.app.dao.impl;
import com.salgaderia.app.dao.PedidoDAO;
import com.salgaderia.app.model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;
public class PedidoDAOImpl implements PedidoDAO {
    @Override public java.util.List<Pedido> listarDoCliente(Connection c,UsuarioCliente usuario) throws SQLException {
        String sql="SELECT p.id pedido_id,p.status,p.tipo_entrega,p.endereco_entrega,p.criado_em,"
                + "i.id item_id,i.quantidade,i.preco_unitario,pr.id produto_id,pr.nome produto_nome,pr.descricao produto_descricao,"
                + "pr.preco_venda,pr.custo_producao,pr.estoque_atual,pr.estoque_minimo,pr.url_imagem,"
                + "cat.id categoria_id,cat.nome categoria_nome,cat.descricao categoria_descricao "
                + "FROM pedidos p JOIN pedido_itens i ON i.pedido_id=p.id JOIN produtos pr ON pr.id=i.produto_id "
                + "LEFT JOIN categorias cat ON cat.id=pr.categoria_id WHERE p.usuario_id=? ORDER BY p.criado_em DESC,p.id DESC,i.id";
        record Dados(com.salgaderia.app.model.enums.StatusPedido status,com.salgaderia.app.model.enums.TipoEntrega entrega,String endereco,LocalDateTime data,java.util.List<PedidoItem> itens) { }
        java.util.Map<Long,Dados> grupos=new java.util.LinkedHashMap<>();
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setLong(1,usuario.getId());
            try(ResultSet rs=ps.executeQuery()){
                while(rs.next()){
                    long id=rs.getLong("pedido_id");
                    Dados dados=grupos.get(id);
                    if(dados==null){dados=new Dados(com.salgaderia.app.model.enums.StatusPedido.valueOf(rs.getString("status")),com.salgaderia.app.model.enums.TipoEntrega.valueOf(rs.getString("tipo_entrega")),rs.getString("endereco_entrega"),rs.getTimestamp("criado_em").toLocalDateTime(),new java.util.ArrayList<>());grupos.put(id,dados);}
                    Long categoriaId=rs.getObject("categoria_id",Long.class);
                    Categoria categoria=categoriaId==null?null:new Categoria(categoriaId,rs.getString("categoria_nome"),rs.getString("categoria_descricao"));
                    Produto produto=new Produto(rs.getLong("produto_id"),categoria,rs.getString("produto_nome"),rs.getString("produto_descricao"),rs.getBigDecimal("preco_venda"),rs.getBigDecimal("custo_producao"),rs.getBigDecimal("estoque_atual"),rs.getBigDecimal("estoque_minimo"),rs.getString("url_imagem"));
                    dados.itens().add(new PedidoItem(rs.getLong("item_id"),produto,rs.getBigDecimal("quantidade"),rs.getBigDecimal("preco_unitario")));
                }
            }
        }
        java.util.List<Pedido> resultado=new java.util.ArrayList<>();
        grupos.forEach((id,d)->resultado.add(new Pedido(id,usuario,usuario.getCliente(),d.status(),d.entrega(),d.endereco(),d.data(),d.itens())));
        return resultado;
    }
    @Override public java.util.List<PedidoFila> listarFila(Connection c) throws SQLException {
        String sql="SELECT p.id,p.status,p.tipo_entrega,p.endereco_entrega,p.criado_em,COALESCE(ct.nome,u.nome,'Cliente') cliente,"
                + "i.quantidade,i.preco_unitario,pr.nome produto FROM pedidos p JOIN usuarios u ON u.id=p.usuario_id "
                + "LEFT JOIN contatos ct ON ct.id=p.contato_id LEFT JOIN pedido_itens i ON i.pedido_id=p.id "
                + "LEFT JOIN produtos pr ON pr.id=i.produto_id WHERE p.origem='VITRINE' AND u.perfil='CLIENTE' "
                + "AND p.status IN ('PENDENTE','PREPARANDO','PRONTO') ORDER BY p.criado_em,p.id,i.id";
        class Grupo {
            long id;String cliente,endereco;com.salgaderia.app.model.enums.StatusPedido status;com.salgaderia.app.model.enums.TipoEntrega entrega;
            LocalDateTime data;BigDecimal total=BigDecimal.ZERO;java.util.List<String> itens=new java.util.ArrayList<>();
        }
        java.util.Map<Long,Grupo> grupos=new java.util.LinkedHashMap<>();
        try(PreparedStatement ps=c.prepareStatement(sql);ResultSet rs=ps.executeQuery()){
            while(rs.next()){
                long id=rs.getLong("id");Grupo g=grupos.get(id);
                if(g==null){g=new Grupo();g.id=id;g.cliente=rs.getString("cliente");g.endereco=rs.getString("endereco_entrega");
                    g.status=com.salgaderia.app.model.enums.StatusPedido.valueOf(rs.getString("status"));g.entrega=com.salgaderia.app.model.enums.TipoEntrega.valueOf(rs.getString("tipo_entrega"));g.data=rs.getTimestamp("criado_em").toLocalDateTime();grupos.put(id,g);}
                BigDecimal quantidade=rs.getBigDecimal("quantidade");if(quantidade!=null){g.itens.add(quantidade.stripTrailingZeros().toPlainString()+" × "+rs.getString("produto"));g.total=g.total.add(quantidade.multiply(rs.getBigDecimal("preco_unitario")));}
            }
        }
        java.util.List<PedidoFila> resultado=new java.util.ArrayList<>();
        for(Grupo g:grupos.values())resultado.add(new PedidoFila(g.id,g.cliente,g.status,g.entrega,g.endereco,g.data,g.total,String.join(", ",g.itens)));
        return resultado;
    }
    @Override public void registrarPdv(Connection c,long id,com.salgaderia.app.model.enums.FormaPagamento forma,BigDecimal recebido,BigDecimal troco) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE pedidos SET origem='PDV',status='ENTREGUE',forma_pagamento=?,valor_recebido=?,troco=? WHERE id=? AND status='PENDENTE'")){
            ps.setString(1,forma.name());ps.setBigDecimal(2,recebido);ps.setBigDecimal(3,troco);ps.setLong(4,id);if(ps.executeUpdate()!=1)throw new SQLException("Venda PDV não atualizada");
        }
    }
    @Override public java.util.Map<String,BigDecimal> produtosMaisVendidos(Connection c,LocalDateTime inicio,LocalDateTime fim) throws SQLException {
        return agrupar(c,"SELECT CONCAT(pr.nome,' (#',pr.id,')') rotulo,SUM(i.quantidade) valor FROM pedidos p JOIN pedido_itens i ON i.pedido_id=p.id JOIN produtos pr ON pr.id=i.produto_id WHERE p.status='ENTREGUE' AND p.criado_em>=? AND p.criado_em<? GROUP BY pr.id,pr.nome ORDER BY valor DESC,pr.id LIMIT 10",inicio,fim);
    }
    @Override public java.util.Map<String,BigDecimal> receitaPorCategoria(Connection c,LocalDateTime inicio,LocalDateTime fim) throws SQLException {
        return agrupar(c,"SELECT COALESCE(cat.nome,'Sem categoria') rotulo,SUM(i.quantidade*i.preco_unitario) valor FROM pedidos p JOIN pedido_itens i ON i.pedido_id=p.id JOIN produtos pr ON pr.id=i.produto_id LEFT JOIN categorias cat ON cat.id=pr.categoria_id WHERE p.status='ENTREGUE' AND p.criado_em>=? AND p.criado_em<? GROUP BY cat.id,cat.nome ORDER BY valor DESC",inicio,fim);
    }
    private java.util.Map<String,BigDecimal> agrupar(Connection c,String sql,LocalDateTime inicio,LocalDateTime fim) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement(sql)){ps.setTimestamp(1,Timestamp.valueOf(inicio));ps.setTimestamp(2,Timestamp.valueOf(fim));try(ResultSet rs=ps.executeQuery()){
            java.util.Map<String,BigDecimal> dados=new java.util.LinkedHashMap<>();while(rs.next())dados.merge(rs.getString("rotulo"),rs.getBigDecimal("valor"),BigDecimal::add);return dados;
        }}
    }
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
