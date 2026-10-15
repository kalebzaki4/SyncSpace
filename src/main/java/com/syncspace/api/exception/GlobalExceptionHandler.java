package com.syncspace.api.exception;

import com.syncspace.api.dto.ErroResponseDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponseDTO> handlerAccessDeniedException(AccessDeniedException ex) {
        ErroResponseDTO erro = new ErroResponseDTO(403, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> handlerUsuarioNaoEncontradoException(UsuarioNaoEncontradoException ex) {
        ErroResponseDTO erro = new ErroResponseDTO(404, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);

    }

    @ExceptionHandler(UsuarioJaExisteException.class)
    public ResponseEntity<ErroResponseDTO> handlerUsuarioJaExisteException(UsuarioJaExisteException ex) {
        ErroResponseDTO erro = new ErroResponseDTO(409, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(PacienteNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> handlerPacienteNaoEncontradoException(PacienteNaoEncontradoException ex) {
        ErroResponseDTO erro = new ErroResponseDTO(404, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(PacienteJaExisteException.class)
    public ResponseEntity<ErroResponseDTO> hanclerPacienteJaExisteException(PacienteJaExisteException ex) {
        ErroResponseDTO erro = new ErroResponseDTO(409, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(AtendenteNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> handlerAtendenteNaoEncontradoException(AtendenteNaoEncontradoException ex) {
        ErroResponseDTO erro = new ErroResponseDTO(404, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MedicoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> handlerMedicoNaoEncontradoException(MedicoNaoEncontradoException ex) {
        ErroResponseDTO erro = new ErroResponseDTO(404, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(AtendenteJaExisteException.class)
    public ResponseEntity<ErroResponseDTO> handlerAtendenteJaExisteException(AtendenteJaExisteException ex) {
        ErroResponseDTO erro = new ErroResponseDTO(409, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(MedicoJaExisteExeption.class)
    public ResponseEntity<ErroResponseDTO> handlerMedicoJaExisteExeption(MedicoJaExisteExeption ex) {
        ErroResponseDTO erro = new ErroResponseDTO(409, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }
}