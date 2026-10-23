package com.api.rapientrega.repositories;

import com.api.rapientrega.models.Repartidor;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, Integer> {

    // Bloquea los repartidores libres para que dos pedidos no tomen al mismo
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Repartidor r WHERE r.disponible = true")
    List<Repartidor> findDisponiblesForUpdate();
}
