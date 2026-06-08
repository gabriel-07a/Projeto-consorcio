package com.consorcio.projeto_consorcio.cota.dto;

import jakarta.validation.constraints.NotNull;

public record CancelarCotaRequestDTO(
        @NotNull(message = "O id da cota não pode ser nulo!")
        Long cotaid
) {
}
