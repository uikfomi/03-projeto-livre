package com.salgaderia.app.model;

import java.math.BigDecimal;

/** Item imutável: o preço é registrado no momento da inclusão no pedido. */
public final class PedidoItem {
    private final Long id;
    private final Produto produto;
    private final BigDecimal quantidade;
    private final BigDecimal precoUnitario;
    public PedidoItem(Long id, Produto produto, BigDecimal quantidade, BigDecimal precoUnitario) {
        this.id = Validacao.id(id);
        this.produto = Validacao.obrigatorio(produto, "produto");
        this.quantidade = Validacao.positivo(quantidade, "quantidade");
        this.precoUnitario = Validacao.naoNegativo(precoUnitario, "preço unitário");
    }
    public Long getId() { return id; }
    public Produto getProduto() { return produto; }
    public BigDecimal getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public BigDecimal calcularSubtotal() { return precoUnitario.multiply(quantidade); }
}
