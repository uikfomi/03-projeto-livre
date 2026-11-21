package com.salgaderia.app.dao;
import com.salgaderia.app.model.Usuario;
import java.sql.*;
import java.util.Optional;
public interface UsuarioDAO {
    long inserir(Connection conexao, Usuario usuario, Long clienteId) throws SQLException;
    Optional<Usuario> buscarPorLogin(Connection conexao, String login) throws SQLException;
    Optional<Usuario> buscarPorId(Connection conexao, long id) throws SQLException;
    boolean bloquearClienteAtivo(Connection conexao,long id,long contatoId) throws SQLException;
    void atualizarApelido(Connection c,long id,String nome) throws SQLException;
    void atualizarSenha(Connection conexao, long id, String senha) throws SQLException;
}
