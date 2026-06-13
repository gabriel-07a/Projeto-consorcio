package com.consorcio.projeto_consorcio.consorcio.dto;

import com.consorcio.projeto_consorcio.consorcio.enums.StatusGrupo;

public record GrupoConsorcioResponseDTO(
        Long grupoId,
        String Nome,
        StatusGrupo nomeStatus,
        Integer vagasDisponiveis
){
}
