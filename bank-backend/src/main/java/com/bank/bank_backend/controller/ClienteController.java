package com.bank.bank_backend.controller;

import com.bank.bank_backend.dto.UpdateClienteRequest;
import com.bank.bank_backend.dto.CrearClienteRequest;
import com.bank.bank_backend.entity.Cliente;
import com.bank.bank_backend.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cliente> listar() {
        return service.listar();
    }

    @PostMapping
    public Cliente crear(@Valid @RequestBody CrearClienteRequest req) {
        return service.crear(req);
    }

    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Long id, @Valid @RequestBody UpdateClienteRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
