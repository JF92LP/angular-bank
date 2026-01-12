package com.bank.bank_backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class ReporteEstadoCuentaResponse {

    private String numeroCuenta;
    private String cliente;
    private BigDecimal saldoActual;
    private List<ReporteMovimientoItem> movimientos;

    // NUEVO: PDF en Base64 (opcional)
    private String pdfBase64;

    public ReporteEstadoCuentaResponse() {}

    public ReporteEstadoCuentaResponse(
            String numeroCuenta,
            String cliente,
            BigDecimal saldoActual,
            List<ReporteMovimientoItem> movimientos
    ) {
        this.numeroCuenta = numeroCuenta;
        this.cliente = cliente;
        this.saldoActual = saldoActual;
        this.movimientos = movimientos;
    }

    // NUEVO (opcional): constructor con pdfBase64
    public ReporteEstadoCuentaResponse(
            String numeroCuenta,
            String cliente,
            BigDecimal saldoActual,
            List<ReporteMovimientoItem> movimientos,
            String pdfBase64
    ) {
        this.numeroCuenta = numeroCuenta;
        this.cliente = cliente;
        this.saldoActual = saldoActual;
        this.movimientos = movimientos;
        this.pdfBase64 = pdfBase64;
    }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public BigDecimal getSaldoActual() { return saldoActual; }
    public void setSaldoActual(BigDecimal saldoActual) { this.saldoActual = saldoActual; }

    public List<ReporteMovimientoItem> getMovimientos() { return movimientos; }
    public void setMovimientos(List<ReporteMovimientoItem> movimientos) { this.movimientos = movimientos; }

    public String getPdfBase64() { return pdfBase64; }
    public void setPdfBase64(String pdfBase64) { this.pdfBase64 = pdfBase64; }
}
