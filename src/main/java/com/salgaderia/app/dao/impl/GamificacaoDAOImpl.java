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
        try(var ps=c.prepareStatement("SELECT id FROM pedidos WHERE contato_id=? AND status='ENTREGUE' AND (pix_provedor IS NULL OR pix_provedor<>'DEMO') LIMIT 3 FOR SHARE")){
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
        try(var ps=c.prepareStatement("""
                UPDATE pedidos p SET roleta_liberada=TRUE WHERE id=? AND origem='VITRINE'
                AND status<>'CANCELADO' AND (status_pagamento='PAGO' OR status='ENTREGUE')
                AND (SELECT COALESCE(SUM(quantidade*preco_unitario),0) FROM pedido_itens WHERE pedido_id=p.id)+taxa_entrega-desconto>0
                """)){
            ps.setLong(1,pedidoId);ps.executeUpdate();
        }
    }
    @Override public Optional<ElegibilidadeRoleta> roletaElegivel(Connection c,long pedidoId,long usuarioId)throws SQLException{
        try(var ps=c.prepareStatement("""
                SELECT ROUND((SELECT COALESCE(SUM(quantidade*preco_unitario),0) FROM pedido_itens WHERE pedido_id=p.id)
                    +p.taxa_entrega-p.desconto,2) total,p.pix_provedor
                FROM pedidos p WHERE id=? AND usuario_id=? AND origem='VITRINE'
                AND roleta_liberada AND status<>'CANCELADO' AND (status_pagamento='PAGO' OR status='ENTREGUE') FOR UPDATE
                """)){
            ps.setLong(1,pedidoId);ps.setLong(2,usuarioId);try(var rs=ps.executeQuery()){
                return rs.next()&&rs.getBigDecimal(1).signum()>0?
                        Optional.of(new ElegibilidadeRoleta(rs.getBigDecimal(1),"DEMO".equals(rs.getString(2)))):Optional.empty();
            }
        }
    }
    private static final String PREMIO_COLUNAS="r.pedido_id,r.premio,r.premio_extra,r.cupom_codigo,r.vip,r.criado_em";
    @Override public Optional<PremioRoleta> premio(Connection c,long pedidoId)throws SQLException{
        try(var ps=c.prepareStatement("SELECT "+PREMIO_COLUNAS+" FROM roleta_premios r WHERE pedido_id=?")){
            ps.setLong(1,pedidoId);try(var rs=ps.executeQuery()){return rs.next()?Optional.of(premio(rs)):Optional.empty();}
        }
    }
    @Override public List<PremioRoleta> premiosValidos(Connection c,long usuarioId)throws SQLException{
        try(var ps=c.prepareStatement("SELECT "+PREMIO_COLUNAS+" "+"""
                FROM roleta_premios r JOIN pedidos p ON p.id=r.pedido_id
                WHERE r.usuario_id=? AND p.status<>'CANCELADO'
                """)){
            ps.setLong(1,usuarioId);try(var rs=ps.executeQuery()){
                List<PremioRoleta> premios=new ArrayList<>();while(rs.next())premios.add(premio(rs));return premios;
            }
        }
    }
    private static PremioRoleta premio(ResultSet rs)throws SQLException{
        String primeiro=rs.getString("premio"),extra=rs.getString("premio_extra");
        return new PremioRoleta(rs.getLong("pedido_id"),PremioRoleta.Tipo.valueOf(extra==null?primeiro:extra),
                rs.getString("cupom_codigo"),false,rs.getBoolean("vip"),primeiro.equals("GIRO_EXTRA")&&extra==null,
                rs.getObject("criado_em",LocalDateTime.class));
    }
    @Override public List<CupomRoleta> cuponsValidos(Connection c,long usuarioId)throws SQLException{
        try(var ps=c.prepareStatement("""
                SELECT c.codigo,c.valor_minimo,c.valido_ate,COALESCE(r.premio_extra,r.premio) premio
                FROM cupons c JOIN roleta_premios r ON r.cupom_codigo=c.codigo
                WHERE c.usuario_exclusivo_id=? AND c.ativo AND c.usado_pedido_id IS NULL AND c.valido_ate>NOW()
                ORDER BY c.valido_ate,c.id
                """)){
            ps.setLong(1,usuarioId);try(var rs=ps.executeQuery()){
                List<CupomRoleta> cupons=new ArrayList<>();
                while(rs.next())cupons.add(new CupomRoleta(rs.getString(1),PremioRoleta.Tipo.valueOf(rs.getString(4)).rotulo(),
                        rs.getBigDecimal(2),rs.getObject(3,LocalDateTime.class)));
                return cupons;
            }
        }
    }
    @Override public Optional<LocalDateTime> ultimoGiro(Connection c,long usuarioId)throws SQLException{
        try(var ps=c.prepareStatement("SELECT MAX(criado_em) FROM roleta_premios WHERE usuario_id=?")){
            ps.setLong(1,usuarioId);try(var rs=ps.executeQuery()){rs.next();return Optional.ofNullable(rs.getObject(1,LocalDateTime.class));}
        }
    }
    @Override public LocalDateTime horarioDoBanco(Connection c)throws SQLException{
        try(var ps=c.prepareStatement("SELECT NOW()");var rs=ps.executeQuery()){
            rs.next();return rs.getObject(1,LocalDateTime.class);
        }
    }
    @Override public long pontosDosPedidos(Connection c,long usuarioId)throws SQLException{
        try(var ps=c.prepareStatement("""
                SELECT COALESCE(SUM(CASE WHEN estrategia_desconto='PONTOS_DOBRO' THEN 20 ELSE 10 END),0)
                FROM pedidos WHERE usuario_id=? AND origem='VITRINE' AND roleta_liberada
                AND status<>'CANCELADO' AND (pix_provedor IS NULL OR pix_provedor<>'DEMO')
                AND (status_pagamento='PAGO' OR status='ENTREGUE')
                """)){
            ps.setLong(1,usuarioId);try(var rs=ps.executeQuery()){rs.next();return rs.getLong(1);}
        }
    }
    @Override public void registrarPremio(Connection c,long usuarioId,PremioRoleta premio,boolean extra)throws SQLException{
        if(premio.cupom()!=null){
            try(var ps=c.prepareStatement("""
                    INSERT INTO cupons(codigo,percentual,usuario_exclusivo_id,valor_minimo,valido_ate,beneficio,valor_beneficio,brinde_descricao)
                    VALUES (?,?,?,?,?,?,?,?)
                    """)){
                var tipo=premio.tipo();
                ps.setString(1,premio.cupom());ps.setBigDecimal(2,tipo.percentual());ps.setLong(3,usuarioId);
                ps.setBigDecimal(4,PremioRoleta.MINIMO);ps.setObject(5,premio.validoAte());ps.setString(6,tipo.beneficio().name());
                ps.setBigDecimal(7,tipo.valorBeneficio());
                ps.setString(8,tipo.beneficio()==com.salgaderia.app.service.desconto.Cupom.Beneficio.BRINDE?tipo.rotulo():null);
                ps.executeUpdate();
            }
        }
        if(extra){
            try(var ps=c.prepareStatement("UPDATE roleta_premios SET premio_extra=?,cupom_codigo=? WHERE pedido_id=? AND premio='GIRO_EXTRA' AND premio_extra IS NULL")){
                ps.setString(1,premio.tipo().name());ps.setString(2,premio.cupom());ps.setLong(3,premio.pedidoId());
                if(ps.executeUpdate()!=1)throw new SQLException("O giro extra já foi utilizado.");
            }
        }else try(var ps=c.prepareStatement("INSERT INTO roleta_premios(pedido_id,usuario_id,premio,cupom_codigo,vip,criado_em) VALUES (?,?,?,?,?,?)")){
            ps.setLong(1,premio.pedidoId());ps.setLong(2,usuarioId);ps.setString(3,premio.tipo().name());ps.setString(4,premio.cupom());
            ps.setBoolean(5,premio.vip());ps.setObject(6,premio.registradoEm());ps.executeUpdate();
        }
    }
    @Override public void revogarCupomDoPremio(Connection c,long pedidoId)throws SQLException{
        try(var ps=c.prepareStatement("UPDATE cupons c JOIN roleta_premios r ON r.cupom_codigo=c.codigo SET c.ativo=FALSE WHERE r.pedido_id=?")){
            ps.setLong(1,pedidoId);ps.executeUpdate();
        }
    }
}
