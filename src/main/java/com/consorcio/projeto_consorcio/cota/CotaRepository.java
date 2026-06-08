package com.consorcio.projeto_consorcio.cota;

import com.consorcio.projeto_consorcio.cota.enums.StatusCota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotaRepository extends JpaRepository<Cota, Long> {

    //traz todas as cotas que um usuário comprou
    List<Cota> findByUsuarioId(Long usuarioId);

    // traz todas as cotas ativas de um grupo
    List<Cota> findByGrupoIdAndStatus(Long grupoId, StatusCota status);

    //conta quantas cotas existem em um grupo
    long countByGrupoId(Long grupoId);
}