package com.consorcio.projeto_consorcio.pagamentos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    // esse join fetch é como se fosse um eager e ele já traz a cota junto do registro de pagamento
    @Query("SELECT p FROM Pagamento p JOIN FETCH p.cota WHERE p.cota.id = :cotaId ORDER BY p.dataVencimento ASC")
    List<Pagamento> buscarExtratoPorCotaId(Long cotaId);
}
