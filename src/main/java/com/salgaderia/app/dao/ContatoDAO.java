package com.salgaderia.app.dao;

import com.salgaderia.app.model.Contato;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** O chamador é responsável pelo ciclo de vida e pelas transações da conexão. */
public interface ContatoDAO {
    long inserir(Connection conexao, Contato contato) throws SQLException;
    void atualizarPerfilCliente(Connection c,long id,String nome,String whatsapp,String endereco) throws SQLException;
    boolean atualizar(Connection conexao, Contato contato) throws SQLException;
    boolean excluir(Connection conexao, long id) throws SQLException;
    Optional<Contato> buscarPorId(Connection conexao, long id) throws SQLException;
    List<Contato> buscar(Connection conexao, FiltroContato filtro, String texto) throws SQLException;
    enum FiltroContato { TODOS, CLIENTES, FORNECEDORES }
}
