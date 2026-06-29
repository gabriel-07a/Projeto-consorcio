package com.consorcio.projeto_consorcio.consorcio.dto;

import com.consorcio.projeto_consorcio.consorcio.enums.StatusGrupo;
import com.consorcio.projeto_consorcio.consorcio.enums.TipoLance;

public record GrupoConsorcioResponseDTO(
        Long grupoId,
        String Nome,
        StatusGrupo nomeStatus,
        Integer vagasDisponiveis,
        Boolean aceitaLances,
        TipoLance tipoLanceAdicional
){
}
