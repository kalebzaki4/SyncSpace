package com.syncspace.api.exception;

import com.syncspace.api.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleUsuarioNaoEncontrado(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioJaCadastradoException.class)
    public ResponseEntity<ErroResponse> handleUsuarioJaCadastrado(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SalaJaCadastradaException.class)
    public ResponseEntity<ErroResponse> handleSalaJaCadastrada(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SalaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleSalaNaoEncontrada(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErroResponse> buildResponse(String message, HttpStatus status) {
        ErroResponse erro = new ErroResponse(message, LocalDateTime.now());
        return ResponseEntity.status(status).body(erro);
    }
}

