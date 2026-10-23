package com.api.rapientrega.controllers;

import com.api.rapientrega.services.PasarelaPagosService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Endpoints de la pasarela de pagos simulada, para probarla de forma directa
 * (la plataforma la usa internamente al crear y reembolsar pedidos).
 */
@RestController
@RequestMapping("/api/pasarela")
public class PasarelaController {

    private final PasarelaPagosService pasarela;

    public PasarelaController(PasarelaPagosService pasarela) {
        this.pasarela = pasarela;
    }

    // POST /api/pasarela/cobros?token=tok_ok&monto=150.00   (token "tok_rechazada" simula rechazo)
    @PostMapping("/cobros")
    public Map<String, Object> cobrar(@RequestParam String token, @RequestParam BigDecimal monto) {
        String referencia = pasarela.cobrar(token, monto);
        return Map.of("referencia", referencia, "estado", "APROBADO", "monto", monto);
    }

    // POST /api/pasarela/reembolsos?referencia=PAY-XXXXXXXX
    @PostMapping("/reembolsos")
    public Map<String, Object> reembolsar(@RequestParam String referencia) {
        pasarela.reembolsar(referencia);
        return Map.of("referencia", referencia, "estado", "REEMBOLSADO");
    }
}
