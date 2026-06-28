package com.consorcio.projeto_consorcio.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

@Configuration
public class Web3jConfig {
    //pega as variaveis do application.properties
    @Value("${web3.rpc-url}")
    private String rpcUrl;

    @Value("${web3.backend-private-key}")
    private String privateKey;

    //serve para abrir uma conexão com a blochcain
    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(rpcUrl));
    }

    //usado para criar a credencial do meu backend
    //pq para chamar alguma função do smartcontract eu preciso de uma credential
    @Bean
    public Credentials credentials() {
        return Credentials.create(privateKey);
    }

    //esse defaultGasProvider é pra definir os limites de gasto de gas
    @Bean
    public DefaultGasProvider gasProvider() {
        return new DefaultGasProvider() {
            @Override
            public java.math.BigInteger getGasLimit() {
                return java.math.BigInteger.valueOf(3_000_000L);
            }
        };
    }
}