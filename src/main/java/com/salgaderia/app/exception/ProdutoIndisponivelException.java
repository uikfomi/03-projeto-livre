package com.salgaderia.app.exception;

/** Produto bloqueado para venda, independentemente do saldo físico. */
public final class ProdutoIndisponivelException extends IllegalArgumentException {
    public ProdutoIndisponivelException(String produto) { super(produto + ": indisponível no momento."); }
}
