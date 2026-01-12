package com.bank.bank_backend.service;

import com.bank.bank_backend.dto.CrearMovimientoRequest;
import com.bank.bank_backend.entity.Cuenta;
import com.bank.bank_backend.entity.Movimiento;
import com.bank.bank_backend.exception.BadRequestException;
import com.bank.bank_backend.exception.NotFoundException;
import com.bank.bank_backend.repository.CuentaRepository;
import com.bank.bank_backend.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class MovimientoService {

    private final CuentaRepository cuentaRepo;
    private final MovimientoRepository movRepo;

    public MovimientoService(CuentaRepository cuentaRepo, MovimientoRepository movRepo) {
        this.cuentaRepo = cuentaRepo;
        this.movRepo = movRepo;
    }

    @Transactional
    public Movimiento crear(CrearMovimientoRequest req) {
        if (req == null) throw new BadRequestException("Request inválido");

        String numeroCuenta = req.getNumeroCuenta();
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new BadRequestException("numeroCuenta es requerido");
        }

        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta.trim())
                .orElseThrow(() -> new NotFoundException("Cuenta no encontrada"));

        if (Boolean.FALSE.equals(cuenta.getEstado())) {
            throw new BadRequestException("La cuenta está inactiva");
        }

        BigDecimal valorReq = req.getValor();
        if (valorReq == null || valorReq.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El valor debe ser mayor a 0");
        }

        String tipo = req.getTipoMovimiento();
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new BadRequestException("tipoMovimiento es requerido");
        }

        BigDecimal saldoActual = cuenta.getSaldoActual();
        if (saldoActual == null) saldoActual = BigDecimal.ZERO;

        String tipoNorm = normalizarTipo(tipo);

        BigDecimal delta;
        if (tipoNorm.equals("Credito")) {
            delta = valorReq; // suma
        } else {
            // Debito
            delta = valorReq.negate(); // resta (valor negativo)
            if (saldoActual.add(delta).compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("Saldo no disponible");
            }
        }

        BigDecimal saldoNuevo = saldoActual.add(delta);

        Movimiento m = new Movimiento();
        m.setFecha(LocalDateTime.now());
        m.setTipoMovimiento(tipoNorm);
        m.setValor(delta);      // positivo o negativo
        m.setSaldo(saldoNuevo); // saldo disponible luego del movimiento
        m.setCuenta(cuenta);

        Movimiento guardado = movRepo.save(m);

        cuenta.setSaldoActual(saldoNuevo);
        cuentaRepo.save(cuenta);

        return guardado;
    }

    private String normalizarTipo(String tipo) {
        String t = tipo.trim().toLowerCase();
        if (t.equals("credito")) return "Credito";
        if (t.equals("debito")) return "Debito";
        throw new BadRequestException("tipoMovimiento debe ser 'Credito' o 'Debito'");
    }
}
