package com.salgaderia.app.exception;

public class AutenticacaoInvalidaException extends RuntimeException {
    public AutenticacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
