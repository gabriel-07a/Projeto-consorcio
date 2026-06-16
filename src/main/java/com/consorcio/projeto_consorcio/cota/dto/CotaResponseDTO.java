package com.consorcio.projeto_consorcio.cota.dto;

public record CotaResponseDTO(
        Long idCota,
        Integer numeroCota,
        String nomeUsuario,
        String nomeConsorcio
) {
}
