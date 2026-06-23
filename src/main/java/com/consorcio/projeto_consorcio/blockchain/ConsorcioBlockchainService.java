package com.consorcio.projeto_consorcio.blockchain;

import com.consorcio.projeto_consorcio.blockchain.wrapper.ConsortiumGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

@Service
public class ConsorcioBlockchainService {

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

}
