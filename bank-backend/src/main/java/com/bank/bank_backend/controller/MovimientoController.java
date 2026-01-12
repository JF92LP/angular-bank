package com.bank.bank_backend.controller;

import com.bank.bank_backend.dto.CrearMovimientoRequest;
import com.bank.bank_backend.entity.Movimiento;
import com.bank.bank_backend.repository.MovimientoRepository;
import com.bank.bank_backend.service.MovimientoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
@CrossOrigin(origins = "http://localhost:4200")
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final MovimientoRepository movRepo;

    public MovimientoController(MovimientoService movimientoService, MovimientoRepository movRepo) {
        this.movimientoService = movimientoService;
        this.movRepo = movRepo;
    }

    // Crear movimiento (Crédito/Débito)
    @PostMapping
    public Movimiento crear(@RequestBody CrearMovimientoRequest req) {
        return movimientoService.crear(req);
    }

    // Listar movimientos por número de cuenta
    @GetMapping("/cuenta/{numeroCuenta}")
    public List<Movimiento> listarPorCuenta(@PathVariable String numeroCuenta) {
        return movRepo.findByCuenta_NumeroCuentaOrderByFechaAsc(numeroCuenta);
    }
}
