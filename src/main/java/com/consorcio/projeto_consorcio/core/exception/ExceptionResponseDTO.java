package com.consorcio.projeto_consorcio.core.exception;

import java.time.Instant;

public record ExceptionResponseDTO (
        Instant timestamp,
        Integer status,
        String erro,
        String mensagem,
        String caminho
){}
