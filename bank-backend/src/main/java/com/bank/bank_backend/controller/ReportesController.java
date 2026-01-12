package com.bank.bank_backend.controller;

import com.bank.bank_backend.dto.ReporteEstadoCuentaResponse;
import com.bank.bank_backend.dto.ReporteMovimientoUsuarioItem;
import com.bank.bank_backend.service.ReporteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportesController {

    private final ReporteService reporteService;

    public ReportesController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * Mantiene compatibilidad con tu frontend actual:
     * - numeroCuenta, fechaInicio, fechaFin (YYYY-MM-DD)
     *
     * Nuevo:
     * - incluirPdf=true -> devuelve pdfBase64 en el JSON
     */
    @GetMapping("/estado-cuenta")
    public ReporteEstadoCuentaResponse estadoCuenta(
            @RequestParam String numeroCuenta,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(required = false, defaultValue = "false") boolean incluirPdf
    ) {
        LocalDate ini = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);

        return reporteService.estadoCuenta(numeroCuenta, ini, fin, incluirPdf);
    }

    /**
     * NUEVO: Listar cuentas por cliente (titular)
     */
    @GetMapping("/cuentas-por-cliente")
    public List<String> cuentasPorCliente(@RequestParam Long clienteId) {
        return reporteService.cuentasPorCliente(clienteId);
    }

    /**
     * NUEVO (opcional): Estado de cuenta por cliente (todas sus cuentas) + rango
     */
    @GetMapping("/estado-cuenta-por-cliente")
    public List<ReporteEstadoCuentaResponse> estadoCuentaPorCliente(
            @RequestParam Long clienteId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(required = false, defaultValue = "false") boolean incluirPdf
    ) {
        LocalDate ini = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);

        return reporteService.estadoCuentaPorCliente(clienteId, ini, fin, incluirPdf);
    }

    /**
     * Movimientos por cliente + rango -> JSON
     */
    @GetMapping("/movimientos-por-cliente")
    public List<ReporteMovimientoUsuarioItem> movimientosPorCliente(
            @RequestParam Long clienteId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin
    ) {
        LocalDate ini = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);

        return reporteService.movimientosPorCliente(clienteId, ini, fin);
    }

    /**
     * ✅ NUEVO: Movimientos por cliente + rango -> PDF Base64
     *
     * Devuelve:
     * - 200 + { pdfBase64: "..." } si hay movimientos
     * - 204 No Content si no hay movimientos en ese rango
     */
    @GetMapping("/movimientos-por-cliente/pdf")
    public ResponseEntity<Map<String, String>> movimientosPorClientePdf(
            @RequestParam Long clienteId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin
    ) {
        LocalDate ini = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);

        byte[] pdfBytes = reporteService.movimientosPorClientePdfBytes(clienteId, ini, fin);

        if (pdfBytes == null || pdfBytes.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        String base64 = Base64.getEncoder().encodeToString(pdfBytes);

        Map<String, String> resp = new HashMap<>();
        resp.put("pdfBase64", base64);

        return ResponseEntity.ok(resp);
    }
}
