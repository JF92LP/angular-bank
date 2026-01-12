package com.bank.bank_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cuentas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_numero_cuenta", columnNames = "numero_cuenta")
})
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cuentaId;

    @Column(name = "numero_cuenta", nullable = false)
    private String numeroCuenta;

    @Column(name = "tipo_cuenta", nullable = false)
    private String tipoCuenta;

    @Column(name = "saldo_inicial", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(name = "saldo_actual", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoActual = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean estado = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cuenta_cliente"))
    private Cliente cliente;

    /* =======================
       GETTERS Y SETTERS
       ======================= */

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
