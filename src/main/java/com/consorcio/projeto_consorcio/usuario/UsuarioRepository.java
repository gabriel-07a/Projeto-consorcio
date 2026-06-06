package com.consorcio.projeto_consorcio.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // O JpaRepository já traz os métodos save(), findById(), delete() prontos por padrão!
    boolean existsByEmail(String email);
    boolean existsByTaxId(String taxId);
    boolean existsByCarteiraWeb3(String carteiraWeb3);

}