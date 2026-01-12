package com.bank.bank_backend.service;

import com.bank.bank_backend.dto.UpdateClienteRequest;
import com.bank.bank_backend.dto.CrearClienteRequest;
import com.bank.bank_backend.entity.Cliente;
import com.bank.bank_backend.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> listar() {
        return repo.findAll();
    }

    // =========================
    // CREAR (con contraseña obligatoria)
    // =========================
    public Cliente crear(CrearClienteRequest req) {
        String identificacion = safeTrim(req.getIdentificacion());

        if (repo.existsByIdentificacion(identificacion)) {
            throw new RuntimeException("Identificación ya registrada");
        }

        Cliente c = new Cliente();
        c.setNombre(safeTrim(req.getNombre()));
        c.setGenero(safeTrim(req.getGenero()));
        c.setEdad(req.getEdad());
        c.setIdentificacion(identificacion);
        c.setDireccion(safeTrim(req.getDireccion()));
        c.setTelefono(safeTrim(req.getTelefono()));
        c.setContrasena(safeTrim(req.getContrasena())); // obligatorio en create
        c.setEstado(req.getEstado());

        return repo.save(c);
    }

    // =========================
    // ACTUALIZAR (con contraseña opcional)
    // =========================
    public Cliente actualizar(Long id, UpdateClienteRequest req) {
        Cliente existente = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no existe"));

        String nuevaIdentificacion = safeTrim(req.getIdentificacion());

        // Validación: identificación única, excluyendo al propio cliente
        String identificacionActual = existente.getIdentificacion();
        boolean cambioIdentificacion = identificacionActual == null
                || !identificacionActual.equalsIgnoreCase(nuevaIdentificacion);

        if (cambioIdentificacion && repo.existsByIdentificacion(nuevaIdentificacion)) {
            throw new RuntimeException("Identificación ya registrada");
        }

        existente.setNombre(safeTrim(req.getNombre()));
        existente.setGenero(safeTrim(req.getGenero()));
        existente.setEdad(req.getEdad());
        existente.setIdentificacion(nuevaIdentificacion);
        existente.setDireccion(safeTrim(req.getDireccion()));
        existente.setTelefono(safeTrim(req.getTelefono()));
        existente.setEstado(req.getEstado());

        // Clave: no sobreescribir contraseña si viene null/vacía
        if (req.getContrasena() != null && !req.getContrasena().trim().isEmpty()) {
            existente.setContrasena(safeTrim(req.getContrasena()));
        }

        return repo.save(existente);
    }

    public void eliminar(Long id) {
        // opcional: validar existencia para error más claro
        if (!repo.existsById(id)) {
            throw new RuntimeException("Cliente no existe");
        }
        repo.deleteById(id);
    }

    private String safeTrim(String s) {
        return s == null ? null : s.trim();
    }
}
