package com.syncspace.api.dto;

import java.time.LocalDateTime;

public record ErroResponse(
        String mensagem,
        LocalDateTime timestamp
) {
}
