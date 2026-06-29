package com.consorcio.projeto_consorcio.assembleia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssembleiaRepository extends JpaRepository<Assembleia, Long> {
    boolean existsByGrupoIdAndCicloAndTipoContemplacao(Long grupoId, Integer ciclo, String tipoContemplacao);
    int countByGrupoId(Long grupoId);
    
    //contagem de assembleias únicas por ciclo (por exemplo, ciclos já fechados ou concluídos)
    int countByGrupoIdAndTipoContemplacao(Long grupoId, String tipoContemplacao);
}
