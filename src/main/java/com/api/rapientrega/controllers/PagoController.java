package com.api.rapientrega.controllers;

import com.api.rapientrega.models.Pago;
import com.api.rapientrega.services.PagoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping("/pedido/{idPedido}")
    public Pago obtenerPorPedido(@PathVariable Integer idPedido) {
        return pagoService.obtenerPorPedido(idPedido);
    }
}
