package com.br.syncspace.infra.exception;

public class CapacidadeExcedidaException extends RuntimeException {
    public CapacidadeExcedidaException(String message) {
        super(message);
    }
}
