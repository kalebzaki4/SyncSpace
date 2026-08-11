package com.br.syncspace.infra.exception;

public class SalaNaoCriadaException extends RuntimeException {
    public SalaNaoCriadaException(String message) {
        super(message);
    }
}
