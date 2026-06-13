package com.consorcio.projeto_consorcio.consorcio;

import com.consorcio.projeto_consorcio.consorcio.dto.ApagarGrupoConsorcioResponseDTO;
import com.consorcio.projeto_consorcio.consorcio.dto.CriarGrupoConsorcioRequestDTO;
import com.consorcio.projeto_consorcio.consorcio.dto.CriarGrupoConsorcioResponseDTO;
import com.consorcio.projeto_consorcio.consorcio.enums.StatusGrupo;
import com.consorcio.projeto_consorcio.consorcio.enums.TipoLance;
import com.consorcio.projeto_consorcio.cota.CotaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class GrupoConsorcioService {
    private final GrupoConsorcioRepository grupoConsorcioRepository;
    private final CotaRepository cotaRepository;

    public GrupoConsorcioService(GrupoConsorcioRepository grupoConsorcioRepository, CotaRepository cotaRepository){
        this.grupoConsorcioRepository = grupoConsorcioRepository;
        this.cotaRepository = cotaRepository;
    }

    @Transactional
    public CriarGrupoConsorcioResponseDTO criarNovoGrupoConsorcio(CriarGrupoConsorcioRequestDTO requestDTO){
        if(requestDTO.vagasMaximas() < requestDTO.duracaoMeses()){
            throw new RuntimeException("Erro: A duração do Grupo não pode ser maior que a quantidade de vagas maximas!");
        }

        TipoLance tipoLanceFinal;
        if(requestDTO.aceitaLances()){
            if(requestDTO.tipoLanceAdicional() == null) throw new RuntimeException("Erro: Se o grupo aceita lances o campo Tipo Lance Adicional não pode ser nulo!");
            tipoLanceFinal = requestDTO.tipoLanceAdicional();
        }else{
            tipoLanceFinal = TipoLance.NENHUM;
        }

        if(grupoConsorcioRepository.existsByNome(requestDTO.nome())) throw new RuntimeException("Erro: Esse nome já foi usado!");

        GrupoConsorcio novoGrupo = new GrupoConsorcio();
        novoGrupo.setNome(requestDTO.nome());
        novoGrupo.setValorCota(requestDTO.valorCota());
        novoGrupo.setVagasMaximas(requestDTO.vagasMaximas());
        novoGrupo.setDuracaoMeses(requestDTO.duracaoMeses());
        novoGrupo.setEnderecoContrato(requestDTO.enderecoContrato());
        novoGrupo.setAceitaLances(requestDTO.aceitaLances());
        novoGrupo.setTipoLanceAdicional(tipoLanceFinal);

        //criar no entity
        //novoGrupo.setVagasDisponiveis(requestDTO.vagasMaximas());
        novoGrupo.setStatus(StatusGrupo.EM_FORMACAO);

        GrupoConsorcio grupoSalvado = grupoConsorcioRepository.save(novoGrupo);

        return new CriarGrupoConsorcioResponseDTO(
                grupoSalvado.getId(),
                grupoSalvado.getNome(),
                grupoSalvado.getStatus().name(),
                grupoSalvado.getVagasMaximas()
        );
    }


    @Transactional
    public String iniciarGrupo(Long grupoId){
        GrupoConsorcio grupoConsorcio = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Erro: Essa grupo não existe!"));
        grupoConsorcio.getState().comecarConsorcio();

        grupoConsorcio.setStatus(StatusGrupo.EM_ANDAMENTO);

        grupoConsorcioRepository.save(grupoConsorcio);

        return "Grupo: " + grupoConsorcio.getNome()+ " iniciado com suscesso!";

    }


    @Transactional
    public ApagarGrupoConsorcioResponseDTO apagarGrupoConsorcio(Long grupoId, String enderecoContrato){
        GrupoConsorcio grupoConsorcio = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Erro: Esse grupo não existe!"));

        grupoConsorcio.getState().validaExclusaoDeConsorcio(grupoConsorcio, enderecoContrato);

        grupoConsorcioRepository.delete(grupoConsorcio);

        return new ApagarGrupoConsorcioResponseDTO(
                grupoId,
                grupoConsorcio.getNome()
        );
    }
}
