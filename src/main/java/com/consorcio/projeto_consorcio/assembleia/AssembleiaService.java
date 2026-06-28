package com.consorcio.projeto_consorcio.assembleia;

import com.consorcio.projeto_consorcio.assembleia.strategy.ContemplacaoStrategy;
import com.consorcio.projeto_consorcio.assembleia.strategy.SorteioStrategy;
import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcioRepository;
import com.consorcio.projeto_consorcio.core.exception.EntidadeNaoEncontradaException;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.cota.CotaRepository;
import com.consorcio.projeto_consorcio.cota.enums.StatusCota;
import com.consorcio.projeto_consorcio.blockchain.BlockchainGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AssembleiaService {
    private final GrupoConsorcioRepository grupoConsorcioRepository;
    private final CotaRepository cotaRepository;
    private final AssembleiaRepository assembleiaRepository;
    private final BlockchainGateway blockchainGateway;

    public AssembleiaService(GrupoConsorcioRepository grupoConsorcioRepository, CotaRepository cotaRepository, AssembleiaRepository assembleiaRepository, BlockchainGateway blockchainGateway){
        this.grupoConsorcioRepository = grupoConsorcioRepository;
        this.cotaRepository = cotaRepository;
        this.assembleiaRepository = assembleiaRepository;
        this.blockchainGateway = blockchainGateway;
    }

    @Transactional
    public Cota realizarSorteio(Long grupoId){
        GrupoConsorcio grupoConsorcio = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Grupo não encontrado!"));

        grupoConsorcio.getState().validarSorteio();

        int assembleiasCount = 0;
        for (Assembleia a : assembleiaRepository.findAll()) {
            if (a.getGrupo().getId().equals(grupoId)) {
                assembleiasCount++;
            }
        }
        int cicloAtual = assembleiasCount + 1;

        blockchainGateway.fecharMesAtual(grupoConsorcio.getEnderecoContrato(), cicloAtual);

        List<Cota> elegiveis = cotaRepository.findByGrupoConsorcioIdAndStatus(grupoId, StatusCota.ATIVA);
        ContemplacaoStrategy estrategiaSorteio = new SorteioStrategy();
        Cota cotaVencedora = estrategiaSorteio.elegerVencedor(elegiveis);
        cotaVencedora.setStatus(StatusCota.CONTEMPLADA);
        cotaRepository.save(cotaVencedora);

        Assembleia registroDoSorteio = new Assembleia();
        registroDoSorteio.setGrupo(grupoConsorcio);
        registroDoSorteio.setCotaContemplada(cotaVencedora);
        registroDoSorteio.setDataAssembleia(LocalDate.now());
        assembleiaRepository.save(registroDoSorteio);

        blockchainGateway.contemplarVencedor(grupoConsorcio.getEnderecoContrato(), cotaVencedora.getUsuario().getCarteiraWeb3(), 0);

        if (cicloAtual < grupoConsorcio.getDuracaoMeses()) {
            blockchainGateway.abrirNovoMes(grupoConsorcio.getEnderecoContrato(), cicloAtual + 1);
        }

        System.out.println("Parabéns! A cota número " + cotaVencedora.getNumeroCota() + " foi contemplada!");
        return cotaVencedora;
    }
}
