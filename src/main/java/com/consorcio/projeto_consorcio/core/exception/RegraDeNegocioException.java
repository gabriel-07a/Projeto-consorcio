package com.consorcio.projeto_consorcio.core.exception;

public class RegraDeNegocioException extends RuntimeException {
    //para quando uma regra é violada
    public RegraDeNegocioException(String message) {
        super(message);
    }
}
