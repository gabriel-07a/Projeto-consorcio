package com.consorcio.projeto_consorcio.lances;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanceRepository extends JpaRepository<Lance, Long> {

}
