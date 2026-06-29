package com.consorcio.projeto_consorcio.assembleia;

import com.consorcio.projeto_consorcio.assembleia.strategy.ContemplacaoStrategy;
import com.consorcio.projeto_consorcio.assembleia.strategy.SorteioStrategy;
import com.consorcio.projeto_consorcio.assembleia.strategy.LanceLivreStrategy;
import com.consorcio.projeto_consorcio.assembleia.strategy.LanceFixoStrategy;
import com.consorcio.projeto_consorcio.assembleia.strategy.LanceEmbutidoStrategy;
import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcioRepository;
import com.consorcio.projeto_consorcio.consorcio.enums.TipoLance;
import com.consorcio.projeto_consorcio.core.exception.EntidadeNaoEncontradaException;
import com.consorcio.projeto_consorcio.core.exception.RegraDeNegocioException;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.cota.CotaRepository;
import com.consorcio.projeto_consorcio.cota.enums.StatusCota;
import com.consorcio.projeto_consorcio.blockchain.BlockchainGateway;
import com.consorcio.projeto_consorcio.lances.Lance;
import com.consorcio.projeto_consorcio.lances.LanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Service
public class AssembleiaService {
    @Autowired
    private GrupoConsorcioRepository grupoConsorcioRepository;
    
    @Autowired
    private CotaRepository cotaRepository;
    
    @Autowired
    private AssembleiaRepository assembleiaRepository;
    
    @Autowired
    private BlockchainGateway blockchainGateway;
    
    @Autowired
    private LanceRepository lanceRepository;

    @Transactional
    public Cota realizarSorteio(Long grupoId){
        GrupoConsorcio grupoConsorcio = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Grupo não encontrado!"));

        grupoConsorcio.getState().validarSorteio();

        List<Cota> elegiveis = cotaRepository.findByGrupoConsorcioIdAndStatus(grupoId, StatusCota.ATIVA);
        if (elegiveis == null || elegiveis.isEmpty()) {
            throw new RegraDeNegocioException("Erro: Não há participantes elegíveis para o sorteio.");
        }

        int sorteiosRealizados = assembleiaRepository.countByGrupoIdAndTipoContemplacao(grupoId, "SORTEIO");
        int cicloAtual = sorteiosRealizados + 1;

        blockchainGateway.fecharMesAtual(grupoConsorcio.getEnderecoContrato(), cicloAtual);

        ContemplacaoStrategy estrategiaSorteio = new SorteioStrategy();
        Cota cotaVencedora = estrategiaSorteio.elegerVencedor(elegiveis);
        cotaVencedora.setStatus(StatusCota.CONTEMPLADA);
        cotaRepository.save(cotaVencedora);

        Assembleia registroDoSorteio = new Assembleia();
        registroDoSorteio.setGrupo(grupoConsorcio);
        registroDoSorteio.setCotaContemplada(cotaVencedora);
        registroDoSorteio.setDataAssembleia(LocalDate.now());
        registroDoSorteio.setCiclo(cicloAtual);
        registroDoSorteio.setTipoContemplacao("SORTEIO");
        assembleiaRepository.save(registroDoSorteio);

        blockchainGateway.contemplarVencedor(grupoConsorcio.getEnderecoContrato(), cotaVencedora.getUsuario().getCarteiraWeb3(), 0);

        // Verifica se devemos avançar o ciclo mensal automaticamente no Sorteio
        boolean avancarAutomatico = false;

        if (!grupoConsorcio.getAceitaLances()) {
            avancarAutomatico = true;
        } else {
            List<Lance> lancesDoCiclo = lanceRepository.findByCotaGrupoConsorcioIdAndNumeroCiclo(grupoId, cicloAtual);
            if (lancesDoCiclo.isEmpty()) {
                avancarAutomatico = true;
            } else {
                BigInteger consortiumFundBalance = blockchainGateway.obterSaldoFundoComum(grupoConsorcio.getEnderecoContrato());
                BigInteger creditValue = blockchainGateway.obterValorCartaCredito(grupoConsorcio.getEnderecoContrato());
                
                BigDecimal maiorLance = lancesDoCiclo.stream()
                        .map(Lance::getValorLance)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                BigInteger maiorLanceWei = maiorLance.multiply(new BigDecimal("1000000000000000000")).toBigInteger();
                
                if (consortiumFundBalance.add(maiorLanceWei).compareTo(creditValue) < 0) {
                    avancarAutomatico = true;
                }
            }
        }

        if (avancarAutomatico && cicloAtual < grupoConsorcio.getDuracaoMeses()) {
            blockchainGateway.abrirNovoMes(grupoConsorcio.getEnderecoContrato(), cicloAtual + 1);
            System.out.println("Ciclo do grupo " + grupoConsorcio.getNome() + " avançado automaticamente após sorteio.");
        }


        System.out.println("Parabéns! A cota número " + cotaVencedora.getNumeroCota() + " foi contemplada!");
        return cotaVencedora;
    }

