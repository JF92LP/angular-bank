package com.bank.bank_backend.repository;

import com.bank.bank_backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByIdentificacion(String identificacion);
}
