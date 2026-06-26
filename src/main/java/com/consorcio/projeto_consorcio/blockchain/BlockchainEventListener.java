package com.consorcio.projeto_consorcio.blockchain;

import com.consorcio.projeto_consorcio.blockchain.wrapper.ConsortiumGroup;
import com.consorcio.projeto_consorcio.cota.CotaService;
import com.consorcio.projeto_consorcio.lances.LanceService;
import com.consorcio.projeto_consorcio.pagamentos.PagamentoService;
import com.consorcio.projeto_consorcio.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

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


    @Value("${web3.contract-address}")
    private String contractAddress;



    @EventListener(ApplicationReadyEvent.class)
    public void iniciaEscutaDaBlokchain(){
        try {
            ConsortiumGroup contrato = ConsortiumGroup.load(contractAddress, web3j, credentials, gasProvider);

            //monitor de pagamaneto
            contrato.installmentPaidEventFlowable(
                    DefaultBlockParameterName.LATEST,
                    DefaultBlockParameterName.LATEST
            ).subscribe(evento -> {
                String carteiraCliente = evento.participant;
                //fazer dps
                //Long valorEmWei = evento.amount.longValue(); ou eu faço assim ou permito apenas pagamento do valor de parcela
                Long mesCiclo = evento.cycle.longValue();
                String hashTransacao = evento.log.getTransactionHash();

                System.out.println("Parcela paga!");
                System.out.println("Cliente: " + carteiraCliente + " | Mês: " + mesCiclo);

                //chamo o método do java
                pagamentoService.confirmarPagamentoPeloBlockchain(carteiraCliente, mesCiclo, hashTransacao);

            }, erro -> {
                System.err.println("Erro no stream de pagamentos: " + erro.getMessage());
            });

            //monitor de lances
            contrato.bidPlacedEventFlowable(
                    DefaultBlockParameterName.LATEST,
                    DefaultBlockParameterName.LATEST //olha a sempre o ultimo bloco minerado
            ).subscribe(evento -> {
                String carteira = evento.participant;
                BigInteger valorWei = evento.amount;
                Long ciclo = evento.cycle.longValue();
                Integer tipoLance = evento.bidType.intValue();
                String txhash = evento.log.getTransactionHash();


                lanceService.registraLancePelaBlockchain(carteira, valorWei, ciclo, tipoLance, txhash);
            }, erro -> {
                System.err.println("Erro no stream de lances: " + erro.getMessage());
            });

            System.out.println("Listernes ativos!");

        } catch (Exception e) {
            System.err.println("Falha crítica ao iniciar os listeners: " + e.getMessage());
        }
    }


}
