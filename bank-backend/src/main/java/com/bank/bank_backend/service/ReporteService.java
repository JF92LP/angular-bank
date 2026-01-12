package com.bank.bank_backend.service;

import com.bank.bank_backend.dto.ReporteEstadoCuentaResponse;
import com.bank.bank_backend.dto.ReporteMovimientoItem;
import com.bank.bank_backend.dto.ReporteMovimientoUsuarioItem;
import com.bank.bank_backend.entity.Cuenta;
import com.bank.bank_backend.entity.Movimiento;
import com.bank.bank_backend.exception.NotFoundException;
import com.bank.bank_backend.repository.CuentaRepository;
import com.bank.bank_backend.repository.MovimientoRepository;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@Service
public class ReporteService {

    private final CuentaRepository cuentaRepo;
    private final MovimientoRepository movRepo;

    public ReporteService(CuentaRepository cuentaRepo, MovimientoRepository movRepo) {
        this.cuentaRepo = cuentaRepo;
        this.movRepo = movRepo;
    }

    /**
     * MÉTODO ORIGINAL – no se rompe el frontend actual
     */
    public ReporteEstadoCuentaResponse estadoCuenta(
            String numeroCuenta,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        return estadoCuenta(numeroCuenta, fechaInicio, fechaFin, false);
    }

    /**
     * NUEVO – permite incluir PDF en Base64
     */
    public ReporteEstadoCuentaResponse estadoCuenta(
            String numeroCuenta,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            boolean incluirPdf
    ) {

        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new NotFoundException("Cuenta no encontrada"));

        LocalDateTime desde = fechaInicio.atStartOfDay();
        LocalDateTime hasta = fechaFin.plusDays(1).atStartOfDay().minusNanos(1);

        List<Movimiento> movimientos = movRepo
                .findByCuenta_NumeroCuentaAndFechaBetweenOrderByFechaAsc(
                        numeroCuenta, desde, hasta
                );

        List<ReporteMovimientoItem> items = movimientos.stream()
                .map(m -> new ReporteMovimientoItem(
                        m.getFecha(),
                        m.getTipoMovimiento(),
                        m.getValor(),
                        m.getSaldo()
                ))
                .toList();

        ReporteEstadoCuentaResponse resp = new ReporteEstadoCuentaResponse(
                cuenta.getNumeroCuenta(),
                cuenta.getCliente().getNombre(),
                nvl(cuenta.getSaldoActual()),
                items
        );

        // ✅ Solo genera PDF si se pide Y si hay movimientos.
        if (incluirPdf && items != null && !items.isEmpty()) {
            byte[] pdf = generarPdfEstadoCuenta(resp, fechaInicio, fechaFin);
            resp.setPdfBase64(Base64.getEncoder().encodeToString(pdf));
        }