    @Transactional
    public Cota realizarAssembleiaPorLance(Long grupoId, TipoLance tipoLance) {
        GrupoConsorcio grupoConsorcio = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Grupo não encontrado!"));

        if (!grupoConsorcio.getAceitaLances()) {
            throw new RegraDeNegocioException("Erro: Este grupo não aceita lances!");
        }

        if (grupoConsorcio.getTipoLanceAdicional() != tipoLance) {
            throw new RegraDeNegocioException("Erro: Este grupo aceita apenas lances do tipo " + grupoConsorcio.getTipoLanceAdicional());
        }

        int sorteiosRealizados = assembleiaRepository.countByGrupoIdAndTipoContemplacao(grupoId, "SORTEIO");
        int cicloAtual = sorteiosRealizados; // O sorteio do ciclo atual já deve ter ocorrido, logo o contador reflete o ciclo atual.

        // Validação 1: O sorteio deste ciclo já deve ter sido realizado
        boolean sorteioRealizado = assembleiaRepository.existsByGrupoIdAndCicloAndTipoContemplacao(grupoId, cicloAtual, "SORTEIO");
        if (!sorteioRealizado) {
            throw new RegraDeNegocioException("Erro: O sorteio obrigatório deste ciclo ainda não foi realizado!");
        }

        List<Cota> elegiveis = cotaRepository.findByGrupoConsorcioIdAndStatus(grupoId, StatusCota.ATIVA);
        List<Lance> lances = lanceRepository.findByCotaGrupoConsorcioIdAndNumeroCiclo(grupoId, cicloAtual);

        ContemplacaoStrategy estrategia;
        switch (tipoLance) {
            case LIVRE -> estrategia = new LanceLivreStrategy(lances);
            case FIXO -> estrategia = new LanceFixoStrategy(lances);
            case EMBUTIDO -> estrategia = new LanceEmbutidoStrategy(lances);
            default -> throw new RegraDeNegocioException("Erro: Tipo de lance inválido para contemplação.");
        }

        Cota cotaVencedora = estrategia.elegerVencedor(elegiveis);

        // Encontra o lance que foi eleito vencedor pela estratégia
        Lance lanceVencedor = lances.stream()
                .filter(Lance::isVencedor)
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Erro: Nenhum lance vencedor selecionado."));

        // Validação 2: Verificar se há saldo suficiente no caixa do contrato inteligente para pagar o prêmio
        BigInteger consortiumFundBalance = blockchainGateway.obterSaldoFundoComum(grupoConsorcio.getEnderecoContrato());
        BigInteger creditValue = blockchainGateway.obterValorCartaCredito(grupoConsorcio.getEnderecoContrato());
        BigInteger winningBidWei = lanceVencedor.getValorLance().multiply(new BigDecimal("1000000000000000000")).toBigInteger();

        BigInteger totalDisponivelNoCaixa = consortiumFundBalance.add(winningBidWei);
        if (totalDisponivelNoCaixa.compareTo(creditValue) < 0) {
            // Se o saldo for insuficiente, desmarca o lance como vencedor para permitir novas tentativas futuras
            lanceVencedor.setVencedor(false);
            throw new RegraDeNegocioException("Erro: Saldo de caixa insuficiente para pagar esta carta de crédito (Saldo + Lance: " 
                    + totalDisponivelNoCaixa + " Wei | Necessário: " + creditValue + " Wei)");
        }

        cotaVencedora.setStatus(StatusCota.CONTEMPLADA);
        cotaRepository.save(cotaVencedora);

        lanceRepository.saveAll(lances);

        Assembleia registroDoSorteio = new Assembleia();
        registroDoSorteio.setGrupo(grupoConsorcio);
        registroDoSorteio.setCotaContemplada(cotaVencedora);
        registroDoSorteio.setDataAssembleia(LocalDate.now());
        registroDoSorteio.setCiclo(cicloAtual);
        registroDoSorteio.setTipoContemplacao("LANCE");
        assembleiaRepository.save(registroDoSorteio);

        blockchainGateway.contemplarVencedor(grupoConsorcio.getEnderecoContrato(), cotaVencedora.getUsuario().getCarteiraWeb3(), 1);

        // Como a assembleia de lances é a última etapa do ciclo do mês, avançamos o ciclo na blockchain
        if (cicloAtual < grupoConsorcio.getDuracaoMeses()) {
            blockchainGateway.abrirNovoMes(grupoConsorcio.getEnderecoContrato(), cicloAtual + 1);
        }

        System.out.println("Parabéns! A cota número " + cotaVencedora.getNumeroCota() + " foi contemplada por lance " + tipoLance + "!");
        return cotaVencedora;
    }

    @Transactional
    public void avancarCicloManualmente(Long grupoId) {
        GrupoConsorcio grupoConsorcio = grupoConsorcioRepository.findById(grupoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Grupo não encontrado!"));

        int sorteiosRealizados = assembleiaRepository.countByGrupoIdAndTipoContemplacao(grupoId, "SORTEIO");
        int cicloAtual = sorteiosRealizados;

        boolean sorteioRealizado = assembleiaRepository.existsByGrupoIdAndCicloAndTipoContemplacao(grupoId, cicloAtual, "SORTEIO");
        if (!sorteioRealizado) {
            throw new RegraDeNegocioException("Erro: O sorteio obrigatório deste ciclo ainda não foi realizado!");
        }

        if (cicloAtual < grupoConsorcio.getDuracaoMeses()) {
            blockchainGateway.abrirNovoMes(grupoConsorcio.getEnderecoContrato(), cicloAtual + 1);
            System.out.println("Ciclo do grupo " + grupoConsorcio.getNome() + " avançado manualmente para o ciclo " + (cicloAtual + 1));
        } else {
            throw new RegraDeNegocioException("Erro: O consórcio já atingiu a duração máxima de meses!");
        }
    }
}
