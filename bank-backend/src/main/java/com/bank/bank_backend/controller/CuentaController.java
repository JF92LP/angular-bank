package com.bank.bank_backend.controller;

import com.bank.bank_backend.entity.Cuenta;
import com.bank.bank_backend.service.CuentaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@CrossOrigin(origins = "http://localhost:4200")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cuenta> listar() {
        return service.listar();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Cuenta> listarPorCliente(@PathVariable Long clienteId) {
        return service.listarPorCliente(clienteId);
    }

    @PostMapping("/cliente/{clienteId}")
    public Cuenta crear(@PathVariable Long clienteId, @RequestBody Cuenta cuenta) {
        return service.crear(cuenta, clienteId);
    }

    @PutMapping("/{cuentaId}")
    public Cuenta actualizar(@PathVariable Long cuentaId, @RequestBody Cuenta cuenta) {
        return service.actualizar(cuentaId, cuenta);
    }

    @DeleteMapping("/{cuentaId}")
    public void eliminar(@PathVariable Long cuentaId) {
        service.eliminar(cuentaId);
    }
}
