package com.api.rapientrega.services;

import com.api.rapientrega.models.Cliente;
import com.api.rapientrega.models.DetallePedido;
import com.api.rapientrega.models.Pedido;
import com.api.rapientrega.models.Producto;
import com.api.rapientrega.models.Repartidor;
import com.api.rapientrega.models.Restaurante;
import com.api.rapientrega.repositories.ClienteRepository;
import com.api.rapientrega.repositories.PedidoRepository;
import com.api.rapientrega.repositories.ProductoRepository;
import com.api.rapientrega.repositories.RepartidorRepository;
import com.api.rapientrega.repositories.RestauranteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    // Regla de negocio: el restaurante tiene 5 minutos para aceptar
    private static final long MINUTOS_PARA_ACEPTAR = 5;

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProductoRepository productoRepository;
    private final RepartidorRepository repartidorRepository;
    private final PagoService pagoService;
    private final FacturacionService facturacionService;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         RestauranteRepository restauranteRepository,
                         ProductoRepository productoRepository,
                         RepartidorRepository repartidorRepository,
                         PagoService pagoService,
                         FacturacionService facturacionService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.productoRepository = productoRepository;
        this.repartidorRepository = repartidorRepository;
        this.pagoService = pagoService;
        this.facturacionService = facturacionService;
    }

    // ---------------- Consultas ----------------

    public List<Pedido> listar(Pedido.Estado estado) {
        return estado == null ? pedidoRepository.findAll() : pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> listarPorCliente(Integer idCliente) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado: " + idCliente);
        }
        return pedidoRepository.findByCliente_IdCliente(idCliente);
    }

    // Sirve también como "seguimiento": trae estado y ubicación actual del repartidor
    public Pedido obtener(Integer idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido no encontrado: " + idPedido));
    }

    // ---------------- Flujo del pedido ----------------

    @Transactional
    public Pedido crear(Pedido solicitud, String tokenTarjeta) {
        if (solicitud.getCliente() == null || solicitud.getCliente().getIdCliente() == null
                || solicitud.getRestaurante() == null || solicitud.getRestaurante().getIdRestaurante() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "cliente.idCliente y restaurante.idRestaurante son obligatorios");
        }
        if (solicitud.getDetalles() == null || solicitud.getDetalles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido debe tener al menos un producto");
        }

        Cliente cliente = clienteRepository.findById(solicitud.getCliente().getIdCliente())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        Restaurante restaurante = restauranteRepository.findById(solicitud.getRestaurante().getIdRestaurante())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDireccionEntrega(
                solicitud.getDireccionEntrega() == null || solicitud.getDireccionEntrega().isBlank()
                        ? cliente.getDireccion()
                        : solicitud.getDireccionEntrega());

        // El total y los precios los calcula el servidor, nunca se confía en lo que manda el cliente
        BigDecimal total = BigDecimal.ZERO;
        List<DetallePedido> detalles = new ArrayList<>();
        for (DetallePedido pedidoDetalle : solicitud.getDetalles()) {
            if (pedidoDetalle.getProducto() == null || pedidoDetalle.getProducto().getIdProducto() == null
                    || pedidoDetalle.getCantidad() == null || pedidoDetalle.getCantidad() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cada detalle necesita producto.idProducto y cantidad mayor a 0");
            }
            Producto producto = productoRepository.findById(pedidoDetalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Producto no encontrado: " + pedidoDetalle.getProducto().getIdProducto()));
            if (!producto.getRestaurante().getIdRestaurante().equals(restaurante.getIdRestaurante())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El producto " + producto.getIdProducto() + " no pertenece al restaurante "
                                + restaurante.getIdRestaurante());
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(pedidoDetalle.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalles.add(detalle);

            total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(pedidoDetalle.getCantidad())));
        }
        pedido.setDetalles(detalles);
        pedido.setTotal(total);
        pedido.setEstado(Pedido.Estado.PENDIENTE);
        pedido.setFechaCreacion(LocalDateTime.now());

        Pedido guardado = pedidoRepository.save(pedido);

        // Cobro en la pasarela externa; si la rechaza, todo se revierte y el pedido no se crea
        pagoService.cobrar(guardado, tokenTarjeta);

        log.info("[PEDIDO] Pedido {} creado por {} para {} (total ${}). Esperando respuesta del restaurante ({} min)",
                guardado.getIdPedido(), cliente.getNombre(), restaurante.getNombre(), total, MINUTOS_PARA_ACEPTAR);
        return guardado;
    }

    @Transactional
    public Pedido aceptar(Integer idPedido) {
        Pedido pedido = bloquear(idPedido);
        exigirEstado(pedido, Pedido.Estado.PENDIENTE, "aceptar");

        if (pedido.getFechaCreacion().plusMinutes(MINUTOS_PARA_ACEPTAR).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El plazo de " + MINUTOS_PARA_ACEPTAR + " minutos para aceptar ya venció; "
                            + "el pedido será cancelado y reembolsado");
        }

        pedido.setEstado(Pedido.Estado.ACEPTADO);
        log.info("[PEDIDO] {} aceptó el pedido {}", pedido.getRestaurante().getNombre(), idPedido);

        asignarRepartidor(pedido);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido rechazar(Integer idPedido) {
        Pedido pedido = bloquear(idPedido);
        exigirEstado(pedido, Pedido.Estado.PENDIENTE, "rechazar");

        pedido.setEstado(Pedido.Estado.RECHAZADO);
        pagoService.reembolsar(pedido);
        log.info("[PEDIDO] {} rechazó el pedido {}. Pago reembolsado", pedido.getRestaurante().getNombre(), idPedido);
        return pedidoRepository.save(pedido);
    }

    // El repartidor recoge el pedido en el restaurante
    @Transactional
    public Pedido recoger(Integer idPedido) {
        Pedido pedido = bloquear(idPedido);
        exigirEstado(pedido, Pedido.Estado.ACEPTADO, "recoger");
        if (pedido.getRepartidor() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El pedido aún no tiene repartidor asignado");
        }

        pedido.setEstado(Pedido.Estado.EN_CAMINO);
        log.info("[PEDIDO] Repartidor {} recogió el pedido {} y va en camino",
                pedido.getRepartidor().getNombre(), idPedido);
        return pedidoRepository.save(pedido);
    }

    // El repartidor entrega: se libera y se factura en el sistema heredado
    @Transactional
    public Pedido entregar(Integer idPedido) {
        Pedido pedido = bloquear(idPedido);
        exigirEstado(pedido, Pedido.Estado.EN_CAMINO, "entregar");

        pedido.setEstado(Pedido.Estado.ENTREGADO);
        Repartidor repartidor = pedido.getRepartidor();
        repartidor.setDisponible(true);
        repartidorRepository.save(repartidor);

        facturacionService.facturar(pedido);

        log.info("[PEDIDO] Pedido {} entregado a {}", idPedido, pedido.getCliente().getNombre());
        return pedidoRepository.save(pedido);
    }

    // ---------------- Tareas del scheduler ----------------

    // Cancela y reembolsa los pedidos que el restaurante no aceptó en 5 minutos
    @Transactional
    public void cancelarVencidos() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(MINUTOS_PARA_ACEPTAR);
        for (Integer id : pedidoRepository.findIdsByEstadoAndFechaCreacionBefore(Pedido.Estado.PENDIENTE, limite)) {
            Pedido pedido = bloquear(id);
            if (pedido.getEstado() != Pedido.Estado.PENDIENTE) {
                continue; // el restaurante respondió justo antes
            }
            pedido.setEstado(Pedido.Estado.CANCELADO);
            pagoService.reembolsar(pedido);
            pedidoRepository.save(pedido);
            log.info("[TIMEOUT] Pedido {} cancelado: {} no respondió en {} min. Pago reembolsado",
                    id, pedido.getRestaurante().getNombre(), MINUTOS_PARA_ACEPTAR);
        }
    }

    // Pedidos aceptados que se quedaron sin repartidor: se reintenta asignar
    @Transactional
    public void reintentarAsignaciones() {
        for (Integer id : pedidoRepository.findIdsSinRepartidor(Pedido.Estado.ACEPTADO)) {
            Pedido pedido = bloquear(id);
            if (pedido.getEstado() != Pedido.Estado.ACEPTADO || pedido.getRepartidor() != null) {
                continue;
            }
            if (!asignarRepartidor(pedido)) {
                break; // si no hay nadie libre, no tiene caso seguir con los demás
            }
            pedidoRepository.save(pedido);
        }
    }

    // ---------------- Auxiliares ----------------

    // Asigna el repartidor libre más cercano al restaurante. Devuelve false si no hay ninguno.
    private boolean asignarRepartidor(Pedido pedido) {
        List<Repartidor> libres = repartidorRepository.findDisponiblesForUpdate();
        if (libres.isEmpty()) {
            log.info("[ASIGNACION] Pedido {}: no hay repartidores libres, se reintentará", pedido.getIdPedido());
            return false;
        }

        Restaurante restaurante = pedido.getRestaurante();
        Repartidor masCercano = libres.stream()
                .min(Comparator.comparingDouble(r -> distanciaKm(
                        restaurante.getLatitud(), restaurante.getLongitud(), r.getLatitud(), r.getLongitud())))
                .get();

        masCercano.setDisponible(false);
        repartidorRepository.save(masCercano);
        pedido.setRepartidor(masCercano);

        log.info("[ASIGNACION] Pedido {} asignado a {} (a {} km del restaurante)", pedido.getIdPedido(),
                masCercano.getNombre(),
                String.format("%.2f", distanciaKm(restaurante.getLatitud(), restaurante.getLongitud(),
                        masCercano.getLatitud(), masCercano.getLongitud())));
        return true;
    }

    // Fórmula de Haversine: distancia en km entre dos coordenadas
    private static double distanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double radioTierra = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return radioTierra * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private Pedido bloquear(Integer idPedido) {
        return pedidoRepository.findByIdForUpdate(idPedido)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido no encontrado: " + idPedido));
    }

    private void exigirEstado(Pedido pedido, Pedido.Estado esperado, String accion) {
        if (pedido.getEstado() != esperado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede " + accion + " un pedido en estado " + pedido.getEstado());
        }
    }
}
