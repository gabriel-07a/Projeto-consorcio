package com.consorcio.projeto_consorcio.pagamentos.dto;

import com.consorcio.projeto_consorcio.pagamentos.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoResponseDTO(
        Long id,
        Integer numeroParcela,
        BigDecimal valor,
        LocalDate dataVencimento,
        StatusPagamento statusPagamento,
        String hashDaTransacao
) {
}
