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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CotaService {
    @Autowired
    private PagamentoService pagamentoService;
    
    @Autowired
    private BlockchainGateway blockchainGateway;
    
    @Autowired
    private CotaRepository cotaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private GrupoConsorcioRepository grupoConsorcioRepository;

    @Transactional
    public CotaResponseDTO comprarCota(Long usuarioId, Long grupoId){
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Usuário não cadastrado no sistema!"));
        GrupoConsorcio grupoConsorcio = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Grupo de Consórcio não encontrado no sistema!"));

        grupoConsorcio.getState().validarNovoParticipante(grupoConsorcio, usuario);
        if(grupoConsorcio.getStatus() == StatusGrupo.EM_ANDAMENTO) pagamentoService.criarParcelas(grupoConsorcio);

        int totalCotas = cotaRepository.countBygrupoConsorcioId(grupoConsorcio.getId());
        int numeroDeCotasNovo = totalCotas + 1;

        Cota novaCota = new Cota();
        novaCota.setUsuario(usuario);
        novaCota.setGrupoConsorcio(grupoConsorcio);
        novaCota.setNumeroCota(numeroDeCotasNovo);
        cotaRepository.save(novaCota);
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
        Cota cota = cotaRepository.findById(cotaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Cota não encontrada!"));
        GrupoConsorcio grupoConsorcio = cota.getGrupoConsorcio();
        grupoConsorcio.getState().validarCancelamento(grupoConsorcio, cota);
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
