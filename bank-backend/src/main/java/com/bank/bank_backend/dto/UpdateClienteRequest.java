package com.bank.bank_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateClienteRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String genero;

    @NotNull
    @Min(1)
    private Integer edad;

    @NotBlank
    private String identificacion;

    @NotBlank
    private String direccion;

    @NotBlank
    private String telefono;

    @NotNull
    private Boolean estado;

    // IMPORTANTE: opcional en update
    private String contrasena;

    // getters/setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
