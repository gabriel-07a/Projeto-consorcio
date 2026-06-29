package com.consorcio.projeto_consorcio.blockchain;

import com.consorcio.projeto_consorcio.blockchain.wrapper.ConsortiumGroup;
import com.consorcio.projeto_consorcio.blockchain.wrapper.MockToken;
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

    @Value("${web3.stablecoin-address:0x0000000000000000000000000000000000000000}")
    private String stablecoinAddress;

    @Value("${web3.admin-address:}")
    private String adminAddressConfig;

    private ConsortiumGroup obterContrato(String enderecoContrato) {
        return ConsortiumGroup.load(enderecoContrato, web3j, credentials, gasProvider);
    }



    @Override
    public boolean registrarParticipante(String enderecoContrato, String carteiraClienteHex) {
        validarFormatoCarteira(carteiraClienteHex);
        preOperacaoCheck();
        try{
            ConsortiumGroup contrato = obterContrato(enderecoContrato);
            
            // Idempotência: verifica se o participante já está registrado na blockchain para evitar travamentos
            Boolean jaRegistrado = contrato.participants(carteiraClienteHex).send().component1();
            if (Boolean.TRUE.equals(jaRegistrado)) {
                return true;
            }

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

    @Override
    public BigInteger obterSaldoFundoComum(String enderecoContrato) {
        preOperacaoCheck();
        try {
            ConsortiumGroup contrato = obterContrato(enderecoContrato);
            return contrato.consortiumFundBalance().send();
        } catch (Exception e) {
            tratarEstrategiaDeFalha(e);
            return BigInteger.ZERO;
        }
    }

    @Override
    public BigInteger obterValorCartaCredito(String enderecoContrato) {
        preOperacaoCheck();
        try {
            ConsortiumGroup contrato = obterContrato(enderecoContrato);
            return contrato.creditValue().send();
        } catch (Exception e) {
            tratarEstrategiaDeFalha(e);
            return BigInteger.ZERO;
        }
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

    @Override
    public String deployGrupoConsorcio(BigDecimal valorCota, Integer duracaoMeses, Boolean aceitaLances) {
        preOperacaoCheck();
        try {
            String tokenAddress = stablecoinAddress;
            if (tokenAddress == null || tokenAddress.isBlank() || tokenAddress.equals("0x0000000000000000000000000000000000000000")) {
                System.out.println("Stablecoin não configurada ou zerada. Realizando deploy automático de um MockToken (Mock USDT)...");
                MockToken tokenMock = MockToken.deploy(web3j, credentials, gasProvider).send();
                tokenAddress = tokenMock.getContractAddress();
                System.out.println("MockToken (USDT) deployado com sucesso no endereço: " + tokenAddress);
                stablecoinAddress = tokenAddress;
            }

            BigInteger creditValueWei = valorCota.multiply(MULTIPLICADOR_WEI).toBigInteger();
            BigInteger installmentValueWei = valorCota.divide(BigDecimal.valueOf(duracaoMeses), 18, java.math.RoundingMode.HALF_UP)
                    .multiply(MULTIPLICADOR_WEI).toBigInteger();
            BigInteger totalMonths = BigInteger.valueOf(duracaoMeses);
            BigInteger maxContemplationsPerCycle = Boolean.TRUE.equals(aceitaLances) ? BigInteger.valueOf(2) : BigInteger.ONE;

            String adminAddress = (adminAddressConfig != null && !adminAddressConfig.isBlank())
                    ? adminAddressConfig
                    : credentials.getAddress();
            String backendAddress = credentials.getAddress();

            ConsortiumGroup contrato = ConsortiumGroup.deploy(
                    web3j,
                    credentials,
                    gasProvider,
                    adminAddress,
                    backendAddress,
                    tokenAddress,
                    creditValueWei,
                    installmentValueWei,
                    totalMonths,
                    maxContemplationsPerCycle
            ).send();

            return contrato.getContractAddress();
        } catch (Exception e) {
            System.err.println("Erro ao deployar contrato do consórcio: " + e.getMessage());
            throw new RegraDeNegocioException("Erro ao deployar contrato inteligente na blockchain: " + e.getMessage());
        }
    }
}
