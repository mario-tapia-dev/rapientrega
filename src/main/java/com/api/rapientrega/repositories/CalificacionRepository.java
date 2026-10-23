package com.api.rapientrega.repositories;

import com.api.rapientrega.models.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {

    boolean existsByPedido_IdPedido(Integer idPedido);

    Optional<Calificacion> findByPedido_IdPedido(Integer idPedido);
}
