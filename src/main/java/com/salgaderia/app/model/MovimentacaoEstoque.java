package com.salgaderia.app.model;

import com.salgaderia.app.model.enums.TipoMovimentacaoEstoque;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimentacaoEstoque {
    private final Long id;
    private final Produto produto;
    private final TipoMovimentacaoEstoque tipo;
    private final BigDecimal quantidade;
    private final String observacao;
    private final LocalDateTime criadoEm;
    public MovimentacaoEstoque(Long id, Produto produto, TipoMovimentacaoEstoque tipo, BigDecimal quantidade,
                               String observacao, LocalDateTime criadoEm) {
        this.id = Validacao.id(id); this.produto = Validacao.obrigatorio(produto, "produto");
        this.tipo = Validacao.obrigatorio(tipo, "tipo"); this.quantidade = Validacao.positivo(quantidade, "quantidade");
        this.observacao = observacao; this.criadoEm = Validacao.obrigatorio(criadoEm, "data de criação");
    }
    public Long getId() { return id; }
    public Produto getProduto() { return produto; }
    public TipoMovimentacaoEstoque getTipo() { return tipo; }
    public BigDecimal getQuantidade() { return quantidade; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
