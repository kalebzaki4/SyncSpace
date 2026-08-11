package com.br.syncspace.infra.exception;

public record ValidationErrorDTO(
        String campoErro,
        String mensagemErro
) {}