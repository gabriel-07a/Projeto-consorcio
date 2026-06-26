package com.consorcio.projeto_consorcio.cota;

import com.consorcio.projeto_consorcio.cota.enums.StatusCota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CotaRepository extends JpaRepository<Cota, Long> {

    //traz todas as cotas que um usuário comprou
    Optional<Cota> findByUsuarioId(Long usuarioId);

    // traz todas as cotas ativas de um grupo
    List<Cota> findByGrupoConsorcioIdAndStatus(Long grupoId, StatusCota status);

    List<Cota> findByGrupoConsorcioId(Long grupoId);

    //conta quantas cotas existem em um grupo
    int countBygrupoConsorcioId(Long grupoId);
}