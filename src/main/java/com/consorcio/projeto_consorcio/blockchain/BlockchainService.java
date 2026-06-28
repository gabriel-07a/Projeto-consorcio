package com.consorcio.projeto_consorcio.blockchain;

import com.consorcio.projeto_consorcio.blockchain.wrapper.ConsortiumGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

@Service
public class BlockchainService {

    @Autowired
    private Web3j web3j;

    @Autowired
    private Credentials credentials;

    @Autowired
    private DefaultGasProvider gasProvider;

    @Value("${web3.contract-address}")
    private String contractAddress;


    public boolean registrarParticipanteNaRede(String carteiraClienteHex) {
        try {
            //pega a classe consortiumgroup
            ConsortiumGroup contrato = ConsortiumGroup.load(contractAddress, web3j, credentials, gasProvider);

            //faz a transação e espera o retorno
            TransactionReceipt recibo = contrato.registerParticipant(carteiraClienteHex).send();

            //retorna se deu certo
            return recibo.isStatusOK();
        } catch (Exception e) {
            System.err.println("Erro ao registrar participante na Blockchain: " + e.getMessage());
            return false;
        }
    }

    public boolean abrirNovoMesNaRede(int novoMes){
        try{
            ConsortiumGroup contrato = ConsortiumGroup.load(contractAddress, web3j, credentials, gasProvider);
            TransactionReceipt recibo = contrato.openCycle(BigInteger.valueOf(novoMes)).send();
            //retorna se deu certo
            return recibo.isStatusOK();
        }catch (Exception e){
            System.err.println("Erro ao abrir novo mes na Blockchain: " + e.getMessage());
            return false;
        }
    }

    public boolean fecharMesAtualNaRede(int mesAtual) {
        try {
            ConsortiumGroup contrato = ConsortiumGroup.load(contractAddress, web3j, credentials, gasProvider);

            TransactionReceipt recibo = contrato.closeCycle(BigInteger.valueOf(mesAtual)).send();
            return recibo.isStatusOK();
        } catch (Exception e) {
            System.err.println("Erro ao fechar ciclo na Blockchain: " + e.getMessage());
            return false;
        }
    }

    public boolean contemplarVencedorNaRede(String carteiraVencedorHex, boolean porSorteio) {
        try {
            ConsortiumGroup contrato = ConsortiumGroup.load(contractAddress, web3j, credentials, gasProvider);

            //no contrato o enum ContemplationType é ou DRAW = 0 ou BID = 1
            BigInteger tipoContemplacao = porSorteio ? BigInteger.ZERO : BigInteger.ONE;

            TransactionReceipt recibo = contrato.contemplateWinner(carteiraVencedorHex, tipoContemplacao).send();
            return recibo.isStatusOK();
        } catch (Exception e) {
            System.err.println("Erro ao contemplar vencedor na Blockchain: " + e.getMessage());
            return false;
        }
    }

    public boolean aplicarInadimplenciaNaRede(String carteiraClienteHex, double valorMulta) {
        try {
            ConsortiumGroup contrato = ConsortiumGroup.load(contractAddress, web3j, credentials, gasProvider);
            //o valor da multa tem que ser convertido para wei
            BigInteger multaEmWei = BigInteger.valueOf((long) (valorMulta * 100)).multiply(BigInteger.valueOf(10).pow(16));

            TransactionReceipt recibo = contrato.markDelinquent(carteiraClienteHex, multaEmWei).send();
            return recibo.isStatusOK();
        } catch (Exception e) {
            System.err.println("Erro ao marcar inadimplência na Blockchain: " + e.getMessage());
            return false;
        }
    }

    public boolean regularizarParticipanteNaRede(String carteiraClienteHex) {
        try {
            ConsortiumGroup contrato = ConsortiumGroup.load(contractAddress, web3j, credentials, gasProvider);

            TransactionReceipt recibo = contrato.regularizeParticipant(carteiraClienteHex).send();
            return recibo.isStatusOK();
        } catch (Exception e) {
            System.err.println("Erro ao regularizar participante na Blockchain: " + e.getMessage());
            return false;
        }
    }

}
