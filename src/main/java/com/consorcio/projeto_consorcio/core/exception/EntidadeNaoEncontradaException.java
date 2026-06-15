package com.consorcio.projeto_consorcio.core.exception;

public class EntidadeNaoEncontradaException extends RuntimeException{
    //essa exception é para quando um registro não existe no bd
    public EntidadeNaoEncontradaException(String mensagem){
        super(mensagem);
    }
}
