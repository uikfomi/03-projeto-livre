package com.salgaderia.app.exception;

import java.math.BigDecimal;

public class EstoqueInsuficienteException extends RuntimeException {
    private final String nomeProduto;
    private final BigDecimal estoqueDisponivel;
    private final BigDecimal quantidadeSolicitada;

    public EstoqueInsuficienteException(String nomeProduto, BigDecimal estoqueDisponivel,
                                        BigDecimal quantidadeSolicitada) {
        super("Estoque insuficiente para " + nomeProduto + ": disponível " + estoqueDisponivel
                + ", solicitado " + quantidadeSolicitada);
        this.nomeProduto = nomeProduto;
        this.estoqueDisponivel = estoqueDisponivel;
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public String getNomeProduto() { return nomeProduto; }
    public BigDecimal getEstoqueDisponivel() { return estoqueDisponivel; }
    public BigDecimal getQuantidadeSolicitada() { return quantidadeSolicitada; }
}
