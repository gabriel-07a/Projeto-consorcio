package com.consorcio.projeto_consorcio.pagamentos;

import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.pagamentos.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cota_id", nullable = false)
    private Cota cota;

    @Column(name = "numero_parcela", nullable = false)
    private Integer numeroParcela;

    @Column(nullable = false)
    private BigDecimal valorParcela;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento statusDoPagamento = StatusPagamento.PENDENTE;

    //futuramente um hash de transação e data de pagamento que vai vim da blockchain
    @Column(name = "hash_transacao", unique = true)
    private String hashTransacao;

    // ⏱️ O momento exato em que o sistema registrou o pagamento via Blockchain
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

}
