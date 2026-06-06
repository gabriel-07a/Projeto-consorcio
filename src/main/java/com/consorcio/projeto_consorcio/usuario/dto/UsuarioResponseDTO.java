package com.consorcio.projeto_consorcio.usuario.dto;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String carteiraWeb3,
        String countryCode
) {}
