package com.bank.bank_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReporteMovimientoItem {
    private LocalDateTime fecha;
    private String tipoMovimiento;
    private BigDecimal valor;
    private BigDecimal saldo;

    public ReporteMovimientoItem() {}

    public ReporteMovimientoItem(LocalDateTime fecha, String tipoMovimiento, BigDecimal valor, BigDecimal saldo) {
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.valor = valor;
        this.saldo = saldo;
    }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}
