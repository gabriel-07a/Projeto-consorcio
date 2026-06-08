package com.consorcio.projeto_consorcio.assembleia.dto;

public record AssembleiaResponseDTO (
    String mensagem,
    Integer numeroCotaVencedor,
    String nomeVencedor
) {}
