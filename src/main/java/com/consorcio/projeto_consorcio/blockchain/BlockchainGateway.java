package com.consorcio.projeto_consorcio.blockchain;

import java.math.BigDecimal;

public interface BlockchainGateway {
    /**
     * Resgistra a carteira de um novo participante na rede blockchain.
     */
    boolean registrarParticipante(String enderecoContrato, String carteiraClienteHex);

    /**
     * Avança o clico(mês) do consórcio na rede.
     */
    boolean abrirNovoMes(String enderecoContrato, Integer numeroCiclo);

    /**
     * Fecha os lances do clico(mês) atual.
     */
    boolean fecharMesAtual(String enderecoContrato, Integer numeroCiclo);

    /**
     * Deixa um participante inadimplente e aplica multa.
     */
    boolean aplicarInadimplencia(String enderecoContrato, String carteiraClienteHex, BigDecimal valorMulta);

    /**
     * Deixa um participante adimplente novamente.
     */
    boolean regularizarParticipante(String enderecoContrato, String carteiraClienteHex);

    /**
     * Paga o vencedor da contemplação.
     * O parâmetro tipoContemplacao usa o 0 para DRAW(Sorteio) e 1 para BID(Lance)
     */
    boolean contemplarVencedor(String enderecoContrato, String carteiraVencedorHex, Integer tipoContemplacao);

}
