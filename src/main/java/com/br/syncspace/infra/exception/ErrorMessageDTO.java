package com.br.syncspace.infra.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorMessageDTO(
        String message,
        String timestamp,
        Object details
) {
    public ErrorMessageDTO(String message) {
        this(message, Instant.now().toString(), null);
    }

    public ErrorMessageDTO(String message, Object details) {
        this(message, Instant.now().toString(), details);
    }
}