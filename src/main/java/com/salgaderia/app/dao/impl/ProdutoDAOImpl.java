package com.salgaderia.app.dao.impl;
import com.salgaderia.app.dao.ProdutoDAO;
import com.salgaderia.app.model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
public class ProdutoDAOImpl implements ProdutoDAO {
    private static final String SELECT = "SELECT p.*,c.nome AS categoria_nome,c.descricao AS categoria_descricao FROM produtos p LEFT JOIN categorias c ON c.id=p.categoria_id";
    @Override public long inserir(Connection c, Produto p) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO produtos(categoria_id,nome,descricao,preco_venda,custo_producao,estoque_atual,estoque_minimo,url_imagem,tipo_combo,recheio_combo,contem_queijo,contem_calabresa,assado_caixa) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps,p); preencherFicha(ps,9,p.getFichaVitrine()); ps.executeUpdate(); try (ResultSet rs=ps.getGeneratedKeys()) { if (!rs.next()) throw new SQLException("ID não retornado"); return rs.getLong(1); }
        }
    }
    @Override public boolean atualizar(Connection c, Produto p) throws SQLException {
        if (p.getId() == null) throw new IllegalArgumentException("ID obrigatório");
        try (PreparedStatement ps=c.prepareStatement("UPDATE produtos SET categoria_id=?,nome=?,descricao=?,preco_venda=?,custo_producao=?,estoque_minimo=?,url_imagem=?,tipo_combo=?,recheio_combo=?,contem_queijo=?,contem_calabresa=?,assado_caixa=? WHERE id=?")) {
            preencherAtualizacao(ps,p); preencherFicha(ps,8,p.getFichaVitrine()); ps.setLong(13,p.getId()); return ps.executeUpdate()==1;
        }
    }
    @Override public boolean excluir(Connection c, long id) throws SQLException {
        try (PreparedStatement ps=c.prepareStatement("DELETE FROM produtos WHERE id=?")) { ps.setLong(1,id); return ps.executeUpdate()==1; }
    }
    @Override public Optional<Produto> buscarPorId(Connection c,long id,boolean bloquear) throws SQLException {
        try (PreparedStatement ps=c.prepareStatement(SELECT + " WHERE p.id=?" + (bloquear ? " FOR UPDATE" : ""))) {
            ps.setLong(1,id); try (ResultSet rs=ps.executeQuery()) { return rs.next() ? Optional.of(mapear(rs)) : Optional.empty(); }
        }
    }
    @Override public List<Produto> listar(Connection c) throws SQLException {
        try (PreparedStatement ps=c.prepareStatement(SELECT + " ORDER BY p.nome,p.id"); ResultSet rs=ps.executeQuery()) {
            List<Produto> produtos=new ArrayList<>(); while(rs.next()) produtos.add(mapear(rs)); return produtos;
        }
    }
    @Override public boolean atualizarEstoque(Connection c,long id,BigDecimal estoque) throws SQLException {
        if (estoque == null || estoque.signum()<0) throw new IllegalArgumentException("Estoque inválido");
        try (PreparedStatement ps=c.prepareStatement("UPDATE produtos SET estoque_atual=? WHERE id=?")) { ps.setBigDecimal(1,estoque); ps.setLong(2,id); return ps.executeUpdate()==1; }
    }
    private void preencher(PreparedStatement ps,Produto p) throws SQLException {
        if (p.getCategoria()==null) ps.setNull(1,Types.BIGINT);
        else if (p.getCategoria().getId()==null) throw new IllegalArgumentException("Categoria sem ID");
        else ps.setLong(1,p.getCategoria().getId());
        ps.setString(2,p.getNome()); ps.setString(3,p.getDescricao()); ps.setBigDecimal(4,p.getPrecoVenda());
        ps.setBigDecimal(5,p.getCustoProducao()); ps.setBigDecimal(6,p.getEstoqueAtual()); ps.setBigDecimal(7,p.getEstoqueMinimo()); ps.setString(8,p.getUrlImagem());
    }
    private void preencherAtualizacao(PreparedStatement ps,Produto p) throws SQLException {
        if (p.getCategoria()==null) ps.setNull(1,Types.BIGINT);
        else if (p.getCategoria().getId()==null) throw new IllegalArgumentException("Categoria sem ID");
        else ps.setLong(1,p.getCategoria().getId());
        ps.setString(2,p.getNome()); ps.setString(3,p.getDescricao()); ps.setBigDecimal(4,p.getPrecoVenda());
        ps.setBigDecimal(5,p.getCustoProducao()); ps.setBigDecimal(6,p.getEstoqueMinimo()); ps.setString(7,p.getUrlImagem());
    }
    private Produto mapear(ResultSet rs) throws SQLException {
        Long id=(Long)rs.getObject("categoria_id",Long.class);
        Categoria categoria=id==null ? null : new Categoria(id,rs.getString("categoria_nome"),rs.getString("categoria_descricao"));
        Produto produto = new Produto(rs.getLong("id"),categoria,rs.getString("nome"),rs.getString("descricao"),rs.getBigDecimal("preco_venda"),rs.getBigDecimal("custo_producao"),rs.getBigDecimal("estoque_atual"),rs.getBigDecimal("estoque_minimo"),rs.getString("url_imagem"));
        String tipo=rs.getString("tipo_combo");
        produto.setFichaVitrine(new FichaVitrine(tipo==null?null:FichaVitrine.TipoCombo.valueOf(tipo),
                rs.getString("recheio_combo"),booleanoOpcional(rs,"contem_queijo"),
                booleanoOpcional(rs,"contem_calabresa"),rs.getBoolean("assado_caixa")));
        return produto;
    }
    private static Boolean booleanoOpcional(ResultSet rs,String coluna) throws SQLException {
        boolean valor=rs.getBoolean(coluna);return rs.wasNull()?null:valor;
    }
    private static void preencherFicha(PreparedStatement ps,int i,FichaVitrine f) throws SQLException {
        ps.setString(i,f.tipo()==null?null:f.tipo().name());ps.setString(i+1,f.recheio());
        ps.setObject(i+2,f.contemQueijo(),Types.BOOLEAN);ps.setObject(i+3,f.contemCalabresa(),Types.BOOLEAN);
        ps.setBoolean(i+4,f.assado());
    }
}
