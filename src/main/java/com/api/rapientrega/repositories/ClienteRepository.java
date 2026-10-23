package com.api.rapientrega.repositories;

import com.api.rapientrega.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdClienteNot(String email, Integer idCliente);
}
