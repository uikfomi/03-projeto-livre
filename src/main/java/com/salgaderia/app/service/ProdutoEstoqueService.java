package com.salgaderia.app.service;
import com.salgaderia.app.model.Produto;
import java.math.BigDecimal;
import java.util.*;
public interface ProdutoEstoqueService {
    long cadastrar(Produto produto);
    boolean atualizar(Produto produto);
    boolean excluir(long id);
    Optional<Produto> buscarPorId(long id);
    List<Produto> listar();
    List<Produto> listarEstoqueBaixo();
    void entradaLote(long produtoId,long fornecedorId,BigDecimal quantidade,String observacao);
    void registrarPerda(long produtoId,BigDecimal quantidade,String observacao);
}
