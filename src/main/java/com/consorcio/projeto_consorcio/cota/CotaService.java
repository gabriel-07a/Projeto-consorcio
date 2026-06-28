package com.consorcio.projeto_consorcio.cota;

import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcioRepository;

import com.consorcio.projeto_consorcio.consorcio.enums.StatusGrupo;
import com.consorcio.projeto_consorcio.core.exception.EntidadeNaoEncontradaException;
import com.consorcio.projeto_consorcio.blockchain.BlockchainGateway;
import com.consorcio.projeto_consorcio.cota.dto.CotaResponseDTO;
import com.consorcio.projeto_consorcio.cota.enums.StatusCota;
import com.consorcio.projeto_consorcio.pagamentos.PagamentoService;
import com.consorcio.projeto_consorcio.usuario.Usuario;
import com.consorcio.projeto_consorcio.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CotaService {
    private final PagamentoService pagamentoService;
    private final BlockchainGateway blockchainGateway;
    private CotaRepository cotaRepository;
    private UsuarioRepository usuarioRepository;
    private GrupoConsorcioRepository grupoConsorcioRepository;

    //injeção de dependencias via construtor
    public CotaService(CotaRepository cotaRepository, UsuarioRepository usuarioRepository, GrupoConsorcioRepository grupoConsorcioRepository, PagamentoService pagamentoService, BlockchainGateway blockchainGateway){
        this.cotaRepository = cotaRepository;
        this.usuarioRepository = usuarioRepository;
        this.grupoConsorcioRepository = grupoConsorcioRepository;
        this.pagamentoService = pagamentoService;
        this.blockchainGateway = blockchainGateway;
    }

    @Transactional // garante que ser der erro o banco defaz tudo
    public CotaResponseDTO comprarCota(Long usuarioId, Long grupoId){
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Usuário não cadastrado no sistema!"));
        GrupoConsorcio grupoConsorcio = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Grupo de Consórcio não encontrado no sistema!"));

        //chama a interface de grupoState, e se for grupoaberto ele adiciona
        //se não ele lança uma exceção
        grupoConsorcio.getState().validarNovoParticipante(grupoConsorcio, usuario);
        //se o state mudar o status para em andamento eu tenho que chamar esse método para criar as parcelas de todos os participanetes
        if(grupoConsorcio.getStatus() == StatusGrupo.EM_ANDAMENTO) pagamentoService.criarParcelas(grupoConsorcio);

        //pega o cotaRepository quantas cota o grupo tem atualmente
        int totalCotas = cotaRepository.countBygrupoConsorcioId(grupoConsorcio.getId());
        //pega a quantidade e incrementa
        int numeroDeCotasNovo = totalCotas + 1;

        Cota novaCota = new Cota();
        novaCota.setUsuario(usuario);
        novaCota.setGrupoConsorcio(grupoConsorcio);
        novaCota.setNumeroCota(numeroDeCotasNovo);
        //salvando tudo
        cotaRepository.save(novaCota);
        //salvando o grupo
        grupoConsorcioRepository.save(grupoConsorcio);

        blockchainGateway.registrarParticipante(grupoConsorcio.getEnderecoContrato(), usuario.getCarteiraWeb3());

        return new CotaResponseDTO(
                novaCota.getId(),
                novaCota.getNumeroCota(),
                novaCota.getUsuario().getNome(),
                novaCota.getGrupoConsorcio().getNome()
        );
    }

    @Transactional
    public CotaResponseDTO cancelarCota(Long cotaId){
        //primeiro busca a cota pelo id
        Cota cota = cotaRepository.findById(cotaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Cota não encontrada!"));
        //depois busca o grupo asociado aquela cota
        GrupoConsorcio grupoConsorcio = cota.getGrupoConsorcio();
        //valida o cancelamento
        grupoConsorcio.getState().validarCancelamento(grupoConsorcio, cota);
        //se passar salva no banco
        cota.setStatus(StatusCota.CANCELADA);
        cotaRepository.save(cota);

        return new CotaResponseDTO(
                cota.getId(),
                cota.getNumeroCota(),
                cota.getUsuario().getNome(),
                cota.getGrupoConsorcio().getNome()
        );

    }



}