        return resp;
    }

    /* =========================================================
       NUEVO: Para búsqueda por titular (cliente) -> listar cuentas
       Esto permite al frontend:
       - Seleccionar un cliente
       - Ver las cuentas asociadas
       - Elegir una y generar reporte / PDF
       ========================================================= */

    public List<String> cuentasPorCliente(Long clienteId) {
        // NO lanzamos excepción si no hay cuentas: el frontend simplemente no muestra botón/tabla.

        // ✅ OJO: en tu CuentaRepository que compartiste NO existe:
        // findByClienteClienteIdOrderByNumeroCuentaAsc
        // Por eso usamos el método existente y ordenamos aquí.
        List<Cuenta> cuentas = cuentaRepo.findByClienteClienteId(clienteId);

        if (cuentas == null || cuentas.isEmpty()) {
            return List.of();
        }

        return cuentas.stream()
                .sorted(Comparator.comparing(Cuenta::getNumeroCuenta, Comparator.nullsLast(String::compareTo)))
                .map(Cuenta::getNumeroCuenta)
                .toList();
    }

    /**
     * NUEVO (opcional): generar reportes de TODAS las cuentas del cliente en un rango.
     * Útil si el PDF/JSON del ejercicio requiere "por usuario".
     * - Si no hay cuentas, retorna lista vacía.
     * - Si una cuenta no tiene movimientos, igual retorna el reporte con movimientos vacíos (sin pdfBase64).
     */
    public List<ReporteEstadoCuentaResponse> estadoCuentaPorCliente(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            boolean incluirPdf
    ) {

        // ✅ Igual que arriba: usamos el repo existente
        List<Cuenta> cuentas = cuentaRepo.findByClienteClienteId(clienteId);

        if (cuentas == null || cuentas.isEmpty()) {
            return List.of();
        }

        return cuentas.stream()
                .sorted(Comparator.comparing(Cuenta::getNumeroCuenta, Comparator.nullsLast(String::compareTo)))
                .map(c -> estadoCuenta(c.getNumeroCuenta(), fechaInicio, fechaFin, incluirPdf))
                .toList();
    }

    /**
     * NUEVO: Movimientos por fechas por usuario (cliente)
     * Devuelve el JSON tipo "Listado de movimientos por usuario" (según PDF).
     *
     * Nota: requiere que exista el método en MovimientoRepository:
     * findByCuenta_Cliente_ClienteIdAndFechaBetweenOrderByFechaAsc(...)
     */
    public List<ReporteMovimientoUsuarioItem> movimientosPorCliente(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {

        LocalDateTime desde = fechaInicio.atStartOfDay();
        LocalDateTime hasta = fechaFin.plusDays(1).atStartOfDay().minusNanos(1);

        List<Movimiento> movimientos = movRepo
                .findByCuenta_Cliente_ClienteIdAndFechaBetweenOrderByFechaAsc(clienteId, desde, hasta);

        if (movimientos == null || movimientos.isEmpty()) {
            return List.of();
        }

        return movimientos.stream()
                .map(m -> {
                    Cuenta cta = m.getCuenta();
                    String nombreCliente = (cta.getCliente() != null && cta.getCliente().getNombre() != null)
                            ? cta.getCliente().getNombre()
                            : "";

                    return new ReporteMovimientoUsuarioItem(
                            m.getFecha(),
                            nombreCliente,
                            cta.getNumeroCuenta(),
                            cta.getTipoCuenta(),
                            nvl(cta.getSaldoInicial()),
                            cta.getEstado(),
                            nvl(m.getValor()),  // ya viene + o -
                            nvl(m.getSaldo())   // saldo luego del movimiento
                    );
                })
                .toList();
    }

    /* =========================================================
       ✅ NUEVO: PDF Movimientos por Cliente (para el botón del frontend)
       - Genera PDF con la tabla: Fecha, Cliente, Número, Tipo, Saldo Inicial, Movimiento, Saldo Disponible
       - Retorna bytes para que el Controller lo codifique Base64
       ========================================================= */

    public byte[] movimientosPorClientePdfBytes(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        List<ReporteMovimientoUsuarioItem> items = movimientosPorCliente(clienteId, fechaInicio, fechaFin);

        // ✅ Si no hay movimientos, devolvemos arreglo vacío (el controller manda 204)
        if (items == null || items.isEmpty()) {
            return new byte[0];
        }

        return generarPdfMovimientosPorCliente(items, fechaInicio, fechaFin);
    }

    /* ===============================
       PDF
       =============================== */

    private byte[] generarPdfEstadoCuenta(
            ReporteEstadoCuentaResponse data,
            LocalDate ini,
            LocalDate fin
    ) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font title = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font bold = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 11, Font.NORMAL);

            doc.add(new Paragraph("REPORTE – ESTADO DE CUENTA", title));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Cliente: " + safe(data.getCliente()), bold));
            doc.add(new Paragraph("Número de cuenta: " + safe(data.getNumeroCuenta()), normal));
            doc.add(new Paragraph("Saldo actual: " + data.getSaldoActual(), normal));
            doc.add(new Paragraph("Rango de fechas: " + ini + " a " + fin, normal));

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Movimientos", bold));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3.5f, 2f, 2f, 2f});

            table.addCell(headerCell("Fecha"));
            table.addCell(headerCell("Tipo"));
            table.addCell(headerCell("Valor"));
            table.addCell(headerCell("Saldo"));

            if (data.getMovimientos() != null) {
                for (ReporteMovimientoItem m : data.getMovimientos()) {
                    table.addCell(bodyCell(String.valueOf(m.getFecha())));
                    table.addCell(bodyCell(safe(m.getTipoMovimiento())));
                    table.addCell(bodyCell(String.valueOf(nvl(m.getValor()))));
                    table.addCell(bodyCell(String.valueOf(nvl(m.getSaldo()))));
                }
            }

            doc.add(table);
            doc.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF del estado de cuenta", e);
        }
    }

    private byte[] generarPdfMovimientosPorCliente(
            List<ReporteMovimientoUsuarioItem> items,
            LocalDate ini,
            LocalDate fin
    ) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36); // horizontal para que no se corte
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font title = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font bold = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 11, Font.NORMAL);

            String nombreCliente = "";
            if (items != null && !items.isEmpty()) {
                nombreCliente = safe(items.get(0).getCliente());
            }

            doc.add(new Paragraph("REPORTE – MOVIMIENTOS POR CLIENTE", title));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Cliente: " + nombreCliente, bold));
            doc.add(new Paragraph("Rango de fechas: " + ini + " a " + fin, normal));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.8f, 3.2f, 2.4f, 2.0f, 2.0f, 2.0f, 2.2f});

            table.addCell(headerCell("Fecha"));
            table.addCell(headerCell("Cliente"));
            table.addCell(headerCell("Número Cuenta"));
            table.addCell(headerCell("Tipo Cuenta"));
            table.addCell(headerCell("Saldo Inicial"));
            table.addCell(headerCell("Movimiento"));
            table.addCell(headerCell("Saldo Disponible"));

            if (items != null) {
                for (ReporteMovimientoUsuarioItem m : items) {
                    table.addCell(bodyCell(String.valueOf(m.getFecha())));
                    table.addCell(bodyCell(safe(m.getCliente())));
                    table.addCell(bodyCell(safe(m.getNumeroCuenta())));
                    table.addCell(bodyCell(safe(m.getTipo()))); // ✅ corregido para que compile y funcione
                    table.addCell(bodyCell(String.valueOf(nvl(m.getSaldoInicial()))));
                    table.addCell(bodyCell(String.valueOf(nvl(m.getMovimiento()))));
                    table.addCell(bodyCell(String.valueOf(nvl(m.getSaldoDisponible()))));
                }
            }

            doc.add(table);
            doc.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de movimientos por cliente", e);
        }
    }

    /* ===============================
       Helpers
       =============================== */

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private PdfPCell headerCell(String text) {
        Font f = new Font(Font.HELVETICA, 10, Font.BOLD);
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBackgroundColor(new java.awt.Color(230, 230, 230));
        c.setPadding(6f);
        return c;
    }

    private PdfPCell bodyCell(String text) {
        Font f = new Font(Font.HELVETICA, 10, Font.NORMAL);
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setPadding(6f);
        return c;
    }
}
