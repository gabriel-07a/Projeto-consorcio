package com.consorcio.projeto_consorcio.consorcio;

import com.consorcio.projeto_consorcio.consorcio.enums.StatusGrupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoConsorcioRepository extends JpaRepository<GrupoConsorcio, Long> { //esse long é referente ao tipo de id
    boolean existsByNome(String nome);
    List<GrupoConsorcio> findByStatus(StatusGrupo status);
}
