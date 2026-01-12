package com.bank.bank_backend.dto;

import java.math.BigDecimal;

public class CrearCuentaRequest {
    private String numeroCuenta;
    private String tipoCuenta;     // "Ahorros" | "Corriente"
    private BigDecimal saldoInicial;
    private Boolean estado;        // opcional

    public CrearCuentaRequest() {}

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
