package com.api.rapientrega.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Simulación de la pasarela de pagos EXTERNA. En la vida real sería una API de un tercero;
 * aquí solo genera referencias falsas y loggea. Tokens de prueba:
 *   - "tok_rechazada" -> la pasarela rechaza la tarjeta (402)
 *   - cualquier otro token no vacío -> aprobado
 */
@Service
public class PasarelaPagosService {

    private static final Logger log = LoggerFactory.getLogger(PasarelaPagosService.class);

    public String cobrar(String tokenTarjeta, BigDecimal monto) {
        if (tokenTarjeta == null || tokenTarjeta.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token de tarjeta es obligatorio");
        }
        if (tokenTarjeta.equals("tok_rechazada")) {
            log.warn("[PASARELA] Cobro de ${} RECHAZADO (token {})", monto, tokenTarjeta);
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Pago rechazado por la pasarela");
        }
        String referencia = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[PASARELA] Cobro de ${} APROBADO (token {}). Referencia {}", monto, tokenTarjeta, referencia);
        return referencia;
    }

    public void reembolsar(String referencia) {
        log.info("[PASARELA] Reembolso emitido para la referencia {}", referencia);
    }
}
