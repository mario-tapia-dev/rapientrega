package com.api.rapientrega.repositories;

import com.api.rapientrega.models.Pedido;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Bloquea el pedido durante la transacción (evita que "aceptar" y el timeout choquen)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pedido p WHERE p.idPedido = :id")
    Optional<Pedido> findByIdForUpdate(@Param("id") Integer id);

    List<Pedido> findByEstado(Pedido.Estado estado);

    List<Pedido> findByCliente_IdCliente(Integer idCliente);

    // Solo ids: así cada pedido se vuelve a leer ya bloqueado y con su estado real
    @Query("SELECT p.idPedido FROM Pedido p WHERE p.estado = :estado AND p.fechaCreacion < :limite")
    List<Integer> findIdsByEstadoAndFechaCreacionBefore(@Param("estado") Pedido.Estado estado,
                                                        @Param("limite") LocalDateTime limite);

    @Query("SELECT p.idPedido FROM Pedido p WHERE p.estado = :estado AND p.repartidor IS NULL ORDER BY p.fechaCreacion ASC")
    List<Integer> findIdsSinRepartidor(@Param("estado") Pedido.Estado estado);
}
