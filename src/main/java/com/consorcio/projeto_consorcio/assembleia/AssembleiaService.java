package com.consorcio.projeto_consorcio.assembleia;

import com.consorcio.projeto_consorcio.assembleia.strategy.ContemplacaoStrategy;
import com.consorcio.projeto_consorcio.assembleia.strategy.SorteioStrategy;
import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcioRepository;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.cota.CotaRepository;
import com.consorcio.projeto_consorcio.cota.enums.StatusCota;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssembleiaService {
    private final GrupoConsorcioRepository grupoConsorcioRepository;
    private final CotaRepository cotaRepository;

    public AssembleiaService(GrupoConsorcioRepository grupoConsorcioRepository, CotaRepository cotaRepository){
        this.grupoConsorcioRepository = grupoConsorcioRepository;
        this.cotaRepository = cotaRepository;
    }

    @Transactional
    public Cota realizarSorteio(Long grupoId){
        GrupoConsorcio grupoConsorcio = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Erro: Grupo não encontrado!"));

        //valida o sorteio
        grupoConsorcio.getState().validarSorteio();
        //busca os elegiveis
        List<Cota> elegiveis = cotaRepository.findByGrupoIdAndStatus(grupoId, StatusCota.ATIVA);
        //realiza o sorteio com os candidatos
        ContemplacaoStrategy estrategiaSorteio = new SorteioStrategy();
        Cota cotaVencedora = estrategiaSorteio.elegerVencedor(elegiveis);
        //atualiza o status da cota para contemplada
        cotaVencedora.setStatus(StatusCota.CONTEMPLADA);
        //salva no banco
        cotaRepository.save(cotaVencedora);
        System.out.println("Parabéns! A cota número " + cotaVencedora.getNumeroCota() + " foi contemplada!");
        return cotaVencedora;
    }
}
