package com.salgaderia.app.dao.impl;

import com.salgaderia.app.dao.ContatoDAO;
import com.salgaderia.app.model.Cliente;
import com.salgaderia.app.model.Contato;
import com.salgaderia.app.model.Fornecedor;
import com.salgaderia.app.model.enums.CategoriaInsumo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContatoDAOImpl implements ContatoDAO {
    private static final String COLUNAS = "tipo,nome,telefone,observacoes,whatsapp,endereco,ponto_referencia,nome_empresa,documento,categoria_insumo,prazo_entrega_dias";

    @Override public long inserir(Connection conexao, Contato contato) throws SQLException {
        String sql = "INSERT INTO contatos (" + COLUNAS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, contato);
            if (ps.executeUpdate() != 1) throw new SQLException("Contato não inserido");
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) throw new SQLException("ID de contato não retornado");
                return chaves.getLong(1);
            }
        }
    }
    @Override public boolean atualizar(Connection conexao, Contato contato) throws SQLException {
        if (contato.getId() == null) throw new IllegalArgumentException("ID obrigatório");
        String sql = "UPDATE contatos SET tipo=?,nome=?,telefone=?,observacoes=?,whatsapp=?,endereco=?,ponto_referencia=?,nome_empresa=?,documento=?,categoria_insumo=?,prazo_entrega_dias=? WHERE id=? AND tipo=?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            preencher(ps, contato); ps.setLong(12, contato.getId()); ps.setString(13, contato instanceof Cliente ? "CLIENTE" : "FORNECEDOR");
            return ps.executeUpdate() == 1;
        }
    }
    @Override public boolean excluir(Connection conexao, long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("DELETE FROM contatos WHERE id=?")) {
            ps.setLong(1, id); return ps.executeUpdate() == 1;
        }
    }
    @Override public Optional<Contato> buscarPorId(Connection conexao, long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("SELECT id," + COLUNAS + " FROM contatos WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(mapear(rs)) : Optional.empty(); }
        }
    }
    @Override public List<Contato> buscar(Connection conexao, FiltroContato filtro, String texto) throws SQLException {
        java.util.Objects.requireNonNull(filtro, "filtro");
        String sql = "SELECT id," + COLUNAS + " FROM contatos WHERE (? IS NULL OR tipo=?) AND ("
                + "nome LIKE ? ESCAPE '!' OR COALESCE(nome_empresa,'') LIKE ? ESCAPE '!' OR COALESCE(telefone,'') LIKE ? ESCAPE '!' "
                + "OR COALESCE(documento,'') LIKE ? ESCAPE '!' OR COALESCE(whatsapp,'') LIKE ? ESCAPE '!' "
                + "OR COALESCE(endereco,'') LIKE ? ESCAPE '!' OR COALESCE(ponto_referencia,'') LIKE ? ESCAPE '!' "
                + "OR COALESCE(observacoes,'') LIKE ? ESCAPE '!' OR COALESCE(categoria_insumo,'') LIKE ? ESCAPE '!') ORDER BY nome,id";
        String tipo = switch (filtro) { case TODOS -> null; case CLIENTES -> "CLIENTE"; case FORNECEDORES -> "FORNECEDOR"; };
        String busca = "%" + (texto == null ? "" : texto.replace("!", "!!").replace("%", "!%").replace("_", "!_")) + "%";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, tipo); ps.setString(2, tipo);
            for (int i = 3; i <= 11; i++) ps.setString(i, busca);
            try (ResultSet rs = ps.executeQuery()) {
                List<Contato> contatos = new ArrayList<>();
                while (rs.next()) contatos.add(mapear(rs));
                return contatos;
            }
        }
    }
    private static void preencher(PreparedStatement ps, Contato contato) throws SQLException {
        if (!(contato instanceof Cliente) && !(contato instanceof Fornecedor)) throw new IllegalArgumentException("Tipo de contato desconhecido");
        ps.setString(1, contato instanceof Cliente ? "CLIENTE" : "FORNECEDOR");
        ps.setString(2, contato.getNome()); ps.setString(3, contato.getTelefone()); ps.setString(4, contato.getObservacoes());
        if (contato instanceof Cliente c) {
            ps.setString(5, c.getWhatsapp()); ps.setString(6, c.getEndereco()); ps.setString(7, c.getPontoReferencia());
            for (int i = 8; i <= 10; i++) ps.setNull(i, Types.VARCHAR);
            ps.setNull(11, Types.INTEGER);
        } else {
            Fornecedor f = (Fornecedor) contato;
            for (int i = 5; i <= 7; i++) ps.setNull(i, Types.VARCHAR);
            ps.setString(8, f.getNomeEmpresa()); ps.setString(9, f.getCnpj());
            ps.setString(10, f.getCategoriaInsumo().name()); ps.setInt(11, f.getPrazoEntregaDias());
        }
    }
    private static Contato mapear(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        if ("CLIENTE".equals(tipo)) return new Cliente(rs.getLong("id"), rs.getString("nome"), rs.getString("telefone"), rs.getString("observacoes"), rs.getString("whatsapp"), rs.getString("endereco"), rs.getString("ponto_referencia"));
        if ("FORNECEDOR".equals(tipo)) {
            String categoria = rs.getString("categoria_insumo");
            if (categoria == null || rs.getString("nome_empresa") == null || rs.getString("documento") == null || rs.getObject("prazo_entrega_dias") == null) throw new SQLException("Fornecedor incompleto: " + rs.getLong("id"));
            return new Fornecedor(rs.getLong("id"), rs.getString("nome"), rs.getString("telefone"), rs.getString("observacoes"), rs.getString("nome_empresa"), rs.getString("documento"), CategoriaInsumo.valueOf(categoria), rs.getInt("prazo_entrega_dias"));
        }
        throw new SQLException("Tipo de contato desconhecido: " + tipo);
    }
}
