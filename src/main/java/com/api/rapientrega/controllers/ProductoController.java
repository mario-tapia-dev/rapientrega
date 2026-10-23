package com.api.rapientrega.controllers;

import com.api.rapientrega.models.Producto;
import com.api.rapientrega.services.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // Menú de un restaurante
    @GetMapping("/restaurantes/{idRestaurante}/productos")
    public List<Producto> menu(@PathVariable Integer idRestaurante) {
        return productoService.listarPorRestaurante(idRestaurante);
    }

    @PostMapping("/restaurantes/{idRestaurante}/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@PathVariable Integer idRestaurante, @RequestBody Producto producto) {
        return productoService.crear(idRestaurante, producto);
    }

    @PutMapping("/productos/{idProducto}")
    public Producto actualizar(@PathVariable Integer idProducto, @RequestBody Producto producto) {
        return productoService.actualizar(idProducto, producto);
    }
}
