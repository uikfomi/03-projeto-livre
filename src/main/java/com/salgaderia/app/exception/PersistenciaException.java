package com.salgaderia.app.exception;

import java.sql.SQLException;

public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String mensagem, SQLException causa) {
        super(mensagem, causa);
    }
}
