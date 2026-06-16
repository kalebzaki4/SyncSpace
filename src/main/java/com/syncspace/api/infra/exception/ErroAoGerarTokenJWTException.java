package com.syncspace.api.infra.exception;

import com.auth0.jwt.exceptions.JWTCreationException;

public class ErroAoGerarTokenJWTException extends RuntimeException {
    public ErroAoGerarTokenJWTException(String message, JWTCreationException exception) {
        super(message);
    }
}
