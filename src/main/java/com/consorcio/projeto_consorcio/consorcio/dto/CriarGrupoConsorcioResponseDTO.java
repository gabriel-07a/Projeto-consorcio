package com.consorcio.projeto_consorcio.consorcio.dto;

public record CriarGrupoConsorcioResponseDTO(
        Long grupoId,
        String Nome,
        String nomeStatus,
        Integer vagasDisponiveis
){
}
