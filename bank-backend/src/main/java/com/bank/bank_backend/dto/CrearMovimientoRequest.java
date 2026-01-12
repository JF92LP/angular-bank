package com.bank.bank_backend.dto;

import java.math.BigDecimal;

public class CrearMovimientoRequest {

    private String numeroCuenta;
    private String tipoMovimiento; // "Credito" | "Debito"
    private BigDecimal valor;

    public CrearMovimientoRequest() {}

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
