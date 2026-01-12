package com.bank.bank_backend.service;

import com.bank.bank_backend.entity.Cliente;
import com.bank.bank_backend.entity.Cuenta;
import com.bank.bank_backend.repository.ClienteRepository;
import com.bank.bank_backend.repository.CuentaRepository;
import com.bank.bank_backend.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepo;
    private final ClienteRepository clienteRepo;

    private final SecureRandom random = new SecureRandom();

    public CuentaService(CuentaRepository cuentaRepo, ClienteRepository clienteRepo) {
        this.cuentaRepo = cuentaRepo;
        this.clienteRepo = clienteRepo;
    }

    public List<Cuenta> listar() {
        return cuentaRepo.findAll();
    }

    public List<Cuenta> listarPorCliente(Long clienteId) {
        return cuentaRepo.findByClienteClienteId(clienteId);
    }

    public Cuenta crear(Cuenta cuenta, Long clienteId) {
        Cliente cliente = clienteRepo.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        // Validaciones mínimas
        if (cuenta.getTipoCuenta() == null || cuenta.getTipoCuenta().trim().isEmpty()) {
            throw new RuntimeException("Tipo de cuenta es obligatorio");
        }

        if (cuenta.getSaldoInicial() == null) cuenta.setSaldoInicial(BigDecimal.ZERO);
        if (cuenta.getSaldoInicial().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Saldo inicial no puede ser negativo");
        }

        // Numero de cuenta: SIEMPRE lo genera el backend
        cuenta.setNumeroCuenta(generarNumeroCuentaUnico(9));

        // Al crear: saldoActual inicia igual a saldoInicial
        cuenta.setSaldoActual(cuenta.getSaldoInicial());

        // Estado por defecto si viene null
        if (cuenta.getEstado() == null) cuenta.setEstado(true);

        cuenta.setCliente(cliente);

        return cuentaRepo.save(cuenta);
    }

    public Cuenta actualizar(Long cuentaId, Cuenta cambios) {
        Cuenta c = cuentaRepo.findById(cuentaId)
                .orElseThrow(() -> new NotFoundException("Cuenta no encontrada"));

        // Solo campos editables
        if (cambios.getTipoCuenta() != null && !cambios.getTipoCuenta().trim().isEmpty()) {
            c.setTipoCuenta(cambios.getTipoCuenta().trim());
        }

        if (cambios.getEstado() != null) {
            c.setEstado(cambios.getEstado());
        }

        // NO permitir cambiar numeroCuenta ni saldos aquí.
        return cuentaRepo.save(c);
    }

    public void eliminar(Long cuentaId) {
        if (!cuentaRepo.existsById(cuentaId)) {
            throw new NotFoundException("Cuenta no encontrada");
        }
        cuentaRepo.deleteById(cuentaId);
    }

    // =========================
    // Helpers
    // =========================

    private String generarNumeroCuentaUnico(int digitos) {
        // 10 intentos: suficiente para esta prueba técnica
        for (int i = 0; i < 10; i++) {
            String candidato = generarDigitos(digitos);
            if (!cuentaRepo.existsByNumeroCuenta(candidato)) {
                return candidato;
            }
        }
        throw new RuntimeException("No se pudo generar un número de cuenta único. Intente nuevamente.");
    }

    private String generarDigitos(int digitos) {
        StringBuilder sb = new StringBuilder(digitos);
        for (int i = 0; i < digitos; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
