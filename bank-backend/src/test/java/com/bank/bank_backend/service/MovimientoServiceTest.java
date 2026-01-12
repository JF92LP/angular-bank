package com.bank.bank_backend.service;

import com.bank.bank_backend.dto.CrearMovimientoRequest;
import com.bank.bank_backend.entity.Cuenta;
import com.bank.bank_backend.entity.Movimiento;
import com.bank.bank_backend.exception.BadRequestException;
import com.bank.bank_backend.exception.NotFoundException;
import com.bank.bank_backend.repository.CuentaRepository;
import com.bank.bank_backend.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private CuentaRepository cuentaRepo;

    @Mock
    private MovimientoRepository movRepo;

    @InjectMocks
    private MovimientoService movimientoService;

    private Cuenta cuentaActiva;

    @BeforeEach
    void setUp() {
        cuentaActiva = new Cuenta();
        cuentaActiva.setCuentaId(1L);
        cuentaActiva.setNumeroCuenta("123");
        cuentaActiva.setTipoCuenta("Ahorros");
        cuentaActiva.setSaldoInicial(new BigDecimal("100.00"));
        cuentaActiva.setSaldoActual(new BigDecimal("100.00"));
        cuentaActiva.setEstado(true);
    }

    @Test
    void crear_reqNull_lanzaBadRequest() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> movimientoService.crear(null));

        assertEquals("Request inválido", ex.getMessage());
        verifyNoInteractions(cuentaRepo, movRepo);
    }

    @Test
    void crear_numeroCuentaVacio_lanzaBadRequest() {
        CrearMovimientoRequest req = mock(CrearMovimientoRequest.class);
        when(req.getNumeroCuenta()).thenReturn("   ");

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> movimientoService.crear(req));

        assertEquals("numeroCuenta es requerido", ex.getMessage());
        verifyNoInteractions(movRepo);
        verifyNoMoreInteractions(cuentaRepo);
    }

    @Test
    void crear_cuentaNoExiste_lanzaNotFound() {
        CrearMovimientoRequest req = mock(CrearMovimientoRequest.class);
        when(req.getNumeroCuenta()).thenReturn("999");
        when(cuentaRepo.findByNumeroCuenta("999")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> movimientoService.crear(req));

        assertEquals("Cuenta no encontrada", ex.getMessage());
        verify(cuentaRepo).findByNumeroCuenta("999");
        verifyNoInteractions(movRepo);
    }

    @Test
    void crear_cuentaInactiva_lanzaBadRequest() {
        CrearMovimientoRequest req = mock(CrearMovimientoRequest.class);
        when(req.getNumeroCuenta()).thenReturn("123");

        Cuenta inactiva = new Cuenta();
        inactiva.setNumeroCuenta("123");
        inactiva.setEstado(false);
        inactiva.setSaldoActual(new BigDecimal("100.00"));

        when(cuentaRepo.findByNumeroCuenta("123")).thenReturn(Optional.of(inactiva));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> movimientoService.crear(req));

        assertEquals("La cuenta está inactiva", ex.getMessage());
        verify(cuentaRepo).findByNumeroCuenta("123");
        verifyNoInteractions(movRepo);
    }

    @Test
    void crear_valorNull_lanzaBadRequest() {
        CrearMovimientoRequest req = mock(CrearMovimientoRequest.class);
        when(req.getNumeroCuenta()).thenReturn("123");
        when(req.getValor()).thenReturn(null);

        // Se necesita que la cuenta exista para llegar a la validación de valor.
        when(cuentaRepo.findByNumeroCuenta("123")).thenReturn(Optional.of(cuentaActiva));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> movimientoService.crear(req));

        assertEquals("El valor debe ser mayor a 0", ex.getMessage());
        verify(cuentaRepo).findByNumeroCuenta("123");
        verifyNoInteractions(movRepo);
    }

    @Test
    void crear_valorCeroONegativo_lanzaBadRequest() {
        CrearMovimientoRequest req = mock(CrearMovimientoRequest.class);
        when(req.getNumeroCuenta()).thenReturn("123");
        when(req.getValor()).thenReturn(BigDecimal.ZERO);

        // Se necesita que la cuenta exista para llegar a la validación de valor.
        when(cuentaRepo.findByNumeroCuenta("123")).thenReturn(Optional.of(cuentaActiva));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> movimientoService.crear(req));

        assertEquals("El valor debe ser mayor a 0", ex.getMessage());
        verify(cuentaRepo).findByNumeroCuenta("123");
        verifyNoInteractions(movRepo);
    }

    @Test
    void crear_tipoMovimientoVacio_lanzaBadRequest() {
        CrearMovimientoRequest req = mock(CrearMovimientoRequest.class);
        when(req.getNumeroCuenta()).thenReturn("123");
        when(req.getValor()).thenReturn(new BigDecimal("10.00"));
        when(req.getTipoMovimiento()).thenReturn("  ");
        when(cuentaRepo.findByNumeroCuenta("123")).thenReturn(Optional.of(cuentaActiva));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> movimientoService.crear(req));

        assertEquals("tipoMovimiento es requerido", ex.getMessage());
        verifyNoInteractions(movRepo);
    }

    @Test
    void crear_tipoMovimientoInvalido_lanzaBadRequest() {
        CrearMovimientoRequest req = mock(CrearMovimientoRequest.class);
        when(req.getNumeroCuenta()).thenReturn("123");
        when(req.getValor()).thenReturn(new BigDecimal("10.00"));
        when(req.getTipoMovimiento()).thenReturn("TRANSFERENCIA");
        when(cuentaRepo.findByNumeroCuenta("123")).thenReturn(Optional.of(cuentaActiva));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> movimientoService.crear(req));

        assertEquals("tipoMovimiento debe ser 'Credito' o 'Debito'", ex.getMessage());
        verifyNoInteractions(movRepo);
    }

    @Test
    void crear_debito_sinSaldo_lanzaBadRequestSaldoNoDisponible() {
        CrearMovimientoRequest req = mock(CrearMovimientoRequest.class);
        when(req.getNumeroCuenta()).thenReturn("123");
        when(req.getValor()).thenReturn(new BigDecimal("150.00"));
        when(req.getTipoMovimiento()).thenReturn("Debito");
        when(cuentaRepo.findByNumeroCuenta("123")).thenReturn(Optional.of(cuentaActiva));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> movimientoService.crear(req));

        assertEquals("Saldo no disponible", ex.getMessage());
        verifyNoInteractions(movRepo);
    }

    @Test
    void crear_credito_ok_creaMovimientoYActualizaSaldo() {
        CrearMovimientoRequest req = mock(CrearMovimientoRequest.class);
        when(req.getNumeroCuenta()).thenReturn("123");
        when(req.getValor()).thenReturn(new BigDecimal("50.00"));
        when(req.getTipoMovimiento()).thenReturn("Credito");
        when(cuentaRepo.findByNumeroCuenta("123")).thenReturn(Optional.of(cuentaActiva));

        when(movRepo.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cuentaRepo.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

        Movimiento result = movimientoService.crear(req);

        assertNotNull(result);
        assertEquals("Credito", result.getTipoMovimiento());
        assertEquals(new BigDecimal("50.00"), result.getValor());
        assertEquals(new BigDecimal("150.00"), result.getSaldo());
        assertEquals(new BigDecimal("150.00"), cuentaActiva.getSaldoActual());

        ArgumentCaptor<Movimiento> movCaptor = ArgumentCaptor.forClass(Movimiento.class);
        verify(movRepo).save(movCaptor.capture());
        Movimiento movGuardado = movCaptor.getValue();

        assertEquals("Credito", movGuardado.getTipoMovimiento());
        assertEquals(new BigDecimal("50.00"), movGuardado.getValor());
        assertEquals(new BigDecimal("150.00"), movGuardado.getSaldo());
        assertEquals(cuentaActiva, movGuardado.getCuenta());
        assertNotNull(movGuardado.getFecha());

        verify(cuentaRepo).save(cuentaActiva);
    }

    @Test
    void crear_debito_ok_creaMovimientoYActualizaSaldo() {
        CrearMovimientoRequest req = mock(CrearMovimientoRequest.class);
        when(req.getNumeroCuenta()).thenReturn("123");
        when(req.getValor()).thenReturn(new BigDecimal("40.00"));
        when(req.getTipoMovimiento()).thenReturn("Debito");
        when(cuentaRepo.findByNumeroCuenta("123")).thenReturn(Optional.of(cuentaActiva));

        when(movRepo.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cuentaRepo.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

        Movimiento result = movimientoService.crear(req);

        assertNotNull(result);
        assertEquals("Debito", result.getTipoMovimiento());
        assertEquals(new BigDecimal("-40.00"), result.getValor());
        assertEquals(new BigDecimal("60.00"), result.getSaldo());
        assertEquals(new BigDecimal("60.00"), cuentaActiva.getSaldoActual());

        verify(movRepo).save(any(Movimiento.class));
        verify(cuentaRepo).save(cuentaActiva);
    }
}
