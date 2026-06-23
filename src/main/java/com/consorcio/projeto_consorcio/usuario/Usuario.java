package com.consorcio.projeto_consorcio.usuario;

import com.consorcio.projeto_consorcio.cota.Cota;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@SQLDelete(sql = "UPDATE usuarios SET ativo = false WHERE id = ?")
@SQLRestriction("ativo = true")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "tax_id", nullable = false, length = 30)
    private String taxId;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "carteira_web3", nullable = false, length = 42)
    private String carteiraWeb3;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "inserted_at", insertable = false, updatable = false)
    private LocalDateTime insertedAt;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cota> cotas = new ArrayList<>();
}

