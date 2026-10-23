package com.api.rapientrega.repositories;

import com.api.rapientrega.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByRestaurante_IdRestaurante(Integer idRestaurante);
}
