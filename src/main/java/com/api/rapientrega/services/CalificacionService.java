package com.api.rapientrega.services;

import com.api.rapientrega.models.Calificacion;
import com.api.rapientrega.models.Pedido;
import com.api.rapientrega.repositories.CalificacionRepository;
import com.api.rapientrega.repositories.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CalificacionService {

    private static final Logger log = LoggerFactory.getLogger(CalificacionService.class);

    private final CalificacionRepository calificacionRepository;
    private final PedidoRepository pedidoRepository;

    public CalificacionService(CalificacionRepository calificacionRepository, PedidoRepository pedidoRepository) {
        this.calificacionRepository = calificacionRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public Calificacion calificar(Integer idPedido, Calificacion datos) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido no encontrado: " + idPedido));

        if (pedido.getEstado() != Pedido.Estado.ENTREGADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se puede calificar un pedido entregado (estado actual: " + pedido.getEstado() + ")");
        }
        if (calificacionRepository.existsByPedido_IdPedido(idPedido)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este pedido ya fue calificado");
        }
        validarPuntuacion(datos.getPuntuacionRestaurante(), "puntuacionRestaurante");
        validarPuntuacion(datos.getPuntuacionRepartidor(), "puntuacionRepartidor");

        Calificacion calificacion = new Calificacion();
        calificacion.setPedido(pedido);
        calificacion.setPuntuacionRestaurante(datos.getPuntuacionRestaurante());
        calificacion.setComentarioRestaurante(datos.getComentarioRestaurante());
        calificacion.setPuntuacionRepartidor(datos.getPuntuacionRepartidor());
        calificacion.setComentarioRepartidor(datos.getComentarioRepartidor());
        Calificacion guardada = calificacionRepository.save(calificacion);

        log.info("[CALIFICACION] Pedido {}: restaurante {}/5, repartidor {}/5", idPedido,
                guardada.getPuntuacionRestaurante(), guardada.getPuntuacionRepartidor());
        return guardada;
    }

    public Calificacion obtenerPorPedido(Integer idPedido) {
        return calificacionRepository.findByPedido_IdPedido(idPedido)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El pedido " + idPedido + " no tiene calificación"));
    }

    private void validarPuntuacion(Integer puntuacion, String campo) {
        if (puntuacion == null || puntuacion < 1 || puntuacion > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, campo + " debe estar entre 1 y 5");
        }
    }
}
