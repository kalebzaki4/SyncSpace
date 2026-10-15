package com.syncspace.api.exception;

public class MedicoJaExisteExeption extends RuntimeException {
    public MedicoJaExisteExeption(String message) {
        super(message);
    }
}
