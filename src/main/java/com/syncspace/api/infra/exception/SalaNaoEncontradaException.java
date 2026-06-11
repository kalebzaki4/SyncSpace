package com.syncspace.api.infra.exception;

public class SalaNaoEncontradaException extends RuntimeException {
    public SalaNaoEncontradaException(String message) {
        super(message);
    }
}
