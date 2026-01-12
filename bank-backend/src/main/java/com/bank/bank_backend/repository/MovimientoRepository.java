package com.bank.bank_backend.repository;

import com.bank.bank_backend.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Movimientos por cuentaId y rango (útil para reportes luego)
    List<Movimiento> findByCuentaCuentaIdAndFechaBetween(Long cuentaId, LocalDateTime desde, LocalDateTime hasta);

    // Lista todos los movimientos de una cuenta por número (ordenados)
    List<Movimiento> findByCuenta_NumeroCuentaOrderByFechaAsc(String numeroCuenta);

    // Movimientos por número y rango (útil para reportes)
    List<Movimiento> findByCuenta_NumeroCuentaAndFechaBetweenOrderByFechaAsc(
            String numeroCuenta,
            LocalDateTime desde,
            LocalDateTime hasta
    );

    // NUEVO: Listado por cliente (usuario) + rango (ordenado)
    List<Movimiento> findByCuenta_Cliente_ClienteIdAndFechaBetweenOrderByFechaAsc(
            Long clienteId,
            LocalDateTime desde,
            LocalDateTime hasta
    );
}
