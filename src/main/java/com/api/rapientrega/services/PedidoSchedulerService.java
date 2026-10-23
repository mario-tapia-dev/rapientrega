package com.api.rapientrega.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PedidoSchedulerService {

    private final PedidoService pedidoService;

    public PedidoSchedulerService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Cada 30 segundos: cancela pedidos sin respuesta (5 min) y reintenta asignar repartidores
    @Scheduled(fixedRate = 30_000)
    public void revisarPedidos() {
        pedidoService.cancelarVencidos();
        pedidoService.reintentarAsignaciones();
    }
}
