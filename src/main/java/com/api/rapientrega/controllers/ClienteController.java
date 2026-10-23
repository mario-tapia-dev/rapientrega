package com.api.rapientrega.controllers;

import com.api.rapientrega.models.Cliente;
import com.api.rapientrega.models.Pedido;
import com.api.rapientrega.services.ClienteService;
import com.api.rapientrega.services.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final PedidoService pedidoService;

    public ClienteController(ClienteService clienteService, PedidoService pedidoService) {
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{idCliente}")
    public Cliente obtener(@PathVariable Integer idCliente) {
        return clienteService.obtener(idCliente);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente crear(@RequestBody Cliente cliente) {
        return clienteService.crear(cliente);
    }

    @PutMapping("/{idCliente}")
    public Cliente actualizar(@PathVariable Integer idCliente, @RequestBody Cliente cliente) {
        return clienteService.actualizar(idCliente, cliente);
    }

    // Historial de pedidos del cliente
    @GetMapping("/{idCliente}/pedidos")
    public List<Pedido> pedidosDelCliente(@PathVariable Integer idCliente) {
        return pedidoService.listarPorCliente(idCliente);
    }
}
