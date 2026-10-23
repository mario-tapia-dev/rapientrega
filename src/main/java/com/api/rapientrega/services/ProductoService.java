package com.api.rapientrega.services;

import com.api.rapientrega.models.Producto;
import com.api.rapientrega.models.Restaurante;
import com.api.rapientrega.repositories.ProductoRepository;
import com.api.rapientrega.repositories.RestauranteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;
    private final RestauranteRepository restauranteRepository;

    public ProductoService(ProductoRepository productoRepository, RestauranteRepository restauranteRepository) {
        this.productoRepository = productoRepository;
        this.restauranteRepository = restauranteRepository;
    }

    public List<Producto> listarPorRestaurante(Integer idRestaurante) {
        if (!restauranteRepository.existsById(idRestaurante)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante no encontrado: " + idRestaurante);
        }
        return productoRepository.findByRestaurante_IdRestaurante(idRestaurante);
    }

    @Transactional
    public Producto crear(Integer idRestaurante, Producto producto) {
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Restaurante no encontrado: " + idRestaurante));
        validarCampos(producto);
        producto.setIdProducto(null);
        producto.setRestaurante(restaurante);
        Producto guardado = productoRepository.save(producto);
        log.info("[PRODUCTO] {} (${}) agregado al menú de {}", guardado.getNombre(), guardado.getPrecio(),
                restaurante.getNombre());
        return guardado;
    }

    @Transactional
    public Producto actualizar(Integer idProducto, Producto datos) {
        Producto existente = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto no encontrado: " + idProducto));
        validarCampos(datos);
        existente.setNombre(datos.getNombre());
        existente.setPrecio(datos.getPrecio());
        return productoRepository.save(existente);
    }

    private void validarCampos(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()
                || producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "nombre y precio (mayor a 0) son obligatorios");
        }
    }
}
