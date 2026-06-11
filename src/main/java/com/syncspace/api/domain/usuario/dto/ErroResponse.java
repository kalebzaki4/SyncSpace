package com.syncspace.api.domain.usuario.dto;

import java.time.LocalDateTime;

public record ErroResponse(
        String mensagem,
        LocalDateTime timestamp
) {
}
