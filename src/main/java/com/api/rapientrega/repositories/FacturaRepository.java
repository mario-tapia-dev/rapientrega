package com.api.rapientrega.repositories;

import com.api.rapientrega.models.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    Optional<Factura> findByPedido_IdPedido(Integer idPedido);
}
