package com.electrotrack.actividades.service;

import com.electrotrack.actividades.model.Actividades;
import com.electrotrack.actividades.repository.ActividadesRepository;

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
class ActividadesServiceImplTest {

    @Mock
    private ActividadesRepository actividadesRepository;

    @InjectMocks
    private ActividadesServiceImpl actividadesService;

    private Actividades actividad;
    private UUID actividadId;
    private UUID servicioId;
    private UUID tecnicoId;
    private LocalDateTime fechaRegistro;

    @BeforeEach
    void prepararDatos() {

        actividadId = UUID.randomUUID();
        servicioId = UUID.randomUUID();
        tecnicoId = UUID.randomUUID();

        fechaRegistro = LocalDateTime.of(
                2026, 11, 21, 10, 0
        );

        actividad = new Actividades(
                actividadId,
                servicioId,
                tecnicoId,
                "Mantencion de instalaciones electricas",
                50,
                fechaRegistro
        );
    }

    @Test
    void listarActividadesCorrectamente() {

        when(actividadesRepository.findAll())
                .thenReturn(List.of(actividad));

        List<Actividades> resultado =
                actividadesService.listarActividades();

        assertEquals(1, resultado.size());
        assertEquals(actividadId, resultado.get(0).getId());

        verify(actividadesRepository).findAll();
    }

    @Test
    void listarActividadesSinRegistros() {

        when(actividadesRepository.findAll())
                .thenReturn(List.of());

        List<Actividades> resultado =
                actividadesService.listarActividades();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(actividadesRepository).findAll();
    }

    @Test
    void buscarActividadExistente() {

        when(actividadesRepository.findById(actividadId))
                .thenReturn(Optional.of(actividad));

        Actividades resultado =
                actividadesService.buscarPorId(actividadId);

        assertEquals(actividadId, resultado.getId());
        assertEquals(servicioId, resultado.getServicioId());
        assertEquals(tecnicoId, resultado.getTecnicoId());
        assertEquals(50, resultado.getPorcentajeAvance());

        verify(actividadesRepository).findById(actividadId);
    }

    @Test
    void buscarActividadInexistente() {

        when(actividadesRepository.findById(actividadId))
                .thenReturn(Optional.empty());

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> actividadesService.buscarPorId(actividadId)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                excepcion.getStatusCode()
        );

