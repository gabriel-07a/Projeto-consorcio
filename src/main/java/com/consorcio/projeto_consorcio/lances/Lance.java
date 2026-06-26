package com.consorcio.projeto_consorcio.lances;


import com.consorcio.projeto_consorcio.cota.Cota;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cota_id", nullable = false)
    private Cota cota;

    @Column(name = "numero_ciclo", nullable = false)
    private Integer numeroCiclo; //mes atual do lance

    @Column(nullable = false)
    private BigDecimal valorLance;

    @Column(name = "tipo_lance", nullable = false)
    private String tipoLance; // livre, fixo, embutido

    @Column(nullable = false)
    private boolean vencedor = false;

    @Column(name = "hash_transacao", unique = true)
    private String hashTransacao;

    @Column(name = "data_registro")
    private LocalDateTime dataRegistro;
}
