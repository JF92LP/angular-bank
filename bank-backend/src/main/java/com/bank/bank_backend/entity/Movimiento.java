package com.bank.bank_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movimientoId;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "tipo_movimiento", nullable = false)
    private String tipoMovimiento; // "Credito" o "Debito"

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal valor; // positivo credito, negativo debito

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal saldo; // saldo disponible después del movimiento

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "cuenta_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_movimiento_cuenta")
    )
    private Cuenta cuenta;

    public Long getMovimientoId() {
        return movimientoId;
    }

    public void setMovimientoId(Long movimientoId) {
        this.movimientoId = movimientoId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }
}