        assertEquals(
                "Actividad no encontrada",
                excepcion.getReason()
        );
    }

    @Test
    void buscarActividadesPorServicio() {

        when(actividadesRepository.findByServicioId(servicioId))
                .thenReturn(List.of(actividad));

        List<Actividades> resultado =
                actividadesService.buscarPorServicio(servicioId);

        assertEquals(1, resultado.size());
        assertEquals(servicioId, resultado.get(0).getServicioId());

        verify(actividadesRepository)
                .findByServicioId(servicioId);
    }

    @Test
    void buscarServicioSinActividades() {

        when(actividadesRepository.findByServicioId(servicioId))
                .thenReturn(List.of());

        List<Actividades> resultado =
                actividadesService.buscarPorServicio(servicioId);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(actividadesRepository)
                .findByServicioId(servicioId);
    }

    @Test
    void registrarActividadCorrectamente() {

        simularGuardado();

        Actividades resultado =
                actividadesService.registrarActividad(actividad);

        assertNotNull(resultado);
        assertEquals(actividadId, resultado.getId());
        assertEquals(servicioId, resultado.getServicioId());
        assertEquals(tecnicoId, resultado.getTecnicoId());

        assertEquals(
                "Mantencion de instalaciones electricas",
                resultado.getDescripcion()
        );

        assertEquals(50, resultado.getPorcentajeAvance());
        assertNotNull(resultado.getFechaRegistro());

        verify(actividadesRepository).save(actividad);
    }

    @Test
    void registrarActividadConDatosNulos() {

        verificarRegistroInvalido(
                null,
                "Los datos son obligatorios"
        );
    }

    @Test
    void registrarActividadSinServicioId() {

        actividad.setServicioId(null);

        verificarRegistroInvalido(
                actividad,
                "El servicio y el tecnico son obligatorios"
        );
    }

    @Test
    void registrarActividadSinTecnicoId() {

        actividad.setTecnicoId(null);

        verificarRegistroInvalido(
                actividad,
                "El servicio y el tecnico son obligatorios"
        );
    }

    @Test
    void registrarActividadSinDescripcion() {

        actividad.setDescripcion("");

        verificarRegistroInvalido(
                actividad,
                "La descripcion es obligatoria"
        );
    }

    @Test
    void registrarActividadConDescripcionNula() {

        actividad.setDescripcion(null);

        verificarRegistroInvalido(
                actividad,
                "La descripcion es obligatoria"
        );
    }

    @Test
    void registrarActividadConDescripcionDeEspacios() {

        actividad.setDescripcion("   ");

        verificarRegistroInvalido(
                actividad,
                "La descripcion es obligatoria"
        );
    }

    @Test
    void registrarActividadConPorcentajeNulo() {

        actividad.setPorcentajeAvance(null);

        verificarRegistroInvalido(
                actividad,
                "El porcentaje debe estar entre 0 y 100"
        );
    }

    @Test
    void registrarActividadConPorcentajeNegativo() {

        actividad.setPorcentajeAvance(-1);

        verificarRegistroInvalido(
                actividad,
                "El porcentaje debe estar entre 0 y 100"
        );
    }

    @Test
    void registrarActividadConPorcentajeSuperiorA100() {

        actividad.setPorcentajeAvance(101);

        verificarRegistroInvalido(
                actividad,
                "El porcentaje debe estar entre 0 y 100"
        );
    }

    @Test
    void registrarActividadConAvanceCero() {

        actividad.setPorcentajeAvance(0);

        simularGuardado();

        Actividades resultado =
                actividadesService.registrarActividad(actividad);

        assertEquals(0, resultado.getPorcentajeAvance());

        verify(actividadesRepository).save(actividad);
    }

    @Test
    void registrarActividadConAvanceCompleto() {

        actividad.setPorcentajeAvance(100);

        simularGuardado();

        Actividades resultado =
                actividadesService.registrarActividad(actividad);

        assertEquals(100, resultado.getPorcentajeAvance());

        verify(actividadesRepository).save(actividad);
    }

    @Test
    void generarFechaRegistroAutomaticamente() {

        actividad.setFechaRegistro(null);

        LocalDateTime antes = LocalDateTime.now();

        actividad.antesDeGuardar();

        LocalDateTime despues = LocalDateTime.now();

        assertNotNull(actividad.getFechaRegistro());

        assertFalse(
                actividad.getFechaRegistro().isBefore(antes)
        );

        assertFalse(
                actividad.getFechaRegistro().isAfter(despues)
        );
    }

    @Test
    void conservarFechaRegistroExistente() {

        actividad.setFechaRegistro(fechaRegistro);

        actividad.antesDeGuardar();

        assertEquals(
                fechaRegistro,
                actividad.getFechaRegistro()
        );
    }

    private void simularGuardado() {

        when(actividadesRepository.save(any(Actividades.class)))
                .thenAnswer(invocation -> {

                    Actividades guardada =
                            invocation.getArgument(0);

                    assertNull(guardada.getId());
                    assertNull(guardada.getFechaRegistro());

                    guardada.setId(actividadId);
                    guardada.antesDeGuardar();

                    return guardada;
                });
    }

    private void verificarRegistroInvalido(
            Actividades datos,
            String mensajeEsperado
    ) {

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> actividadesService.registrarActividad(datos)
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                excepcion.getStatusCode()
        );

        assertEquals(
                mensajeEsperado,
                excepcion.getReason()
        );

        verify(actividadesRepository, never())
                .save(any(Actividades.class));
    }
}
