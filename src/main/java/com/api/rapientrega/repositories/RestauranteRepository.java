package com.api.rapientrega.repositories;

import com.api.rapientrega.models.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {

    // Filtro simple: texto contenido en el nombre y/o en el tipo de cocina (sin importar mayúsculas)
    List<Restaurante> findByNombreContainingIgnoreCaseAndTipoCocinaContainingIgnoreCase(String nombre, String tipoCocina);
}
