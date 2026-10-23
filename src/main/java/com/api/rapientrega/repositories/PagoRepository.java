package com.api.rapientrega.repositories;

import com.api.rapientrega.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    Optional<Pago> findByPedido_IdPedido(Integer idPedido);
}
