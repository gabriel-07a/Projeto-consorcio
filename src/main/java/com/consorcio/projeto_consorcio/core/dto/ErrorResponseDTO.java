package com.consorcio.projeto_consorcio.core.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String mensagem,
        int status,
        LocalDateTime timestamp
) {
}
