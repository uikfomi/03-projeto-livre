package com.salgaderia.app.model;

import java.math.BigDecimal;

/** Item imutável: o preço é registrado no momento da inclusão no pedido. */
public final class PedidoItem {
    private final Long id;
    private final Produto produto;
    private final BigDecimal quantidade;
    private final BigDecimal precoUnitario;
    private final String observacaoPersonalizada;
    private final PersonalizacaoItem personalizacao;
    public PedidoItem(Long id, Produto produto, BigDecimal quantidade, BigDecimal precoUnitario) {
        this(id, produto, quantidade, precoUnitario, null, null);
    }
    public PedidoItem(Long id, Produto produto, BigDecimal quantidade, BigDecimal precoUnitario,
                      String observacaoPersonalizada, PersonalizacaoItem personalizacao) {
        this.id = Validacao.id(id);
        this.produto = Validacao.obrigatorio(produto, "produto");
        this.quantidade = Validacao.positivo(quantidade, "quantidade");
        this.precoUnitario = Validacao.naoNegativo(precoUnitario, "preço unitário");
        if (observacaoPersonalizada != null && observacaoPersonalizada.length() > 512)
            throw new IllegalArgumentException("Descrição personalizada muito longa.");
        this.observacaoPersonalizada = observacaoPersonalizada;
        this.personalizacao = personalizacao;
    }
    public Long getId() { return id; }
    public Produto getProduto() { return produto; }
    public BigDecimal getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public String getObservacaoPersonalizada() { return observacaoPersonalizada; }
    public PersonalizacaoItem getPersonalizacao() { return personalizacao; }
    public PedidoItem comProdutoAtual(Produto atual) {
        return new PedidoItem(null, atual, quantidade, atual.getPrecoVenda(), observacaoPersonalizada, personalizacao);
    }
    public BigDecimal calcularSubtotal() { return precoUnitario.multiply(quantidade); }
}
