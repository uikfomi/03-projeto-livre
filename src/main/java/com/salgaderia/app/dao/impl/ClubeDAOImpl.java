package com.salgaderia.app.dao.impl;

import com.salgaderia.app.dao.ClubeDAO;
import com.salgaderia.app.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ClubeDAOImpl implements ClubeDAO {
    private static final String ASSINATURAS = """
            SELECT a.usuario_id,u.nome cliente,a.plano_id,p.nome plano,a.valida_ate,a.desconto_vigente,a.cancelada
            FROM assinaturas_clientes a JOIN usuarios u ON u.id=a.usuario_id JOIN planos_assinatura p ON p.id=a.plano_id
            """;

    @Override public List<PlanoAssinatura> listarPlanos(Connection c,boolean apenasAtivos) throws SQLException {
        try(var ps=c.prepareStatement("SELECT * FROM planos_assinatura" + (apenasAtivos?" WHERE ativo=TRUE":"") + " ORDER BY mensalidade,id");
            var rs=ps.executeQuery()){
            List<PlanoAssinatura> planos=new ArrayList<>();
            while(rs.next())planos.add(plano(rs));
            return planos;
        }
    }
    @Override public Optional<PlanoAssinatura> planoAtivo(Connection c,long id) throws SQLException {
        try(var ps=c.prepareStatement("SELECT * FROM planos_assinatura WHERE id=? AND ativo=TRUE FOR SHARE")){
            ps.setLong(1,id);try(var rs=ps.executeQuery()){return rs.next()?Optional.of(plano(rs)):Optional.empty();}
        }
    }
    private static PlanoAssinatura plano(ResultSet rs)throws SQLException{
        return new PlanoAssinatura(rs.getLong("id"),rs.getString("nome"),rs.getString("descricao"),
                rs.getBigDecimal("mensalidade"),rs.getBigDecimal("desconto_percentual"),rs.getBoolean("ativo"));
    }
    @Override public void salvarPlano(Connection c,PlanoAssinatura plano) throws SQLException {
        boolean novo=plano.id()==null;
        try(var ps=c.prepareStatement(novo?"INSERT INTO planos_assinatura(nome,descricao,mensalidade,desconto_percentual,ativo) VALUES (?,?,?,?,?)":
                "UPDATE planos_assinatura SET nome=?,descricao=?,mensalidade=?,desconto_percentual=?,ativo=? WHERE id=?")){
            ps.setString(1,plano.nome());ps.setString(2,plano.descricao());ps.setBigDecimal(3,plano.mensalidade());
            ps.setBigDecimal(4,plano.descontoPercentual());ps.setBoolean(5,plano.ativo());
            if(!novo)ps.setLong(6,plano.id());
            if(ps.executeUpdate()!=1)throw new IllegalArgumentException("Plano não encontrado.");
        }
    }
    @Override public Optional<AssinaturaCliente> assinatura(Connection c,long usuarioId,boolean bloquear) throws SQLException {
        try(var ps=c.prepareStatement(ASSINATURAS+" WHERE a.usuario_id=?"+(bloquear?" FOR UPDATE OF a":""))){
            ps.setLong(1,usuarioId);try(var rs=ps.executeQuery()){return rs.next()?Optional.of(assinatura(rs)):Optional.empty();}
        }
    }
    @Override public List<AssinaturaCliente> assinantesAtivos(Connection c,LocalDate hoje) throws SQLException {
        try(var ps=c.prepareStatement(ASSINATURAS+" WHERE u.ativo AND NOT a.cancelada AND a.valida_ate>? ORDER BY u.nome,a.usuario_id")){
            ps.setObject(1,hoje);
            try(var rs=ps.executeQuery()){
                List<AssinaturaCliente> lista=new ArrayList<>();while(rs.next())lista.add(assinatura(rs));return lista;
            }
        }
    }
    private static AssinaturaCliente assinatura(ResultSet rs)throws SQLException{
        return new AssinaturaCliente(rs.getLong("usuario_id"),rs.getString("cliente"),rs.getLong("plano_id"),rs.getString("plano"),
                rs.getObject("valida_ate",LocalDate.class),rs.getBigDecimal("desconto_vigente"),rs.getBoolean("cancelada"));
    }
    @Override public List<MensalidadeClube> pendencias(Connection c,Long usuarioId,boolean bloquear) throws SQLException {
        String sql="""
                SELECT m.id,m.usuario_id,u.nome cliente,m.plano_id,p.nome plano,m.valor,m.desconto_percentual
                FROM mensalidades_clube m JOIN usuarios u ON u.id=m.usuario_id JOIN planos_assinatura p ON p.id=m.plano_id
                WHERE m.status='PENDENTE'
                """;
        try(var ps=c.prepareStatement(sql+(usuarioId==null?"":" AND m.usuario_id=?")+" ORDER BY m.id"+(bloquear?" FOR UPDATE OF m":""))){
            if(usuarioId!=null)ps.setLong(1,usuarioId);
            try(var rs=ps.executeQuery()){
                List<MensalidadeClube> lista=new ArrayList<>();
                while(rs.next())lista.add(new MensalidadeClube(rs.getLong(1),rs.getLong(2),rs.getString(3),rs.getLong(4),rs.getString(5),rs.getBigDecimal(6),rs.getBigDecimal(7)));
                return lista;
            }
        }
    }
    @Override public void prepararAssinatura(Connection c,long usuarioId,long planoId) throws SQLException {
        try(var ps=c.prepareStatement("""
                INSERT INTO assinaturas_clientes(usuario_id,plano_id) VALUES (?,?)
                ON DUPLICATE KEY UPDATE plano_id=?,cancelada=FALSE,valida_ate=NULL,desconto_vigente=0
                """)){
            ps.setLong(1,usuarioId);ps.setLong(2,planoId);ps.setLong(3,planoId);ps.executeUpdate();
        }
    }
    @Override public void abrirMensalidade(Connection c,long usuarioId,PlanoAssinatura plano) throws SQLException {
        try(var ps=c.prepareStatement("INSERT INTO mensalidades_clube(usuario_id,plano_id,valor,desconto_percentual) VALUES (?,?,?,?)")){
            ps.setLong(1,usuarioId);ps.setLong(2,plano.id());ps.setBigDecimal(3,plano.mensalidade());ps.setBigDecimal(4,plano.descontoPercentual());ps.executeUpdate();
        }
    }
    @Override public void confirmarMensalidade(Connection c,MensalidadeClube m,LocalDate ate,long adminId) throws SQLException {
        try(var ps=c.prepareStatement("UPDATE mensalidades_clube SET status='CONFIRMADA',confirmada_em=NOW(),confirmada_por=?,periodo_ate=? WHERE id=? AND status='PENDENTE'")){
            ps.setLong(1,adminId);ps.setObject(2,ate);ps.setLong(3,m.id());
            if(ps.executeUpdate()!=1)throw new IllegalArgumentException("A mensalidade não está mais pendente.");
        }
        try(var ps=c.prepareStatement("UPDATE assinaturas_clientes SET valida_ate=?,desconto_vigente=? WHERE usuario_id=? AND plano_id=? AND NOT cancelada")){
            ps.setObject(1,ate);ps.setBigDecimal(2,m.descontoPercentual());ps.setLong(3,m.usuarioId());ps.setLong(4,m.planoId());
            if(ps.executeUpdate()!=1)throw new IllegalArgumentException("A assinatura não está disponível para ativação.");
        }
    }
    @Override public void cancelar(Connection c,long usuarioId) throws SQLException {
        try(var assinatura=c.prepareStatement("UPDATE assinaturas_clientes SET cancelada=TRUE WHERE usuario_id=?");
            var mensalidades=c.prepareStatement("UPDATE mensalidades_clube SET status='CANCELADA' WHERE usuario_id=? AND status='PENDENTE'")){
            assinatura.setLong(1,usuarioId);assinatura.executeUpdate();
            mensalidades.setLong(1,usuarioId);mensalidades.executeUpdate();
        }
    }
}
