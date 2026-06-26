package com.consorcio.projeto_consorcio.lances;

import com.consorcio.projeto_consorcio.core.exception.RegraDeNegocioException;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.cota.CotaRepository;
import com.consorcio.projeto_consorcio.usuario.Usuario;
import com.consorcio.projeto_consorcio.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
public class LanceService {

    @Autowired
    private LanceRepository lanceRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CotaRepository cotaRepository;

    @Transactional
    public void registraLancePelaBlockchain(String carteiraCliente, BigInteger valorWei, Long numeroCiclo, Integer tipoLanceEnum, String hashTransacao) {
        Usuario usuario = usuarioRepository.findByCarteiraWeb3IgnoreCase(carteiraCliente)
                .orElseThrow(() -> new RegraDeNegocioException("Erro: Lance identificado de uma carteira não cadastrada: " + carteiraCliente));

        Cota cota = cotaRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RegraDeNegocioException("Erro: Cota não encontrada para o usuário: " + usuario.getNome()));

        //converte o wei para BigDecimal
        BigDecimal valorLance = new BigDecimal(valorWei).divide(new BigDecimal("1000000000000000000"));


        String tipoLanceStr = "LIVRE";
        if (tipoLanceEnum == 1) tipoLanceStr = "FIXO";
        if (tipoLanceEnum == 2) tipoLanceStr = "EMBUTIDO";

        Lance lance = new Lance();
        lance.setCota(cota);
        lance.setNumeroCiclo(numeroCiclo.intValue());
        lance.setValorLance(valorLance);
        lance.setTipoLance(tipoLanceStr);
        String txHashBlockchain = hashTransacao;
        lance.setHashTransacao(txHashBlockchain);
        lance.setDataRegistro(LocalDateTime.now());
        lance.setVencedor(false); //dps de decidido quem venceu que esse campo pode se tornar true

        lanceRepository.save(lance);


    }

}
