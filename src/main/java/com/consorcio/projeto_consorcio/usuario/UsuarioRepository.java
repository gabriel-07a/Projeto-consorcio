package com.consorcio.projeto_consorcio.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // O JpaRepository já traz os métodos save(), findById(), delete() prontos por padrão!
    boolean existsByEmail(String email);
    boolean existsByTaxId(String taxId);
    boolean existsByCarteiraWeb3(String carteiraWeb3);
    Optional<Usuario> findByCarteiraWeb3IgnoreCase(String carteiraWeb3); //esse optional pertite que o meu service retorne um erro caso ele não ache nada

    List<Usuario> getUsuariosById(Long id);
}