package com.api.rapientrega.controllers;

import com.api.rapientrega.models.Repartidor;
import com.api.rapientrega.services.RepartidorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repartidores")
public class RepartidorController {

    private final RepartidorService repartidorService;

    public RepartidorController(RepartidorService repartidorService) {
        this.repartidorService = repartidorService;
    }

    @GetMapping
    public List<Repartidor> listar() {
        return repartidorService.listar();
    }

    @GetMapping("/{idRepartidor}")
    public Repartidor obtener(@PathVariable Integer idRepartidor) {
        return repartidorService.obtener(idRepartidor);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Repartidor crear(@RequestBody Repartidor repartidor) {
        return repartidorService.crear(repartidor);
    }

    @PutMapping("/{idRepartidor}")
    public Repartidor actualizar(@PathVariable Integer idRepartidor, @RequestBody Repartidor repartidor) {
        return repartidorService.actualizar(idRepartidor, repartidor);
    }

    // El repartidor reporta su posición: PUT /api/repartidores/1/ubicacion?latitud=19.17&longitud=-96.13
    @PutMapping("/{idRepartidor}/ubicacion")
    public Repartidor actualizarUbicacion(@PathVariable Integer idRepartidor,
                                          @RequestParam Double latitud,
                                          @RequestParam Double longitud) {
        return repartidorService.actualizarUbicacion(idRepartidor, latitud, longitud);
    }
}
