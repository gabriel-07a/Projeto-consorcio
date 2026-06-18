package com.consorcio.projeto_consorcio.assembleia;

import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.cota.Cota;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "assembleias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assembleia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private GrupoConsorcio grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cota_vencedora_id", nullable = false)
    private Cota cotaContemplada;


    @Column(name = "data_assembleia", nullable = false)
    private LocalDate dataAssembleia;
}