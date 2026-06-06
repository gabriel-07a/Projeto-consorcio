package com.consorcio.projeto_consorcio.cota;

import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_grupo")
@Getter
@Setter
public class Cota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "numero_cota", nullable = false)
    private Integer numeroCota;

    @ManyToOne(fetch = FetchType.LAZY)
    //isso diz que muitas cotas pertencem a um único usuário.
    //o lazy faz o hibernate trazer apenas o dados da cota
    //se não fosse lazy ele faria um join e traria todos os dados de usuario tbm
    //isso ajuda em performance
    @JoinColumn(name = "usuario_id", nullable = false)
    //isso diz ao hibernate que essa é a coluna que vou usar no banco
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_consorcio_id")
    private GrupoConsorcio grupoConsorcio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_cota", nullable = false)
    private StatusCota status = StatusCota.ATIVA;
    //esse valor é ativa porque esse vai ser um valor padrão
    //qualquer valor que eu der em qualquer atributo aqui vai funcinar como
    //um default value apenas

    @CreationTimestamp
    @Column(name = "adesao_em", nullable = false, updatable = false)
    private LocalDateTime adesaoEm;

}
