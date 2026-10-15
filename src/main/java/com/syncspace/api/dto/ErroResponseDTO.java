package com.syncspace.api.dto;

import java.time.LocalDateTime;

public record ErroResponseDTO(
        int status,
        String mensagem,
        LocalDateTime timestamp
) {}