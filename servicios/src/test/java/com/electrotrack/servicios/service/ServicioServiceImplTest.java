package com.electrotrack.servicios.service;

import com.electrotrack.servicios.model.Servicio;
import com.electrotrack.servicios.repository.ServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioServiceImplTest {

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ServicioServiceImpl servicioService;

    private Servicio servicio;
    private UUID servicioId;
    private UUID tecnicoId;
    private LocalDateTime fechaProgramada;

    @BeforeEach
    void prepararDatos() {

        servicioId = UUID.randomUUID();
        tecnicoId = UUID.randomUUID();

        fechaProgramada = LocalDateTime.of(
                2026, 11, 21, 10, 0
        );

        servicio = new Servicio(
                servicioId,
                "Mantencion electrica",
                "Enel",
                "Reparacion de falla electrica",
                "Av. Pajaritos 1234",
                fechaProgramada,
                tecnicoId,
                "PENDIENTE"
        );
    }

    @Test
    void listarServiciosCorrectamente() {

        when(servicioRepository.findAll())
                .thenReturn(List.of(servicio));

        List<Servicio> resultado =
                servicioService.listarServicios();

        assertEquals(1, resultado.size());
        assertEquals(servicioId, resultado.get(0).getId());

        verify(servicioRepository).findAll();
    }

    @Test
    void buscarServicioExistente() {

        when(servicioRepository.findById(servicioId))
                .thenReturn(Optional.of(servicio));

        Servicio resultado =
                servicioService.buscarPorId(servicioId);

        assertEquals(servicioId, resultado.getId());
        assertEquals("Enel", resultado.getCliente());

        verify(servicioRepository).findById(servicioId);
    }

    @Test
    void buscarServicioInexistente() {

        when(servicioRepository.findById(servicioId))
                .thenReturn(Optional.empty());

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> servicioService.buscarPorId(servicioId)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                excepcion.getStatusCode()
        );
    }

    @Test
    void crearServicioCorrectamente() {

        servicio.setEstado("FINALIZADO");

        when(servicioRepository.save(any(Servicio.class)))
                .thenAnswer(invocation -> {
                    Servicio guardado = invocation.getArgument(0);
                    assertNull(guardado.getId());
                    guardado.setId(servicioId);
                    return guardado;
                });

        Servicio resultado =
                servicioService.crearServicio(servicio);

        assertNotNull(resultado);
        assertEquals(servicioId, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("Enel", resultado.getCliente());

        verify(servicioRepository).save(servicio);
    }

    @Test
    void crearServicioConDatosNulos() {

        verificarCreacionInvalida(null);
    }

    @Test
    void crearServicioSinTitulo() {

        servicio.setTitulo("");

        verificarCreacionInvalida(servicio);
    }

    @Test
    void crearServicioConTituloDemasiadoLargo() {

        servicio.setTitulo("A".repeat(151));

        verificarCreacionInvalida(servicio);
    }

    @Test
    void crearServicioSinCliente() {

        servicio.setCliente("");

        verificarCreacionInvalida(servicio);
    }

    @Test
    void crearServicioConClienteDemasiadoLargo() {

        servicio.setCliente("A".repeat(151));

        verificarCreacionInvalida(servicio);
    }

    @Test
    void crearServicioSinDescripcion() {

        servicio.setDescripcion("");

        verificarCreacionInvalida(servicio);
    }

    @Test
    void crearServicioSinDireccion() {

        servicio.setDireccion("");

        verificarCreacionInvalida(servicio);
    }

    @Test
    void crearServicioConDireccionDemasiadoLarga() {

        servicio.setDireccion("A".repeat(256));

        verificarCreacionInvalida(servicio);
    }

    @Test
    void crearServicioSinFechaProgramada() {

        servicio.setFechaProgramada(null);

        verificarCreacionInvalida(servicio);
    }

    @Test
    void crearServicioEliminandoEspacios() {

        servicio.setTitulo("  Mantencion electrica  ");
        servicio.setCliente("  Enel  ");
        servicio.setDescripcion("  Reparacion electrica  ");
        servicio.setDireccion("  Av. Pajaritos 1234  ");

        when(servicioRepository.save(any(Servicio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Servicio resultado =
                servicioService.crearServicio(servicio);

        assertEquals(
                "Mantencion electrica",
                resultado.getTitulo()
        );

        assertEquals("Enel", resultado.getCliente());

        assertEquals(
                "Reparacion electrica",
                resultado.getDescripcion()
        );

        assertEquals(
                "Av. Pajaritos 1234",
                resultado.getDireccion()
        );

        verify(servicioRepository).save(servicio);
    }

    @Test
    void asignarTecnicoCorrectamente() {

        UUID nuevoTecnicoId = UUID.randomUUID();

        when(servicioRepository.findById(servicioId))
                .thenReturn(Optional.of(servicio));

        when(servicioRepository.save(servicio))
                .thenReturn(servicio);

        Servicio resultado = servicioService.asignarTecnico(
                servicioId,
                nuevoTecnicoId
        );

        assertEquals(
                nuevoTecnicoId,
                resultado.getTecnicoId()
        );

        verify(servicioRepository).save(servicio);
    }

    @Test
    void asignarTecnicoConIdNulo() {

        when(servicioRepository.findById(servicioId))
                .thenReturn(Optional.of(servicio));

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> servicioService.asignarTecnico(
                        servicioId,
                        null
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                excepcion.getStatusCode()
        );

        verify(servicioRepository, never())
                .save(any(Servicio.class));
    }

    @Test
    void actualizarEstadoInvalido() {

        verificarEstadoInvalido(
                "CANCELADO",
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    void actualizarEstadoNulo() {

        verificarEstadoInvalido(
                null,
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    void cambiarEstadoPendienteAEnProceso() {

        when(servicioRepository.findById(servicioId))
                .thenReturn(Optional.of(servicio));

        when(servicioRepository.save(servicio))
                .thenReturn(servicio);

        Servicio resultado = servicioService.actualizarEstado(
                servicioId,
                "EN_PROCESO"
        );

        assertEquals("EN_PROCESO", resultado.getEstado());

        verify(servicioRepository).save(servicio);
    }

    @Test
    void cambiarEstadoEnProcesoAFinalizado() {

        servicio.setEstado("EN_PROCESO");

        when(servicioRepository.findById(servicioId))
                .thenReturn(Optional.of(servicio));

        when(servicioRepository.save(servicio))
                .thenReturn(servicio);

        Servicio resultado = servicioService.actualizarEstado(
                servicioId,
                "FINALIZADO"
        );

        assertEquals("FINALIZADO", resultado.getEstado());

        verify(servicioRepository).save(servicio);
    }

    @Test
    void iniciarServicioSinTecnicoAsignado() {

        servicio.setTecnicoId(null);

        verificarEstadoInvalido(
                "EN_PROCESO",
                HttpStatus.CONFLICT
        );
    }

    @Test
    void modificarEstadoDeServicioFinalizado() {

        servicio.setEstado("FINALIZADO");

        verificarEstadoInvalido(
                "EN_PROCESO",
                HttpStatus.CONFLICT
        );
    }

    @Test
    void cambiarEstadoPendienteDirectamenteAFinalizado() {

        verificarEstadoInvalido(
                "FINALIZADO",
                HttpStatus.CONFLICT
        );
    }

    @Test
    void cambiarEstadoEnProcesoAPendiente() {

        servicio.setEstado("EN_PROCESO");

        verificarEstadoInvalido(
                "PENDIENTE",
                HttpStatus.CONFLICT
        );
    }

    @Test
    void actualizarServicioCorrectamente() {

        LocalDateTime nuevaFecha =
                fechaProgramada.plusDays(1);

        Servicio nuevosDatos = new Servicio(
                UUID.randomUUID(),
                "  Revision electrica  ",
                "  Nuevo cliente  ",
                "  Revision de instalaciones  ",
                "  Nueva direccion  ",
                nuevaFecha,
                UUID.randomUUID(),
                "FINALIZADO"
        );

        when(servicioRepository.findById(servicioId))
                .thenReturn(Optional.of(servicio));

        when(servicioRepository.save(servicio))
                .thenReturn(servicio);

        Servicio resultado = servicioService.actualizarServicio(
                servicioId,
                nuevosDatos
        );

        assertEquals(
                "Revision electrica",
                resultado.getTitulo()
        );

        assertEquals(
                "Nuevo cliente",
                resultado.getCliente()
        );

        assertEquals(
                "Revision de instalaciones",
                resultado.getDescripcion()
        );

        assertEquals(
                "Nueva direccion",
                resultado.getDireccion()
        );

        assertEquals(
                nuevaFecha,
                resultado.getFechaProgramada()
        );

        assertEquals(servicioId, resultado.getId());
        assertEquals(tecnicoId, resultado.getTecnicoId());
        assertEquals("PENDIENTE", resultado.getEstado());

        verify(servicioRepository).save(servicio);
    }

    @Test
    void actualizarServicioConDatosInvalidos() {

        Servicio nuevosDatos = new Servicio();

        when(servicioRepository.findById(servicioId))
                .thenReturn(Optional.of(servicio));

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> servicioService.actualizarServicio(
                        servicioId,
                        nuevosDatos
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                excepcion.getStatusCode()
        );

        assertEquals("Enel", servicio.getCliente());

        verify(servicioRepository, never())
                .save(any(Servicio.class));
    }

    @Test
    void eliminarServicioCorrectamente() {

        when(servicioRepository.findById(servicioId))
                .thenReturn(Optional.of(servicio));

        servicioService.eliminarServicio(servicioId);

        verify(servicioRepository).delete(servicio);
    }

    private void verificarCreacionInvalida(Servicio datos) {

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> servicioService.crearServicio(datos)
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                excepcion.getStatusCode()
        );

        verify(servicioRepository, never())
                .save(any(Servicio.class));
    }

    private void verificarEstadoInvalido(
            String nuevoEstado,
            HttpStatus estadoEsperado
    ) {

        String estadoOriginal = servicio.getEstado();

        when(servicioRepository.findById(servicioId))
                .thenReturn(Optional.of(servicio));

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> servicioService.actualizarEstado(
                        servicioId,
                        nuevoEstado
                )
        );

        assertEquals(
                estadoEsperado,
                excepcion.getStatusCode()
        );

        assertEquals(
                estadoOriginal,
                servicio.getEstado()
        );

        verify(servicioRepository, never())
                .save(any(Servicio.class));
    }
}
