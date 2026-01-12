package com.bank.bank_backend.controller;

import com.bank.bank_backend.exception.BadRequestException;
import com.bank.bank_backend.exception.GlobalExceptionHandler;
import com.bank.bank_backend.entity.Movimiento;
import com.bank.bank_backend.repository.MovimientoRepository;
import com.bank.bank_backend.service.MovimientoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MovimientoController.class)
@Import(GlobalExceptionHandler.class)
class MovimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovimientoService movimientoService;

    @MockBean
    private MovimientoRepository movimientoRepository;

    @Test
    void crearMovimiento_credito_ok() throws Exception {
        when(movimientoService.crear(any())).thenReturn(new Movimiento());

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void crearMovimiento_sinSaldo_retorna400() throws Exception {
        when(movimientoService.crear(any()))
                .thenThrow(new BadRequestException("Saldo no disponible"));

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));
    }
}
