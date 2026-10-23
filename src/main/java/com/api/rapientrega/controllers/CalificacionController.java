package com.api.rapientrega.controllers;

import com.api.rapientrega.models.Calificacion;
import com.api.rapientrega.services.CalificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos/{idPedido}/calificacion")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Calificacion calificar(@PathVariable Integer idPedido, @RequestBody Calificacion calificacion) {
        return calificacionService.calificar(idPedido, calificacion);
    }

    @GetMapping
    public Calificacion obtener(@PathVariable Integer idPedido) {
        return calificacionService.obtenerPorPedido(idPedido);
    }
}
