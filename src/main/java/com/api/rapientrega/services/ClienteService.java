package com.api.rapientrega.services;

import com.api.rapientrega.models.Cliente;
import com.api.rapientrega.repositories.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente obtener(Integer idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente no encontrado: " + idCliente));
    }

    @Transactional
    public Cliente crear(Cliente cliente) {
        validarCampos(cliente);
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un cliente con el email " + cliente.getEmail());
        }
        cliente.setIdCliente(null);
        Cliente guardado = clienteRepository.save(cliente);
        log.info("[CLIENTE] Cliente registrado: {} (id {})", guardado.getNombre(), guardado.getIdCliente());
        return guardado;
    }

    @Transactional
    public Cliente actualizar(Integer idCliente, Cliente datos) {
        Cliente existente = obtener(idCliente);
        validarCampos(datos);
        if (clienteRepository.existsByEmailAndIdClienteNot(datos.getEmail(), idCliente)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe otro cliente con el email " + datos.getEmail());
        }
        existente.setNombre(datos.getNombre());
        existente.setEmail(datos.getEmail());
        existente.setTelefono(datos.getTelefono());
        existente.setDireccion(datos.getDireccion());
        return clienteRepository.save(existente);
    }

    private void validarCampos(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()
                || cliente.getEmail() == null || cliente.getEmail().isBlank()
                || cliente.getDireccion() == null || cliente.getDireccion().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "nombre, email y direccion son obligatorios");
        }
    }
}
