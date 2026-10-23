package com.api.rapientrega.services;

import com.api.rapientrega.models.Factura;
import com.api.rapientrega.models.Pedido;
import com.api.rapientrega.repositories.FacturaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/**
 * Adaptador hacia el sistema de facturación heredado (2012), que no se puede reemplazar.
 * La plataforma moderna se adapta a su formato: un registro de texto de ancho fijo.
 * Formato: [8 id pedido][30 nombre cliente][10 total en centavos][8 fecha ddMMyyyy]
 */
@Service
public class FacturacionService {

    private static final Logger log = LoggerFactory.getLogger(FacturacionService.class);
    private static final DateTimeFormatter FECHA_LEGADO = DateTimeFormatter.ofPattern("ddMMyyyy");

    private final FacturaRepository facturaRepository;

    public FacturacionService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    @Transactional
    public Factura facturar(Pedido pedido) {
        String registro = armarRegistroLegado(pedido);
        String folio = enviarAlSistemaLegado(registro);

        Factura factura = new Factura();
        factura.setPedido(pedido);
        factura.setFolio(folio);
        factura.setFecha(LocalDateTime.now());
        return facturaRepository.save(factura);
    }

    public Factura obtenerPorPedido(Integer idPedido) {
        return facturaRepository.findByPedido_IdPedido(idPedido)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No hay factura para el pedido " + idPedido));
    }

    // Traduce el pedido moderno al formato viejo
    private String armarRegistroLegado(Pedido pedido) {
        long centavos = pedido.getTotal().movePointRight(2).longValue();
        return String.format(Locale.ROOT, "%08d%-30.30s%010d%s",
                pedido.getIdPedido(),
                pedido.getCliente().getNombre().toUpperCase(Locale.ROOT),
                centavos,
                LocalDate.now().format(FECHA_LEGADO));
    }

    // Simulación: el sistema heredado "recibe" el registro y devuelve un folio
    private String enviarAlSistemaLegado(String registro) {
        String folio = "FOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[LEGADO] Registro enviado: |{}|", registro);
        log.info("[LEGADO] Folio devuelto: {}", folio);
        return folio;
    }
}
