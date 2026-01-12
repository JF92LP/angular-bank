-- =========================================
-- BaseDatos.sql
-- Sistema: Angular Bank
-- Descripción: Script de base de datos,
-- entidades y esquema relacional
-- =========================================

-- =========================
-- Crear Base de Datos
-- =========================
CREATE DATABASE angular_bank;

-- Conectarse a la base de datos
\c angular_bank;

-- =========================
-- Tabla: clientes
-- =========================
CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(150),
    telefono VARCHAR(20),
    email VARCHAR(100),
    estado BOOLEAN NOT NULL
);

-- =========================
-- Tabla: cuentas
-- =========================
CREATE TABLE cuentas (
    id SERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(20) NOT NULL,
    saldo_inicial NUMERIC(15,2) NOT NULL,
    estado BOOLEAN NOT NULL,
    cliente_id INTEGER NOT NULL,
    CONSTRAINT fk_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id)
        ON DELETE CASCADE
);

-- =========================
-- Tabla: movimientos
-- =========================
CREATE TABLE movimientos (
    id SERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL,
    tipo_movimiento VARCHAR(30) NOT NULL,
    valor NUMERIC(15,2) NOT NULL,
    saldo NUMERIC(15,2) NOT NULL,
    cuenta_id INTEGER NOT NULL,
    CONSTRAINT fk_cuenta
        FOREIGN KEY (cuenta_id)
        REFERENCES cuentas(id)
        ON DELETE CASCADE
);

-- =========================
-- Índices recomendados
-- =========================
CREATE INDEX idx_cuentas_cliente ON cuentas(cliente_id);
CREATE INDEX idx_movimientos_cuenta ON movimientos(cuenta_id);
CREATE INDEX idx_movimientos_fecha ON movimientos(fecha);
