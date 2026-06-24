package com.consorcio.projeto_consorcio.pagamentos;

import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcioRepository;
import com.consorcio.projeto_consorcio.core.exception.EntidadeNaoEncontradaException;
import com.consorcio.projeto_consorcio.core.exception.RegraDeNegocioException;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.cota.CotaRepository;
import com.consorcio.projeto_consorcio.pagamentos.dto.PagamentoResponseDTO;
import com.consorcio.projeto_consorcio.pagamentos.enums.StatusPagamento;
import com.consorcio.projeto_consorcio.usuario.Usuario;
import com.consorcio.projeto_consorcio.usuario.UsuarioRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PagamentoService {
    private final GrupoConsorcioRepository grupoConsorcioRepository;
    private final CotaRepository cotaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public PagamentoService(CotaRepository cotaRepository, GrupoConsorcioRepository grupoConsorcioRepository, PagamentoRepository pagamentoRepository, UsuarioRepository usuarioRepository){
        this.cotaRepository = cotaRepository;
        this.grupoConsorcioRepository = grupoConsorcioRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void criarParcelas(GrupoConsorcio grupo){
        List<Cota> cotasDoGrupo = cotaRepository.findByGrupoConsorcioId(grupo.getId());

        //se for decidido ter taxas mais pra frente a gente coloca:
/*        BigDecimal taxaCalculada = grupo.getValorCartaCredito()
                .multiply(grupo.getTaxaAdministracao())
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);*/

        BigDecimal valorTotal = grupo.getValorCota();
        BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(grupo.getDuracaoMeses()), 2, RoundingMode.HALF_UP); //esse dois é para o java cortar o número dps de duas casas decimais

        List<Pagamento> todasAsParcelas = new ArrayList<>();
        //colocando o vencimento para o dia 5 do proximo mes depois do inicio
        LocalDate dataPrimeiroVencimento = LocalDate.now().plusMonths(1).withDayOfMonth(5);

        for(Cota cota : cotasDoGrupo){
            for(int numeroDaParcela = 1; numeroDaParcela <= grupo.getDuracaoMeses(); numeroDaParcela++){
                Pagamento pagamento = new Pagamento();
                pagamento.setCota(cota);
                pagamento.setNumeroParcela(numeroDaParcela);
                pagamento.setValorParcela(valorParcela);
                pagamento.setDataVencimento(dataPrimeiroVencimento.plusMonths(numeroDaParcela - 1));
                pagamento.setStatusDoPagamento(StatusPagamento.PENDENTE);

                todasAsParcelas.add(pagamento);
            }
        }

        pagamentoRepository.saveAll(todasAsParcelas);

    }

    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> buscarExtrato(Long cotaId /*,Long idUsuarioLogado*/){
        Cota cota = cotaRepository.findById(cotaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cota não encontrada!"));

        //if(!cota.getUsuario().getId().equals(idUsuarioLogado)) throw new AcessoNegadoException("Você não tem permissão para ver esse extrato!");
        //futuramente com o spring security
        List<Pagamento> parcelas = pagamentoRepository.buscarExtratoPorCotaId(cotaId);

        return parcelas.stream()
                .map(p -> new PagamentoResponseDTO(
                        p.getId(),
                        p.getNumeroParcela(),
                        p.getValorParcela(),
                        p.getDataVencimento(),
                        p.getStatusDoPagamento(),
                        p.getHashTransacao()
                )).toList();
    }

    @Transactional
    public String registrarPagamentoWeb3(Long pagamentoId, String hashTransacao){
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: pagamento não encontrado!"));

        if(pagamento.getStatusDoPagamento() == StatusPagamento.PAGO) return "Pagamento já foi processado anteriormente!";

        if(pagamento.getStatusDoPagamento() == StatusPagamento.CANCELADO) throw new RegraDeNegocioException("Erro: não é possível pagar uma parcela cancelada!");
        if(hashTransacao == null || hashTransacao.isBlank()) throw new RegraDeNegocioException("Erro: transação negada!");

        pagamento.setStatusDoPagamento(StatusPagamento.PAGO);
        pagamento.setHashTransacao(hashTransacao);
        pagamento.setDataPagamento(LocalDateTime.now());
        return "Sucesso! Parcela " + pagamento.getNumeroParcela() + " paga e registrada na Blockchain!";
    }

    @Transactional
    public void confirmarPagamentoPeloBlockchain(String carteiraCliente, Long mesCiclo, String hashTransacao){
        Usuario usuario = usuarioRepository.findByCarteiraWeb3IgnoreCase(carteiraCliente)
                .orElseThrow(() -> new RegraDeNegocioException("Erro: Evento de pagamento identificado de uma carteira não cadastrada: " + carteiraCliente));

        Pagamento pagamento = pagamentoRepository.buscarParcelaPendente(usuario.getId(), mesCiclo.intValue())
                .orElseThrow(() -> new RegraDeNegocioException("Erro: Parcela não encontrada para esse usuário!"));

        pagamento.setStatusDoPagamento(StatusPagamento.PAGO);
        pagamento.setHashTransacao(hashTransacao);
        pagamento.setDataPagamento(LocalDateTime.now());

        pagamentoRepository.save(pagamento);
    }




    // Esta expressao significa que vai ser ativado todo dia as 1 da manha
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void fiscalizarParcelasAtrasadas() {
        LocalDate hoje = LocalDate.now();

        int quantidadeAtualizada = pagamentoRepository.atualizarStatusParcelasVencidas(hoje);

        // devolvo uma mensagem de execução ("Rotina de fiscalização concluída: {} parcelas marcadas como ATRASADAS no dia {}.", quantidadeAtualizada, hoje);
    }
}
