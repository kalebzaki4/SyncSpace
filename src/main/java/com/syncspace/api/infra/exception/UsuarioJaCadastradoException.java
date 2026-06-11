package com.syncspace.api.infra.exception;

public class UsuarioJaCadastradoException extends RuntimeException {
    public UsuarioJaCadastradoException(String message) {
        super(message);
    }
}
