package com.consorcio.projeto_consorcio.blockchain;

import com.consorcio.projeto_consorcio.blockchain.wrapper.ConsortiumGroup;
import com.consorcio.projeto_consorcio.core.exception.AdminGasExhaustionException;
import com.consorcio.projeto_consorcio.core.exception.BlockchainConnectivityException;
import com.consorcio.projeto_consorcio.core.exception.InvalidCryptoAddressException;
import com.consorcio.projeto_consorcio.core.exception.RegraDeNegocioException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.ClientConnectionException;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Component
public class Web3jAdapter implements BlockchainGateway{

    @Autowired
    private Web3j web3j;

    @Autowired
    private Credentials credentials;

    @Autowired
    private DefaultGasProvider gasProvider;

    private static final BigDecimal MULTIPLICADOR_WEI = new BigDecimal("1000000000000000000");

    private ConsortiumGroup obterContrato(String enderecoContrato) {
        return ConsortiumGroup.load(enderecoContrato, web3j, credentials, gasProvider);
    }



    @Override
    public boolean registrarParticipante(String enderecoContrato, String carteiraClienteHex) {
        validarFormatoCarteira(carteiraClienteHex);
        preOperacaoCheck();
        try{
            ConsortiumGroup contrato = obterContrato(enderecoContrato);
            TransactionReceipt receipt = contrato.registerParticipant(carteiraClienteHex).send();
            validarReciboTransacao(receipt);
            return true;
        }catch (Exception e){
            tratarEstrategiaDeFalha(e);
            return false;
        }
    }

    @Override
    public boolean abrirNovoMes(String enderecoContrato, Integer numeroCiclo) {
        BigInteger ciclo = BigInteger.valueOf(numeroCiclo);
        preOperacaoCheck();
        try{
            ConsortiumGroup contrato = obterContrato(enderecoContrato);
            TransactionReceipt receipt = contrato.openCycle(ciclo).send();
            validarReciboTransacao(receipt);
            return true;
        } catch (Exception e) {
            tratarEstrategiaDeFalha(e);
            return false;
        }
    }

    @Override
    public boolean fecharMesAtual(String enderecoContrato, Integer numeroCiclo) {
        preOperacaoCheck();
        try{
            ConsortiumGroup contrato = obterContrato(enderecoContrato);
            BigInteger ciclo = BigInteger.valueOf(numeroCiclo);
            TransactionReceipt receipt = contrato.closeCycle(ciclo).send();
            validarReciboTransacao(receipt);
            return true;
        } catch (Exception e) {
            tratarEstrategiaDeFalha(e);
            return false;
        }
    }

    @Override
    public boolean aplicarInadimplencia(String enderecoContrato, String carteiraClienteHex, BigDecimal valorMulta) {
        validarFormatoCarteira(carteiraClienteHex);
        preOperacaoCheck();
        try {
            ConsortiumGroup contrato = obterContrato(enderecoContrato);
            BigInteger penaltyAmountWei = valorMulta.multiply(MULTIPLICADOR_WEI).toBigInteger();
            TransactionReceipt receipt = contrato.markDelinquent(carteiraClienteHex, penaltyAmountWei).send();
            validarReciboTransacao(receipt);
            return true;
        } catch (Exception e) {
            tratarEstrategiaDeFalha(e);
            return false;
        }
    }

    @Override
    public boolean regularizarParticipante(String enderecoContrato, String carteiraClienteHex) {
        validarFormatoCarteira(carteiraClienteHex);
        preOperacaoCheck();
        try {
            ConsortiumGroup contrato = obterContrato(enderecoContrato);
            TransactionReceipt receipt = contrato.regularizeParticipant(carteiraClienteHex).send();
            validarReciboTransacao(receipt);
            return true;
        } catch (Exception e) {
            tratarEstrategiaDeFalha(e);
            return false;
        }
    }

    @Override
    public boolean contemplarVencedor(String enderecoContrato, String carteiraVencedorHex, Integer tipoContemplacao) {
        validarFormatoCarteira(carteiraVencedorHex);
        preOperacaoCheck();
        try {
            ConsortiumGroup contrato = obterContrato(enderecoContrato);
            BigInteger cType = BigInteger.valueOf(tipoContemplacao);
            TransactionReceipt receipt = contrato.contemplateWinner(carteiraVencedorHex, cType).send();
            validarReciboTransacao(receipt);
            return true;
        } catch (Exception e) {
            tratarEstrategiaDeFalha(e);
            return false;
        }
    }

    private void validarFormatoCarteira(String carteira) {
        if (carteira == null || !carteira.matches("^0x[a-fA-F0-9]{40}$")) {
            throw new InvalidCryptoAddressException("O endereço enviado não segue o padrão padrão hexadecimal da rede Ethereum (0x...): " + carteira);
        }
    }

    private void validarReciboTransacao(TransactionReceipt receipt) {
        if (receipt == null || !receipt.isStatusOK()) {
            String revertReason = (receipt != null) ? receipt.getRevertReason() : null;
            if (revertReason != null && !revertReason.isEmpty()) {
                throw new RegraDeNegocioException("Operação negada pelas validações do contrato inteligente: " + revertReason);
            }
            throw new RegraDeNegocioException("A transação foi enviada, mas a Máquina Virtual Ethereum rejeitou a execução do contrato.");
        }
    }

    private void tratarEstrategiaDeFalha(Exception e) {
        if (e instanceof TransactionException) {
            TransactionException te = (TransactionException) e;
            if (te.getTransactionReceipt().isPresent()) {
                validarReciboTransacao(te.getTransactionReceipt().get());
            }
        }

        Throwable causa = e.getCause();

        //erros de conexão
        if (e instanceof ClientConnectionException ||
                e instanceof ConnectException ||
                e instanceof SocketTimeoutException ||
                e instanceof IOException ||
                causa instanceof ConnectException ||
                causa instanceof SocketTimeoutException ||
                causa instanceof IOException) {

            throw new BlockchainConnectivityException(
                    "Não foi possível estabelecer comunicação com o nó da Blockchain. O servidor RPC pode estar offline."
            );
        }

        //regra de negocio do smart contract
        throw new RegraDeNegocioException("Operação negada pelas validações do contrato inteligente: " + e.getMessage());
    }

    private void preOperacaoCheck() {
        try {
            BigInteger balance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST)
                    .send()
                    .getBalance();
            BigInteger gasPrice = gasProvider.getGasPrice();
            BigInteger gasLimit = gasProvider.getGasLimit();
            BigInteger requiredAmount = gasPrice.multiply(gasLimit);

            if (balance.compareTo(requiredAmount) < 0) {
                throw new AdminGasExhaustionException("Erro: A carteira administradora não possui fundos suficientes para pagar as taxas de processamento da rede.");
            }
        } catch (IOException e) {
            throw new BlockchainConnectivityException("Não foi possível estabelecer comunicação com o nó da Blockchain para verificação de saldo.");
        }
    }
}
