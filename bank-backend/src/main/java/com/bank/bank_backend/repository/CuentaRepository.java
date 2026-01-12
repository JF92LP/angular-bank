package com.bank.bank_backend.repository;

import com.bank.bank_backend.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    // Buscar cuenta por número (ya usado en movimientos y reportes)
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    // Validación de unicidad (ya usado en creación)
    boolean existsByNumeroCuenta(String numeroCuenta);

    // Listar cuentas por cliente (por ID)
    List<Cuenta> findByClienteClienteId(Long clienteId);

    // 🔹 NUEVO: listar cuentas de un cliente ordenadas (para reportes por titular)
    List<Cuenta> findByClienteClienteIdOrderByNumeroCuentaAsc(Long clienteId);
}
