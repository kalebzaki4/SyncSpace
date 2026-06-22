package com.syncspace.api.infra.exception;

import com.syncspace.api.domain.usuario.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioJaCadastradoException.class)
    public ResponseEntity<ErroResponse> handleUsuarioJaCadastrado(UsuarioJaCadastradoException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SalaJaCadastradaException.class)
    public ResponseEntity<ErroResponse> handleSalaJaCadastrada(SalaJaCadastradaException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SalaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleSalaNaoEncontrada(SalaNaoEncontradaException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ErroAoGerarTokenJWTException.class)
    public ResponseEntity<ErroResponse> handleErroAoGerarTokenJWT(ErroAoGerarTokenJWTException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErroResponse> buildResponse(String message, HttpStatus status) {
        ErroResponse erro = new ErroResponse(message, LocalDateTime.now());
        return ResponseEntity.status(status).body(erro);
    }
}
