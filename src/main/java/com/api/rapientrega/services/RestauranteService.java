package com.api.rapientrega.services;

import com.api.rapientrega.models.Restaurante;
import com.api.rapientrega.repositories.RestauranteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RestauranteService {

    private static final Logger log = LoggerFactory.getLogger(RestauranteService.class);

    private final RestauranteRepository restauranteRepository;

    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    // Filtros opcionales: si no se mandan, se listan todos
    public List<Restaurante> listar(String nombre, String tipoCocina) {
        return restauranteRepository.findByNombreContainingIgnoreCaseAndTipoCocinaContainingIgnoreCase(
                nombre == null ? "" : nombre,
                tipoCocina == null ? "" : tipoCocina);
    }

    public Restaurante obtener(Integer idRestaurante) {
        return restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Restaurante no encontrado: " + idRestaurante));
    }

    @Transactional
    public Restaurante crear(Restaurante restaurante) {
        validarCampos(restaurante);
        restaurante.setIdRestaurante(null);
        Restaurante guardado = restauranteRepository.save(restaurante);
        log.info("[RESTAURANTE] Restaurante registrado: {} (id {})", guardado.getNombre(), guardado.getIdRestaurante());
        return guardado;
    }

    @Transactional
    public Restaurante actualizar(Integer idRestaurante, Restaurante datos) {
        Restaurante existente = obtener(idRestaurante);
        validarCampos(datos);
        existente.setNombre(datos.getNombre());
        existente.setTipoCocina(datos.getTipoCocina());
        existente.setDireccion(datos.getDireccion());
        existente.setLatitud(datos.getLatitud());
        existente.setLongitud(datos.getLongitud());
        return restauranteRepository.save(existente);
    }

    private void validarCampos(Restaurante r) {
        if (r.getNombre() == null || r.getNombre().isBlank()
                || r.getTipoCocina() == null || r.getTipoCocina().isBlank()
                || r.getDireccion() == null || r.getDireccion().isBlank()
                || r.getLatitud() == null || r.getLongitud() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "nombre, tipoCocina, direccion, latitud y longitud son obligatorios");
        }
    }
}
