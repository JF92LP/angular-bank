package com.bank.bank_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReporteMovimientoUsuarioItem {

    private LocalDateTime fecha;
    private String cliente;
    private String numeroCuenta;
    private String tipo;              // tipo de cuenta (Ahorros/Corriente) o el que uses
    private BigDecimal saldoInicial;  // saldo inicial de la cuenta
    private Boolean estado;           // estado de la cuenta
    private BigDecimal movimiento;    // movimiento (+ crédito / - débito)
    private BigDecimal saldoDisponible; // saldo luego del movimiento

    public ReporteMovimientoUsuarioItem() {}

    public ReporteMovimientoUsuarioItem(
            LocalDateTime fecha,
            String cliente,
            String numeroCuenta,
            String tipo,
            BigDecimal saldoInicial,
            Boolean estado,
            BigDecimal movimiento,
            BigDecimal saldoDisponible
    ) {
        this.fecha = fecha;
        this.cliente = cliente;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.saldoInicial = saldoInicial;
        this.estado = estado;
        this.movimiento = movimiento;
        this.saldoDisponible = saldoDisponible;
    }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public BigDecimal getMovimiento() { return movimiento; }
    public void setMovimiento(BigDecimal movimiento) { this.movimiento = movimiento; }

    public BigDecimal getSaldoDisponible() { return saldoDisponible; }
    public void setSaldoDisponible(BigDecimal saldoDisponible) { this.saldoDisponible = saldoDisponible; }
}
