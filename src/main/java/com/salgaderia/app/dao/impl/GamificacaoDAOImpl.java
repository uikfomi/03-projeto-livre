package com.salgaderia.app.dao.impl;

import com.salgaderia.app.dao.GamificacaoDAO;
import com.salgaderia.app.model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GamificacaoDAOImpl implements GamificacaoDAO {
    @Override public Optional<Conta> contaPorContato(Connection c,long contatoId)throws SQLException{
        return buscarConta(c,"SELECT u.id,u.cliente_id FROM usuarios u WHERE perfil='CLIENTE' AND ativo AND cliente_id=?",contatoId);
    }
    @Override public Optional<Conta> contaDoPedido(Connection c,long pedidoId)throws SQLException{
        return buscarConta(c,"""
                SELECT u.id,u.cliente_id FROM pedidos p JOIN usuarios u
                  ON (p.origem='VITRINE' AND u.id=p.usuario_id AND u.cliente_id=p.contato_id)
                  OR (p.origem='PDV' AND u.cliente_id=p.contato_id)
                WHERE p.id=? AND u.perfil='CLIENTE' AND u.ativo
                """,pedidoId);
    }
    private Optional<Conta> buscarConta(Connection c,String sql,long id)throws SQLException{
        try(var ps=c.prepareStatement(sql)){
            ps.setLong(1,id);try(var rs=ps.executeQuery()){
                return rs.next()?Optional.of(new Conta(rs.getLong(1),rs.getLong(2))):Optional.empty();
            }
        }
    }
    @Override public List<ConquistaCliente> conquistas(Connection c,long usuarioId)throws SQLException{
        try(var ps=c.prepareStatement("""
                SELECT k.codigo,k.nome,k.descricao,ck.desbloqueada_em
                FROM cliente_conquistas ck JOIN conquistas k ON k.id=ck.conquista_id
                WHERE ck.usuario_id=? ORDER BY ck.desbloqueada_em,k.id
                """)){
            ps.setLong(1,usuarioId);try(var rs=ps.executeQuery()){
                List<ConquistaCliente> selos=new ArrayList<>();
                while(rs.next())selos.add(new ConquistaCliente(rs.getString(1),rs.getString(2),rs.getString(3),rs.getObject(4,LocalDateTime.class)));
                return selos;
            }
        }
    }
    @Override public void desbloquear(Connection c,long usuarioId,String codigo)throws SQLException{
        try(var ps=c.prepareStatement("""
                INSERT INTO cliente_conquistas(usuario_id,conquista_id)
                SELECT ?,id FROM conquistas WHERE codigo=?
                ON DUPLICATE KEY UPDATE desbloqueada_em=cliente_conquistas.desbloqueada_em
                """)){
            ps.setLong(1,usuarioId);ps.setString(2,codigo);ps.executeUpdate();
        }
    }
    @Override public int entregues(Connection c,long contatoId)throws SQLException{
        try(var ps=c.prepareStatement("SELECT id FROM pedidos WHERE contato_id=? AND status='ENTREGUE' LIMIT 3 FOR SHARE")){
            ps.setLong(1,contatoId);try(var rs=ps.executeQuery()){
                int quantidade=0;while(rs.next())quantidade++;return quantidade;
            }
        }
    }
    @Override public List<QuantidadeCategoria> quantidadesPorCategoria(Connection c,long pedidoId)throws SQLException{
        try(var ps=c.prepareStatement("""
                SELECT k.id,k.nome,SUM(i.quantidade) quantidade
                FROM pedido_itens i JOIN produtos p ON p.id=i.produto_id JOIN categorias k ON k.id=p.categoria_id
                WHERE i.pedido_id=? GROUP BY k.id,k.nome
                """)){
            ps.setLong(1,pedidoId);try(var rs=ps.executeQuery()){
                List<QuantidadeCategoria> quantidades=new ArrayList<>();
                while(rs.next())quantidades.add(new QuantidadeCategoria(new Categoria(rs.getLong(1),rs.getString(2),null),rs.getBigDecimal(3)));
                return quantidades;
            }
        }
    }
    @Override public void liberarRoleta(Connection c,long pedidoId)throws SQLException{
        try(var ps=c.prepareStatement("UPDATE pedidos SET roleta_liberada=TRUE WHERE id=? AND origem='VITRINE'")){
            ps.setLong(1,pedidoId);ps.executeUpdate();
        }
    }
    @Override public boolean roletaDisponivel(Connection c,long pedidoId,long usuarioId)throws SQLException{
        try(var ps=c.prepareStatement("""
                SELECT id FROM pedidos WHERE id=? AND usuario_id=? AND origem='VITRINE'
                AND roleta_liberada AND status<>'CANCELADO' FOR UPDATE
                """)){
            ps.setLong(1,pedidoId);ps.setLong(2,usuarioId);try(var rs=ps.executeQuery()){return rs.next();}
        }
    }
    @Override public Optional<PremioRoleta> premio(Connection c,long pedidoId)throws SQLException{
        try(var ps=c.prepareStatement("SELECT pedido_id,premio,cupom_codigo FROM roleta_premios WHERE pedido_id=?")){
            ps.setLong(1,pedidoId);try(var rs=ps.executeQuery()){return rs.next()?Optional.of(premio(rs)):Optional.empty();}
        }
    }
    @Override public List<PremioRoleta> premiosValidos(Connection c,long usuarioId)throws SQLException{
        try(var ps=c.prepareStatement("""
                SELECT r.pedido_id,r.premio,r.cupom_codigo FROM roleta_premios r JOIN pedidos p ON p.id=r.pedido_id
                WHERE r.usuario_id=? AND p.status<>'CANCELADO'
                """)){
            ps.setLong(1,usuarioId);try(var rs=ps.executeQuery()){
                List<PremioRoleta> premios=new ArrayList<>();while(rs.next())premios.add(premio(rs));return premios;
            }
        }
    }
    private static PremioRoleta premio(ResultSet rs)throws SQLException{
        return new PremioRoleta(rs.getLong(1),PremioRoleta.Tipo.valueOf(rs.getString(2)),rs.getString(3),false);
    }
    @Override public List<String> cuponsValidos(Connection c,long usuarioId)throws SQLException{
        try(var ps=c.prepareStatement("SELECT codigo FROM cupons WHERE usuario_exclusivo_id=? AND ativo ORDER BY id DESC")){
            ps.setLong(1,usuarioId);try(var rs=ps.executeQuery()){
                List<String> cupons=new ArrayList<>();while(rs.next())cupons.add(rs.getString(1));return cupons;
            }
        }
    }
    @Override public void registrarPremio(Connection c,long usuarioId,PremioRoleta premio)throws SQLException{
        if(premio.cupom()!=null){
            try(var ps=c.prepareStatement("INSERT INTO cupons(codigo,percentual,usuario_exclusivo_id) VALUES (?,?,?)")){
                ps.setString(1,premio.cupom());ps.setBigDecimal(2,premio.tipo().percentual());ps.setLong(3,usuarioId);ps.executeUpdate();
            }
        }
        try(var ps=c.prepareStatement("INSERT INTO roleta_premios(pedido_id,usuario_id,premio,cupom_codigo) VALUES (?,?,?,?)")){
            ps.setLong(1,premio.pedidoId());ps.setLong(2,usuarioId);ps.setString(3,premio.tipo().name());ps.setString(4,premio.cupom());ps.executeUpdate();
        }
    }
    @Override public void revogarCupomDoPremio(Connection c,long pedidoId)throws SQLException{
        try(var ps=c.prepareStatement("UPDATE cupons c JOIN roleta_premios r ON r.cupom_codigo=c.codigo SET c.ativo=FALSE WHERE r.pedido_id=?")){
            ps.setLong(1,pedidoId);ps.executeUpdate();
        }
    }
}
