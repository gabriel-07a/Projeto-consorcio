package com.consorcio.projeto_consorcio.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO (
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String senhaLimpa,

        @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "Formato de carteira Web3 inválido")
        String carteiraWeb3,

        @NotBlank(message = "O documento (CPF/Tax ID) é obrigatório")
        String taxId,

        @NotBlank(message = "O código do país é obrigatório")
        String countryCode
){}
