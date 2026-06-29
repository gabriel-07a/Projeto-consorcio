package com.consorcio.projeto_consorcio.consorcio.dto;

import java.math.BigDecimal;

public record GrupoCaixaResponseDTO(
        Long grupoId,
        String nomeGrupo,
        BigDecimal saldoCaixa,
        BigDecimal valorCartaCredito
) {
}
