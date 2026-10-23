package com.api.rapientrega.services;

import com.api.rapientrega.models.Repartidor;
import com.api.rapientrega.repositories.RepartidorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RepartidorService {

    private static final Logger log = LoggerFactory.getLogger(RepartidorService.class);

    private final RepartidorRepository repartidorRepository;

    public RepartidorService(RepartidorRepository repartidorRepository) {
        this.repartidorRepository = repartidorRepository;
    }

    public List<Repartidor> listar() {
        return repartidorRepository.findAll();
    }

    public Repartidor obtener(Integer idRepartidor) {
        return repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Repartidor no encontrado: " + idRepartidor));
    }

    @Transactional
    public Repartidor crear(Repartidor repartidor) {
        if (repartidor.getNombre() == null || repartidor.getNombre().isBlank()
                || repartidor.getLatitud() == null || repartidor.getLongitud() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "nombre, latitud y longitud son obligatorios");
        }
        repartidor.setIdRepartidor(null);
        repartidor.setDisponible(true); // un repartidor nuevo siempre empieza libre
        Repartidor guardado = repartidorRepository.save(repartidor);
        log.info("[REPARTIDOR] Repartidor registrado: {} (id {})", guardado.getNombre(), guardado.getIdRepartidor());
        return guardado;
    }

    @Transactional
    public Repartidor actualizar(Integer idRepartidor, Repartidor datos) {
        Repartidor existente = obtener(idRepartidor);
        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nombre es obligatorio");
        }
        existente.setNombre(datos.getNombre());
        return repartidorRepository.save(existente);
    }

    // El repartidor reporta dónde está (así el cliente puede seguir su pedido)
    @Transactional
    public Repartidor actualizarUbicacion(Integer idRepartidor, Double latitud, Double longitud) {
        Repartidor repartidor = obtener(idRepartidor);
        repartidor.setLatitud(latitud);
        repartidor.setLongitud(longitud);
        log.info("[UBICACION] Repartidor {} ahora en ({}, {})", idRepartidor, latitud, longitud);
        return repartidorRepository.save(repartidor);
    }
}
