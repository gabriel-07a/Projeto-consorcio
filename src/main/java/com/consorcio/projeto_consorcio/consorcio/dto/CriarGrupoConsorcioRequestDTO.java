package com.consorcio.projeto_consorcio.consorcio.dto;

import com.consorcio.projeto_consorcio.consorcio.enums.TipoLance;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CriarGrupoConsorcioRequestDTO (
        @NotBlank(message = "O nome é obrigatório!")
        String nome,

        @NotNull(message = "O valor da cota não pode ser nulo!")
        @DecimalMin(value = "100.00", message = "Erro: O valor da cota não pode ser menos que R$100,00!")
        BigDecimal valorCota,

        @NotNull(message = "As vagas maximas são obrigatórias!")
        @Positive(message = "As vagas máximas devem ser maiores que zero!")
        Integer vagasMaximas,

        @NotNull(message = "A duração não pode ser nula!")
        Integer duracaoMeses,

        @NotBlank(message = "O endereço do smart Contract deve ser preenchido!")
        @Size(min = 42, max = 42, message = "O endereço do Smart Contract deve ter 42 caracteres!")
        @Pattern(
                regexp = "^0x[a-fA-F0-9]{40}$",
                message = "O endereço do Smart Contract deve ser em um formato válido!"
        )
        String enderecoContrato,

        @NotNull(message = "O campo Aceita Lances não pode ser nulo!")
        Boolean aceitaLances,

        TipoLance tipoLanceAdicional

){
}
