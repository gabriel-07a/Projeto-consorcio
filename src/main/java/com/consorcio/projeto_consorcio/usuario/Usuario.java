package com.consorcio.projeto_consorcio.usuario;

import com.consorcio.projeto_consorcio.cota.Cota;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "tax_id", nullable = false, length = 30)
    private String taxId;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "carteira_web3", nullable = false, unique = true, length = 42)
    private String carteiraWeb3;

    @Column(name = "inserted_at", insertable = false, updatable = false)
    private LocalDateTime insertedAt;


    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    //esse mappedBy diz ao hibernate para não criar uma tabela nova e sim olhar no atributo usuario
    //da classe cota, e é ele que manda na relação e tem a chave estrangeira
    //server para o hibernate apagar todas as cotas se o grupo for excluido
    //o orphanremoval serve para que se vc tirar uma conta da lista do usuario, ele entende que a conta ficou
    //órfâ e apaga no banco de dados
    private List<Cota> cotas = new ArrayList<>(); //esse arraylist é uma boa prática para instanciar uma lsita vazia
}
