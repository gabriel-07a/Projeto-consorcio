package com.consorcio.projeto_consorcio.consorcio;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoConsorcioRepository extends JpaRepository<GrupoConsorcio, Long> { //esse long é referente ao tipo de id
    boolean existsByNome(String nome);
}
