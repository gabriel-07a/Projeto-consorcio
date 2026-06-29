package com.consorcio.projeto_consorcio.lances;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanceRepository extends JpaRepository<Lance, Long> {
    List<Lance> findByCotaGrupoConsorcioIdAndNumeroCiclo(Long grupoId, Integer numeroCiclo);
}
