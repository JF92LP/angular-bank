package com.bank.bank_backend.controller;


import com.bank.bank_backend.dto.ReporteEstadoCuentaResponse;
import com.bank.bank_backend.exception.GlobalExceptionHandler;
import com.bank.bank_backend.exception.NotFoundException;
import com.bank.bank_backend.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReportesController.class)
@Import(GlobalExceptionHandler.class)
class ReportesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Test
    void estadoCuenta_ok_retorna200() throws Exception {
        // Dado
        ReporteEstadoCuentaResponse resp = new ReporteEstadoCuentaResponse(
                "123",
                "Juan Perez",
                null,
                List.of()
        );

        when(reporteService.estadoCuenta(eq("123"), any(), any(), eq(false)))
                .thenReturn(resp);

        // Cuando / Entonces
        mockMvc.perform(get("/reportes/estado-cuenta")
                        .param("numeroCuenta", "123")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31")
                        .param("incluirPdf", "false"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numeroCuenta").value("123"))
                .andExpect(jsonPath("$.cliente").value("Juan Perez"));
    }

    @Test
    void estadoCuenta_cuentaNoExiste_retorna404() throws Exception {
        when(reporteService.estadoCuenta(eq("999"), any(), any(), anyBoolean()))
                .thenThrow(new NotFoundException("Cuenta no encontrada"));

        mockMvc.perform(get("/reportes/estado-cuenta")
                        .param("numeroCuenta", "999")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cuenta no encontrada"));
    }

    @Test
    void cuentasPorCliente_ok_retorna200Lista() throws Exception {
        when(reporteService.cuentasPorCliente(10L)).thenReturn(List.of("001", "002"));

        mockMvc.perform(get("/reportes/cuentas-por-cliente")
                        .param("clienteId", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("001"))
                .andExpect(jsonPath("$[1]").value("002"));
    }

    @Test
    void estadoCuentaPorCliente_ok_retorna200Lista() throws Exception {
        ReporteEstadoCuentaResponse r1 = new ReporteEstadoCuentaResponse("001", "Juan", null, List.of());
        ReporteEstadoCuentaResponse r2 = new ReporteEstadoCuentaResponse("002", "Juan", null, List.of());

        when(reporteService.estadoCuentaPorCliente(eq(10L), any(), any(), eq(false)))
                .thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/reportes/estado-cuenta-por-cliente")
                        .param("clienteId", "10")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31")
                        .param("incluirPdf", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroCuenta").value("001"))
                .andExpect(jsonPath("$[1].numeroCuenta").value("002"));
    }

    @Test
    void movimientosPorCliente_ok_retorna200Lista() throws Exception {
        when(reporteService.movimientosPorCliente(eq(10L), any(), any()))
                .thenReturn(List.of()); // aquí puedes poner items si quieres validar shape

        mockMvc.perform(get("/reportes/movimientos-por-cliente")
                        .param("clienteId", "10")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void movimientosPorClientePdf_sinMovimientos_retorna204() throws Exception {
        when(reporteService.movimientosPorClientePdfBytes(eq(10L), any(), any()))
                .thenReturn(new byte[0]);

        mockMvc.perform(get("/reportes/movimientos-por-cliente/pdf")
                        .param("clienteId", "10")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31"))
                .andExpect(status().isNoContent());
    }

    @Test
    void movimientosPorClientePdf_conMovimientos_retorna200ConPdfBase64() throws Exception {
        byte[] pdfFake = "PDF".getBytes(StandardCharsets.UTF_8);

        when(reporteService.movimientosPorClientePdfBytes(eq(10L), any(), any()))
                .thenReturn(pdfFake);

        mockMvc.perform(get("/reportes/movimientos-por-cliente/pdf")
                        .param("clienteId", "10")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pdfBase64").isNotEmpty());
    }
}
