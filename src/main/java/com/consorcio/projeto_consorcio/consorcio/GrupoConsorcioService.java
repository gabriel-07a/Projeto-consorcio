package com.consorcio.projeto_consorcio.consorcio;

import com.consorcio.projeto_consorcio.consorcio.dto.ApagarGrupoConsorcioResponseDTO;
import com.consorcio.projeto_consorcio.consorcio.dto.CriarGrupoConsorcioRequestDTO;
import com.consorcio.projeto_consorcio.consorcio.dto.CriarGrupoConsorcioResponseDTO;
import com.consorcio.projeto_consorcio.consorcio.dto.GrupoConsorcioResponseDTO;
import com.consorcio.projeto_consorcio.consorcio.enums.StatusGrupo;
import com.consorcio.projeto_consorcio.consorcio.enums.TipoLance;
import com.consorcio.projeto_consorcio.core.exception.EntidadeNaoEncontradaException;
import com.consorcio.projeto_consorcio.core.exception.RegraDeNegocioException;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.cota.CotaRepository;
import com.consorcio.projeto_consorcio.cota.dto.CotaResponseDTO;
import com.consorcio.projeto_consorcio.blockchain.BlockchainGateway;
import com.consorcio.projeto_consorcio.pagamentos.PagamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GrupoConsorcioService {
    private final GrupoConsorcioRepository grupoConsorcioRepository;
    private final CotaRepository cotaRepository;
    private final PagamentoService pagamentoService;
    private final BlockchainGateway blockchainGateway;

    public GrupoConsorcioService(GrupoConsorcioRepository grupoConsorcioRepository, CotaRepository cotaRepository, PagamentoService pagamentoService, BlockchainGateway blockchainGateway){
        this.grupoConsorcioRepository = grupoConsorcioRepository;
        this.cotaRepository = cotaRepository;
        this.pagamentoService = pagamentoService;
        this.blockchainGateway = blockchainGateway;
    }

    @Transactional
    public CriarGrupoConsorcioResponseDTO criarNovoGrupoConsorcio(CriarGrupoConsorcioRequestDTO requestDTO){
        if(requestDTO.vagasMaximas() < requestDTO.duracaoMeses()){
            throw new RegraDeNegocioException("Erro: A duração do Grupo não pode ser maior que a quantidade de vagas maximas!");
        }

        TipoLance tipoLanceFinal;
        if(requestDTO.aceitaLances()){
            if(requestDTO.tipoLanceAdicional() == null) throw new RegraDeNegocioException("Erro: Se o grupo aceita lances o campo Tipo Lance Adicional não pode ser nulo!");
            tipoLanceFinal = requestDTO.tipoLanceAdicional();
        }else{
            tipoLanceFinal = TipoLance.NENHUM;
        }

        if(grupoConsorcioRepository.existsByNome(requestDTO.nome())) throw new RegraDeNegocioException("Erro: Esse nome já foi usado!");

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

    @Transactional(readOnly = true)
    public GrupoConsorcioResponseDTO buscarGrupo(Long id){
        GrupoConsorcio grupo = grupoConsorcioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Grupo não encontrado!"));

        return new GrupoConsorcioResponseDTO(
                grupo.getId(),
                grupo.getNome(),
                grupo.getStatus(),
                grupo.getVagasMaximas()
        );
    }

    @Transactional(readOnly = true)
    public List<CotaResponseDTO> listarCotas(Long grupoId){
        if(!grupoConsorcioRepository.existsById(grupoId)) throw new EntidadeNaoEncontradaException("Erro: Este grupo não existe!");

        List<Cota> cotasRetornadas = cotaRepository.findByGrupoConsorcioId(grupoId);

        return cotasRetornadas.stream()
                .map(cota -> new CotaResponseDTO(
                        cota.getId(),
                        cota.getNumeroCota(),
                        cota.getUsuario().getNome(),
                        cota.getGrupoConsorcio().getNome()
                ))
                .toList();

    }

    @Transactional
    public List<GrupoConsorcioResponseDTO> buscarGrupos(StatusGrupo status){
        List<GrupoConsorcio> gruposRetornados;

        if(status == null){
            gruposRetornados = grupoConsorcioRepository.findAll();
        }else{
            gruposRetornados = grupoConsorcioRepository.findByStatus(status);
        }

        return gruposRetornados.stream()
                .map(grupo -> new GrupoConsorcioResponseDTO(
                        grupo.getId(),
                        grupo.getNome(),
                        grupo.getStatus(),
                        grupo.getVagasMaximas()
                ))
                .toList();
                //depois adicionar mais campos do dto e aqui
    }

    @Transactional
    public String iniciarGrupo(Long grupoId){
        GrupoConsorcio grupoConsorcio = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Essa grupo não existe!"));
        grupoConsorcio.getState().comecarConsorcio();

        grupoConsorcio.setStatus(StatusGrupo.EM_ANDAMENTO);

        grupoConsorcioRepository.save(grupoConsorcio);

        pagamentoService.criarParcelas(grupoConsorcio);//para gerar as parcelas de todos

        return "Grupo: " + grupoConsorcio.getNome()+ " iniciado com suscesso!";

    }

    @org.springframework.transaction.annotation.Transactional
    public String encerrarGrupo(Long grupoId){
        GrupoConsorcio grupo = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Essa grupo não existe!"));
        grupo.getState().encerrarConsorcio();
        grupo.setStatus(StatusGrupo.ENCERRADO);
        grupoConsorcioRepository.save(grupo);
        return "Grupo: " + grupo.getNome() + " encerrado com sucesso!";

    }


    @Transactional
    public ApagarGrupoConsorcioResponseDTO apagarGrupoConsorcio(Long grupoId, String enderecoContrato){
        GrupoConsorcio grupoConsorcio = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Esse grupo não existe!"));

        grupoConsorcio.getState().validaExclusaoDeConsorcio(grupoConsorcio, enderecoContrato);

        grupoConsorcioRepository.delete(grupoConsorcio);

        return new ApagarGrupoConsorcioResponseDTO(
                grupoId,
                grupoConsorcio.getNome()
        );
    }
}
