package com.syncspace.api.exception;

public class UsuarioJaExisteException extends RuntimeException {
  public UsuarioJaExisteException(String message) {
    super(message);
  }
}
