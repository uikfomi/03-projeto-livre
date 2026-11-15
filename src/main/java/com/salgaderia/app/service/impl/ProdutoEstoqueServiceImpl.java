package com.salgaderia.app.service.impl;
import com.salgaderia.app.dao.*;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.TipoMovimentacaoEstoque;
import com.salgaderia.app.service.ProdutoEstoqueService;
import com.salgaderia.app.util.ConnectionFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
public class ProdutoEstoqueServiceImpl implements ProdutoEstoqueService {
    private final ProdutoDAO produtos;private final ContatoDAO contatos;private final MovimentacaoEstoqueDAO movimentos;
    public ProdutoEstoqueServiceImpl(){this(new ProdutoDAOImpl(),new ContatoDAOImpl(),new MovimentacaoEstoqueDAOImpl());}
    public ProdutoEstoqueServiceImpl(ProdutoDAO produtos,ContatoDAO contatos,MovimentacaoEstoqueDAO movimentos){this.produtos=Objects.requireNonNull(produtos);this.contatos=Objects.requireNonNull(contatos);this.movimentos=Objects.requireNonNull(movimentos);}
    @Override public long cadastrar(Produto produto){
        if(produto.getId()!=null||produto.getEstoqueAtual().signum()!=0)throw new IllegalArgumentException("Novo produto deve iniciar sem ID e com estoque zero");
        long id=ConnectionFactory.executarEmTransacao(c->produtos.inserir(c,produto));produto.setId(id);return id;
    }
    @Override public boolean atualizar(Produto produto){return ConnectionFactory.executarEmTransacao(c->produtos.atualizar(c,produto));}
    @Override public boolean excluir(long id){return ConnectionFactory.executarEmTransacao(c->produtos.excluir(c,id));}
    @Override public Optional<Produto> buscarPorId(long id){return ConnectionFactory.executarEmTransacao(c->produtos.buscarPorId(c,id,false));}
    @Override public List<Produto> listar(){return ConnectionFactory.executarEmTransacao(produtos::listar);}
    @Override public List<Produto> listarEstoqueBaixo(){return listar().stream().filter(Produto::isEstoqueBaixo).toList();}
    @Override public void entradaLote(long produtoId,long fornecedorId,BigDecimal quantidade,String observacao){movimentar(produtoId,fornecedorId,quantidade,observacao,true);}
    @Override public void registrarPerda(long produtoId,BigDecimal quantidade,String observacao){movimentar(produtoId,null,quantidade,observacao,false);}
    private void movimentar(long id,Long fornecedor,BigDecimal quantidade,String observacao,boolean entrada){
        validarQuantidade(quantidade);
        ConnectionFactory.executarEmTransacao(c->{
            if(entrada && !(contatos.buscarPorId(c,fornecedor).orElseThrow(()->new IllegalArgumentException("Fornecedor não encontrado")) instanceof Fornecedor))throw new IllegalArgumentException("Contato não é fornecedor");
            Produto p=produtos.buscarPorId(c,id,true).orElseThrow(()->new IllegalArgumentException("Produto não encontrado"));
            if(entrada)p.adicionarEstoque(quantidade);else p.removerEstoque(quantidade);
            if(!produtos.atualizarEstoque(c,id,p.getEstoqueAtual()))throw new IllegalStateException("Produto não atualizado");
            movimentos.inserir(c,new MovimentacaoEstoque(null,p,entrada?TipoMovimentacaoEstoque.ENTRADA:TipoMovimentacaoEstoque.SAIDA,quantidade,observacao,LocalDateTime.now()),fornecedor,entrada?"LOTE":"PERDA",p.getCustoProducao());return null;
        });
    }
    static void validarQuantidade(BigDecimal q){if(q==null||q.signum()<=0||q.stripTrailingZeros().scale()>3||q.compareTo(new BigDecimal("999999999.999"))>0)throw new IllegalArgumentException("Quantidade inválida para DECIMAL(12,3)");}
}
