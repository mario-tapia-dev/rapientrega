package com.api.rapientrega.services;

import com.api.rapientrega.models.Pago;
import com.api.rapientrega.models.Pedido;
import com.api.rapientrega.repositories.PagoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PasarelaPagosService pasarela;

    public PagoService(PagoRepository pagoRepository, PasarelaPagosService pasarela) {
        this.pagoRepository = pagoRepository;
        this.pasarela = pasarela;
    }

    // Si la pasarela rechaza, lanza excepción y el pedido completo se revierte
    @Transactional
    public Pago cobrar(Pedido pedido, String tokenTarjeta) {
        String referencia = pasarela.cobrar(tokenTarjeta, pedido.getTotal());

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMonto(pedido.getTotal());
        pago.setEstado(Pago.Estado.APROBADO);
        pago.setReferencia(referencia);
        pago.setFecha(LocalDateTime.now());
        return pagoRepository.save(pago);
    }

    @Transactional
    public Pago reembolsar(Pedido pedido) {
        Pago pago = obtenerPorPedido(pedido.getIdPedido());
        if (pago.getEstado() == Pago.Estado.REEMBOLSADO) {
            return pago;
        }
        pasarela.reembolsar(pago.getReferencia());
        pago.setEstado(Pago.Estado.REEMBOLSADO);
        return pagoRepository.save(pago);
    }

    public Pago obtenerPorPedido(Integer idPedido) {
        return pagoRepository.findByPedido_IdPedido(idPedido)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No hay pago para el pedido " + idPedido));
    }
}
