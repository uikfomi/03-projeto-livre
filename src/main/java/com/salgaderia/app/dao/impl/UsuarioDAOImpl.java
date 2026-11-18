package com.salgaderia.app.dao.impl;
import com.salgaderia.app.dao.*;
import com.salgaderia.app.model.*;
import java.sql.*;
import java.util.*;
public class UsuarioDAOImpl implements UsuarioDAO {
    private final ContatoDAO contatos;
    public UsuarioDAOImpl(ContatoDAO contatos) { this.contatos=Objects.requireNonNull(contatos); }
    @Override public long inserir(Connection c, Usuario u, Long clienteId) throws SQLException {
        try (PreparedStatement ps=c.prepareStatement("INSERT INTO usuarios(nome,login,senha,perfil,ativo,cliente_id) VALUES (?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,u.getNome()); ps.setString(2,u.getLogin()); ps.setString(3,u.getSenha()); ps.setString(4,u.getPerfil().name()); ps.setBoolean(5,u.isAtivo());
            if(clienteId==null) ps.setNull(6,Types.BIGINT); else ps.setLong(6,clienteId);
            ps.executeUpdate(); try(ResultSet rs=ps.getGeneratedKeys()){if(!rs.next())throw new SQLException("ID de usuário não retornado"); return rs.getLong(1);}
        }
    }
    @Override public Optional<Usuario> buscarPorLogin(Connection c,String login) throws SQLException { return buscar(c,"login",login); }
    @Override public Optional<Usuario> buscarPorId(Connection c,long id) throws SQLException { return buscar(c,"id",id); }
    @Override public boolean bloquearClienteAtivo(Connection c,long id,long contatoId) throws SQLException {
        try(var ps=c.prepareStatement("SELECT id FROM usuarios WHERE id=? AND cliente_id=? AND perfil='CLIENTE' AND ativo=TRUE FOR UPDATE")){
            ps.setLong(1,id);ps.setLong(2,contatoId);try(var rs=ps.executeQuery()){return rs.next();}
        }
    }
    private Optional<Usuario> buscar(Connection c,String coluna,Object valor) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT id,nome,login,senha,perfil,ativo,cliente_id FROM usuarios WHERE "+coluna+"=?")) {
            ps.setObject(1,valor); try(ResultSet rs=ps.executeQuery()){
                if(!rs.next())return Optional.empty();
                long id=rs.getLong("id"); String nome=rs.getString("nome"), login=rs.getString("login"), senha=rs.getString("senha"), perfil=rs.getString("perfil"); boolean ativo=rs.getBoolean("ativo");
                if("ADMIN".equals(perfil)) return Optional.of(new UsuarioAdmin(id,nome,login,senha,ativo));
                if(!"CLIENTE".equals(perfil))throw new SQLException("Perfil de usuário inválido");
                Long clienteId=rs.getObject("cliente_id",Long.class);
                if(clienteId==null)throw new SQLException("Usuário cliente sem contato associado: "+id);
                Contato contato=contatos.buscarPorId(c,clienteId).orElseThrow(()->new SQLException("Contato inexistente: "+clienteId));
                if(!(contato instanceof Cliente cliente))throw new SQLException("Contato do usuário não é cliente");
                return Optional.of(new UsuarioCliente(id,nome,login,senha,ativo,cliente));
            }
        }
    }
    @Override public void atualizarSenha(Connection c,long id,String senha) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE usuarios SET senha=? WHERE id=?")){ ps.setString(1,senha);ps.setLong(2,id); if(ps.executeUpdate()!=1)throw new SQLException("Usuário inexistente"); }
    }
}
