package com.api.rapientrega.controllers;

import com.api.rapientrega.models.Restaurante;
import com.api.rapientrega.services.RestauranteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    // Búsqueda: GET /api/restaurantes?nombre=taco&tipoCocina=mexicana (ambos opcionales)
    @GetMapping
    public List<Restaurante> listar(@RequestParam(required = false) String nombre,
                                    @RequestParam(required = false) String tipoCocina) {
        return restauranteService.listar(nombre, tipoCocina);
    }

    @GetMapping("/{idRestaurante}")
    public Restaurante obtener(@PathVariable Integer idRestaurante) {
        return restauranteService.obtener(idRestaurante);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurante crear(@RequestBody Restaurante restaurante) {
        return restauranteService.crear(restaurante);
    }

    @PutMapping("/{idRestaurante}")
    public Restaurante actualizar(@PathVariable Integer idRestaurante, @RequestBody Restaurante restaurante) {
        return restauranteService.actualizar(idRestaurante, restaurante);
    }
}
