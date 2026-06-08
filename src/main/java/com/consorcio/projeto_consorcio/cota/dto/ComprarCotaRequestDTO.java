package com.consorcio.projeto_consorcio.cota.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ComprarCotaRequestDTO(
        @NotNull(message = "O id do usuário é obrigátorio!")
        Long usuarioId,

        @NotNull(message = "O id do grupo é obrigatório!")
        Long grupoId
) {
}
