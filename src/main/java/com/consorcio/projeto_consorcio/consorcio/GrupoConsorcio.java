package com.consorcio.projeto_consorcio.consorcio;

import com.consorcio.projeto_consorcio.consorcio.state.*;
import com.consorcio.projeto_consorcio.cota.Cota;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.consorcio.projeto_consorcio.consorcio.state.StatusGrupo.*;

@Entity
@Table(name = "grupo_consorcio") //nome da tabela
@Getter
@Setter
public class GrupoConsorcio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "valor_cota", nullable = false)
    private BigDecimal valorCota;

    @Column(name = "vagas_maximas", nullable = false)
    private Integer vagasMaximas;

    @Column(name = "endereco_contrato", length = 42) // length 42 para Web3
    private String enderecoContrato;

    // PADRÃO STATE
    @Enumerated(EnumType.STRING)
    @Column(name = "status_grupo", nullable = false)
    private StatusGrupo status = StatusGrupo.EM_FORMACAO;

    // PADRÃO STRATEGY
    @Column(name = "aceita_lances", nullable = false)
    private Boolean aceitaLances;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_lance_adicional")
    private TipoLance tipoLanceAdicional;

    @Column(name = "inserted_at", insertable = false, updatable = false)
    private LocalDateTime insertedAt; // O banco preenche isso sozinho graças ao DEFAULT CURRENT_TIMESTAMP

    @OneToMany(mappedBy = "grupoConsorcio", cascade = CascadeType.ALL, orphanRemoval = true)
    //esse mappedBy diz ao hibernate para não criar uma tabela nova e sim olhar no atributo usuario
    //da classe cota, e é ele que manda na relação e tem a chave estrangeira
    //server para o hibernate apagar todas as cotas se o grupo for excluido
    //o orphanremoval serve para que se vc tirar uma conta da lista do usuario, ele entende que a conta ficou
    //órfâ e apaga no banco de dados
    private List<Cota> cotas = new ArrayList<>(); //esse arraylist é uma boa prática para instanciar uma lsita vazia


    //o cota service vai usar isso para rodar as regras!
    public GrupoState getState() {
        return switch (this.status) {
            case EM_FORMACAO -> new GrupoAbertoState();
            case EM_ANDAMENTO -> new GrupoEmAndamentoState();
            case ENCERRADO -> new GrupoFinalizadoState();
        };
    }
}