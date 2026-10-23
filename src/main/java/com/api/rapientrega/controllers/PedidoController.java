package com.api.rapientrega.controllers;

import com.api.rapientrega.models.Pedido;
import com.api.rapientrega.services.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // GET /api/pedidos?estado=PENDIENTE (el filtro es opcional)
    @GetMapping
    public List<Pedido> listar(@RequestParam(required = false) Pedido.Estado estado) {
        return pedidoService.listar(estado);
    }

    // También sirve como seguimiento: incluye estado y ubicación del repartidor
    @GetMapping("/{idPedido}")
    public Pedido obtener(@PathVariable Integer idPedido) {
        return pedidoService.obtener(idPedido);
    }

    // El cliente crea el pedido y paga con el token de tarjeta
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido crear(@RequestBody Pedido pedido, @RequestParam String tokenTarjeta) {
        return pedidoService.crear(pedido, tokenTarjeta);
    }

    // ---- Acciones del restaurante ----

    @PutMapping("/{idPedido}/aceptar")
    public Pedido aceptar(@PathVariable Integer idPedido) {
        return pedidoService.aceptar(idPedido);
    }

    @PutMapping("/{idPedido}/rechazar")
    public Pedido rechazar(@PathVariable Integer idPedido) {
        return pedidoService.rechazar(idPedido);
    }

    // ---- Acciones del repartidor ----

    @PutMapping("/{idPedido}/recoger")
    public Pedido recoger(@PathVariable Integer idPedido) {
        return pedidoService.recoger(idPedido);
    }

    @PutMapping("/{idPedido}/entregar")
    public Pedido entregar(@PathVariable Integer idPedido) {
        return pedidoService.entregar(idPedido);
    }
}
