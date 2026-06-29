package com.consorcio.projeto_consorcio.blockchain;

import com.consorcio.projeto_consorcio.blockchain.wrapper.ConsortiumGroup;
import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcioRepository;
import com.consorcio.projeto_consorcio.lances.LanceService;
import com.consorcio.projeto_consorcio.pagamentos.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BlockchainEventListener {
    @Autowired
    private Web3j web3j;

    @Autowired
    private Credentials credentials;

    @Autowired
    private DefaultGasProvider gasProvider;

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private LanceService lanceService;

    @Autowired
    private GrupoConsorcioRepository grupoConsorcioRepository;

    private final Set<String> enderecosEscutados = ConcurrentHashMap.newKeySet();

    @EventListener(ApplicationReadyEvent.class)
    public void iniciaEscutaDaBlokchain(){
        try {
            System.out.println("Iniciando escutas dinâmicas de contratos de consórcios...");
            
            // Busca todos os grupos existentes no banco de dados e registra escuta
            grupoConsorcioRepository.findAll().forEach(grupo -> {
                if (grupo.getEnderecoContrato() != null && !grupo.getEnderecoContrato().isBlank()) {
                    registrarNovoContratoEscuta(grupo.getEnderecoContrato());
                }
            });
            
            System.out.println("Escutas iniciais configuradas com sucesso!");
        } catch (Exception e) {
            System.err.println("Falha crítica ao iniciar escutas dinâmicas: " + e.getMessage());
        }
    }

    public void registrarNovoContratoEscuta(String contractAddress) {
        if (contractAddress == null || contractAddress.isBlank()) {
            return;
        }

        String enderecoNormalizado = contractAddress.trim().toLowerCase();
        if (enderecosEscutados.contains(enderecoNormalizado)) {
            return; // Já está escutando este contrato
        }

        try {
            ConsortiumGroup contrato = ConsortiumGroup.load(contractAddress, web3j, credentials, gasProvider);

            // monitor de pagamento
            contrato.installmentPaidEventFlowable(
                    DefaultBlockParameterName.LATEST,
                    null
            ).subscribe(evento -> {
                try {
                    String carteiraCliente = evento.participant;
                    Long mesCiclo = evento.cycle.longValue();
                    String hashTransacao = evento.log.getTransactionHash();

                    System.out.println("[" + contractAddress + "] Parcela paga capturada!");
                    System.out.println("Cliente: " + carteiraCliente + " | Mês: " + mesCiclo);

                    pagamentoService.confirmarPagamentoPeloBlockchain(contractAddress, carteiraCliente, mesCiclo, hashTransacao);
                } catch (Throwable t) {
                    System.err.println("[" + contractAddress + "] Erro crítico ao processar evento de pagamento: " + t.getMessage());
                    t.printStackTrace();
                }
            }, erro -> {
                System.err.println("[" + contractAddress + "] Erro no monitor de pagamentos (inscrição cancelada): " + erro.getMessage());
            });

            // monitor de lances
            contrato.bidPlacedEventFlowable(
                    DefaultBlockParameterName.LATEST,
                    null
            ).subscribe(evento -> {
                try {
                    String carteira = evento.participant;
                    BigInteger valorWei = evento.amount;
                    Long ciclo = evento.cycle.longValue();
                    Integer tipoLance = evento.bidType.intValue();
                    String txhash = evento.log.getTransactionHash();

                    System.out.println("[" + contractAddress + "] Lance capturado!");
                    lanceService.registraLancePelaBlockchain(contractAddress, carteira, valorWei, ciclo, tipoLance, txhash);
                } catch (Throwable t) {
                    System.err.println("[" + contractAddress + "] Erro crítico ao processar evento de lance: " + t.getMessage());
                    t.printStackTrace();
                }
            }, erro -> {
                System.err.println("[" + contractAddress + "] Erro no monitor de lances (inscrição cancelada): " + erro.getMessage());
            });

            enderecosEscutados.add(enderecoNormalizado);
            System.out.println("Iniciada escuta ativa de eventos no contrato: " + contractAddress);

        } catch (Exception e) {
            System.err.println("Erro ao assinar escuta no contrato " + contractAddress + ": " + e.getMessage());
        }
    }
}
