package com.br.syncspace.infra.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ReservaNaoEncontradaException.class)
    public ResponseEntity<ErrorMessageDTO> handleReservaNaoEncontrada(ReservaNaoEncontradaException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CapacidadeExcedidaException.class)
    public ResponseEntity<ErrorMessageDTO> handleCapacidadeExcedida(CapacidadeExcedidaException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HoraErradaException.class)
    public ResponseEntity<ErrorMessageDTO> handleHoraErrada(HoraErradaException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SalaNaoCriadaException.class)
    public ResponseEntity<ErrorMessageDTO> handleSalaNaoCriada(SalaNaoCriadaException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SalaNaoEncontradaException.class)
    public ResponseEntity<ErrorMessageDTO> handleSalaNaoEncontrada(SalaNaoEncontradaException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErrorMessageDTO> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailNaoEncontradoException.class)
    public ResponseEntity<ErrorMessageDTO> handleEmailNaoEncontrado(EmailNaoEncontradoException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErrorMessageDTO> handleEmailJaCadastrado(EmailJaCadastradoException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SenhaInvalidaException.class)
    public ResponseEntity<ErrorMessageDTO> handleSenhaInvalida(SenhaInvalidaException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ValidationErrorDTO> errosCampos = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ValidationErrorDTO(erro.getField(), erro.getDefaultMessage()))
                .toList();

        return buildResponse("Falha na validação dos campos informados.", HttpStatus.BAD_REQUEST, errosCampos);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessageDTO> handleConstraintViolation(ConstraintViolationException ex) {
        List<ValidationErrorDTO> errosCampos = ex.getConstraintViolations().stream()
                .map(violacao -> new ValidationErrorDTO(
                        violacao.getPropertyPath().toString(),
                        violacao.getMessage()))
                .toList();

        return buildResponse("Parâmetros de requisição inválidos.", HttpStatus.BAD_REQUEST, errosCampos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return buildResponse("O corpo da requisição é inválido ou está malformatado.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessageDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String tipoEsperado = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconhecido";
        String mensagem = String.format("O parâmetro '%s' recebeu o valor '%s', mas esperava o tipo '%s'.",
                ex.getName(), ex.getValue(), tipoEsperado);

        return buildResponse(mensagem, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessageDTO> handleMissingParams(MissingServletRequestParameterException ex) {
        String mensagem = String.format("O parâmetro obrigatório '%s' não foi informado.", ex.getParameterName());
        return buildResponse(mensagem, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessageDTO> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse("Usuário ou senha inválidos.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessageDTO> handleAuthenticationException(AuthenticationException ex) {
        return buildResponse("Falha na autenticação. Token ausente ou inválido.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessageDTO> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse("Acesso negado. Você não possui permissão para acessar este recurso.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessageDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return buildResponse("Conflito de integridade nos dados (chave duplicada ou restrição de banco).", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleEntityNotFound(EntityNotFoundException ex) {
        return buildResponse("Recurso solicitado não foi encontrado no banco de dados.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessageDTO> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String mensagem = String.format("O método HTTP '%s' não é suportado para este endpoint. Métodos aceitos: %s",
                ex.getMethod(), ex.getSupportedHttpMethods());
        return buildResponse(mensagem, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorMessageDTO> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return buildResponse("Tipo de mídia não suportado. Certifique-se de usar 'application/json'.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleNoResourceFound(NoResourceFoundException ex) {
        return buildResponse("A rota solicitada não existe nesta API.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleGenericException(Exception ex) {
        log.error("Erro interno não tratado no servidor: ", ex);
        return buildResponse("Ocorreu um erro interno no servidor. Tente novamente mais tarde.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorMessageDTO> buildResponse(String message, HttpStatus status) {
        return buildResponse(message, status, null);
    }

    private ResponseEntity<ErrorMessageDTO> buildResponse(String message, HttpStatus status, Object details) {
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(
                message,
                Instant.now().toString(),
                details
        );
        return ResponseEntity.status(status).body(errorMessageDTO);
    }
}