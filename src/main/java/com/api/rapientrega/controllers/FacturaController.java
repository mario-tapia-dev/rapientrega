package com.api.rapientrega.controllers;

import com.api.rapientrega.models.Factura;
import com.api.rapientrega.services.FacturacionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturacionService facturacionService;

    public FacturaController(FacturacionService facturacionService) {
        this.facturacionService = facturacionService;
    }

    @GetMapping("/pedido/{idPedido}")
    public Factura obtenerPorPedido(@PathVariable Integer idPedido) {
        return facturacionService.obtenerPorPedido(idPedido);
    }
}
