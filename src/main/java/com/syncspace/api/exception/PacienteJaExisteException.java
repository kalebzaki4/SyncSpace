package com.syncspace.api.exception;

public class PacienteJaExisteException extends RuntimeException {
    public PacienteJaExisteException(String message) {
        super(message);
    }
}
