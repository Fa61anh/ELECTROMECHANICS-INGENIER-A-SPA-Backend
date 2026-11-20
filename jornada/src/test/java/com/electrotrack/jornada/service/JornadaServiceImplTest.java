package com.electrotrack.jornada.service;

import com.electrotrack.jornada.model.Jornada;
import com.electrotrack.jornada.repository.JornadaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaServiceImplTest {

    @Mock
    private JornadaRepository jornadaRepository;

    @InjectMocks
    private JornadaServiceImpl jornadaService;

    private Jornada jornada;
    private UUID jornadaId;
    private UUID tecnicoId;
    private UUID otroTecnicoId;
    private LocalDateTime fechaInicio;

    private final Double latitud = -33.5112533;
    private final Double longitud = -70.75251;

    @BeforeEach
    void prepararDatos() {

        jornadaId = UUID.randomUUID();
        tecnicoId = UUID.randomUUID();
        otroTecnicoId = UUID.randomUUID();

        fechaInicio = LocalDateTime.now().minusHours(8);

        jornada = new Jornada();

        jornada.setId(jornadaId);
        jornada.setTecnicoId(tecnicoId);
        jornada.setFechaHoraInicio(fechaInicio);
        jornada.setLatitudInicio(latitud);
        jornada.setLongitudInicio(longitud);
        jornada.setEstado("EN_CURSO");
    }

    @Test
    void iniciarJornadaCorrectamente() {

        when(jornadaRepository.findAll())
                .thenReturn(List.of());

        simularGuardado();

        LocalDateTime antes = LocalDateTime.now();

        Jornada resultado = jornadaService.iniciarJornada(
                tecnicoId,
                latitud,
                longitud
        );

        LocalDateTime despues = LocalDateTime.now();

        assertNotNull(resultado);
        assertEquals(jornadaId, resultado.getId());
        assertEquals(tecnicoId, resultado.getTecnicoId());
        assertEquals("EN_CURSO", resultado.getEstado());
        assertEquals(latitud, resultado.getLatitudInicio());
        assertEquals(longitud, resultado.getLongitudInicio());

        assertNotNull(resultado.getFechaHoraInicio());

        assertFalse(
                resultado.getFechaHoraInicio().isBefore(antes)
        );

        assertFalse(
                resultado.getFechaHoraInicio().isAfter(despues)
        );

        assertNull(resultado.getFechaHoraFin());
        assertNull(resultado.getLatitudFin());
        assertNull(resultado.getLongitudFin());

        verify(jornadaRepository).findAll();
        verify(jornadaRepository).save(any(Jornada.class));
    }

    @Test
    void iniciarJornadaSinTecnicoId() {

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> jornadaService.iniciarJornada(
                        null,
                        latitud,
                        longitud
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                excepcion.getStatusCode()
        );

        assertEquals(
                "El ID del técnico es obligatorio",
                excepcion.getReason()
        );

        verifyNoInteractions(jornadaRepository);
    }

    @Test
    void impedirJornadaActivaDuplicada() {

        when(jornadaRepository.findAll())
                .thenReturn(List.of(jornada));

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> jornadaService.iniciarJornada(
                        tecnicoId,
                        latitud,
                        longitud
                )
        );

        assertEquals(
                HttpStatus.CONFLICT,
                excepcion.getStatusCode()
        );

        assertEquals(
                "El técnico ya tiene una jornada activa",
                excepcion.getReason()
        );

        verify(jornadaRepository, never())
                .save(any(Jornada.class));
    }

    @Test
    void iniciarJornadaConRegistrosPreviosPermitidos() {

        jornada.setEstado("FINALIZADA");

        Jornada jornadaOtroTecnico = new Jornada();

        jornadaOtroTecnico.setId(UUID.randomUUID());
        jornadaOtroTecnico.setTecnicoId(otroTecnicoId);
        jornadaOtroTecnico.setEstado("EN_CURSO");

        when(jornadaRepository.findAll())
                .thenReturn(
                        List.of(jornada, jornadaOtroTecnico)
                );

        simularGuardado();

        Jornada resultado = jornadaService.iniciarJornada(
                tecnicoId,
                latitud,
                longitud
        );

        assertEquals(tecnicoId, resultado.getTecnicoId());
        assertEquals("EN_CURSO", resultado.getEstado());
        assertNotSame(jornada, resultado);

        verify(jornadaRepository).save(any(Jornada.class));
    }

    @Test
    void finalizarJornadaCorrectamente() {

        Double latitudSalida = -33.5200;
        Double longitudSalida = -70.7600;

        when(jornadaRepository.findById(jornadaId))
                .thenReturn(Optional.of(jornada));

        when(jornadaRepository.save(jornada))
                .thenReturn(jornada);

        LocalDateTime antes = LocalDateTime.now();

        Jornada resultado = jornadaService.finalizarJornada(
                jornadaId,
                latitudSalida,
                longitudSalida
        );

        LocalDateTime despues = LocalDateTime.now();

        assertEquals(jornadaId, resultado.getId());
        assertEquals(tecnicoId, resultado.getTecnicoId());
        assertEquals("FINALIZADA", resultado.getEstado());

        assertEquals(
                fechaInicio,
                resultado.getFechaHoraInicio()
        );

        assertEquals(latitud, resultado.getLatitudInicio());
        assertEquals(longitud, resultado.getLongitudInicio());

        assertEquals(
                latitudSalida,
                resultado.getLatitudFin()
        );

        assertEquals(
                longitudSalida,
                resultado.getLongitudFin()
        );

        assertNotNull(resultado.getFechaHoraFin());

        assertFalse(
                resultado.getFechaHoraFin().isBefore(antes)
        );

        assertFalse(
                resultado.getFechaHoraFin().isAfter(despues)
        );

        assertTrue(
                resultado.getFechaHoraFin()
                        .isAfter(resultado.getFechaHoraInicio())
        );

        verify(jornadaRepository).findById(jornadaId);
        verify(jornadaRepository).save(jornada);
    }

    @Test
    void finalizarJornadaInexistente() {

        when(jornadaRepository.findById(jornadaId))
                .thenReturn(Optional.empty());

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> jornadaService.finalizarJornada(
                        jornadaId,
                        latitud,
                        longitud
                )
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                excepcion.getStatusCode()
        );

        assertEquals(
                "Jornada no encontrada",
                excepcion.getReason()
        );

        verify(jornadaRepository, never())
                .save(any(Jornada.class));
    }

    @Test
    void impedirFinalizarJornadaYaTerminada() {

        jornada.setEstado("FINALIZADA");

        when(jornadaRepository.findById(jornadaId))
                .thenReturn(Optional.of(jornada));

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> jornadaService.finalizarJornada(
                        jornadaId,
                        latitud,
                        longitud
                )
        );

        assertEquals(
                HttpStatus.CONFLICT,
                excepcion.getStatusCode()
        );

        assertEquals(
                "La jornada ya está finalizada",
                excepcion.getReason()
        );

        verify(jornadaRepository, never())
                .save(any(Jornada.class));
    }

    @ParameterizedTest
    @MethodSource("coordenadasInvalidas")
    void iniciarJornadaConUbicacionInvalida(
            Double latitudInvalida,
            Double longitudInvalida
    ) {

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> jornadaService.iniciarJornada(
                        tecnicoId,
                        latitudInvalida,
                        longitudInvalida
                )
        );

        verificarErrorUbicacion(excepcion);

        verifyNoInteractions(jornadaRepository);
    }

    @ParameterizedTest
    @MethodSource("coordenadasInvalidas")
    void finalizarJornadaConUbicacionInvalida(
            Double latitudInvalida,
            Double longitudInvalida
    ) {

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> jornadaService.finalizarJornada(
                        jornadaId,
                        latitudInvalida,
                        longitudInvalida
                )
        );

        verificarErrorUbicacion(excepcion);

        verifyNoInteractions(jornadaRepository);
    }

    static Stream<Arguments> coordenadasInvalidas() {

        return Stream.of(
                Arguments.of(null, -70.75),
                Arguments.of(-33.51, null),
                Arguments.of(Double.NaN, -70.75),
                Arguments.of(-33.51, Double.NaN),
                Arguments.of(Double.POSITIVE_INFINITY, -70.75),
                Arguments.of(-33.51, Double.NEGATIVE_INFINITY),
                Arguments.of(90.1, -70.75),
                Arguments.of(-90.1, -70.75),
                Arguments.of(-33.51, 180.1),
                Arguments.of(-33.51, -180.1)
        );
    }

    @Test
    void iniciarJornadaConLimitesMinimosGPS() {

        when(jornadaRepository.findAll())
                .thenReturn(List.of());

        simularGuardado();

        Jornada resultado = jornadaService.iniciarJornada(
                tecnicoId,
                -90.0,
                -180.0
        );

        assertEquals(-90.0, resultado.getLatitudInicio());
        assertEquals(-180.0, resultado.getLongitudInicio());
        assertEquals("EN_CURSO", resultado.getEstado());

        verify(jornadaRepository).save(any(Jornada.class));
    }

    @Test
    void finalizarJornadaConLimitesMaximosGPS() {

        when(jornadaRepository.findById(jornadaId))
                .thenReturn(Optional.of(jornada));

        when(jornadaRepository.save(jornada))
                .thenReturn(jornada);

        Jornada resultado = jornadaService.finalizarJornada(
                jornadaId,
                90.0,
                180.0
        );

        assertEquals(90.0, resultado.getLatitudFin());
        assertEquals(180.0, resultado.getLongitudFin());
        assertEquals("FINALIZADA", resultado.getEstado());

        verify(jornadaRepository).save(jornada);
    }

    @Test
    void listarJornadasCorrectamente() {

        when(jornadaRepository.findAll())
                .thenReturn(List.of(jornada));

        List<Jornada> resultado =
                jornadaService.listarJornadas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(jornadaId, resultado.get(0).getId());

        verify(jornadaRepository).findAll();
    }

    @Test
    void listarJornadasSinRegistros() {

        when(jornadaRepository.findAll())
                .thenReturn(List.of());

        List<Jornada> resultado =
                jornadaService.listarJornadas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(jornadaRepository).findAll();
    }

    @Test
    void listarJornadasPorTecnicoCorrectamente() {

        Jornada otraJornada = new Jornada();

        otraJornada.setId(UUID.randomUUID());
        otraJornada.setTecnicoId(otroTecnicoId);
        otraJornada.setEstado("EN_CURSO");

        when(jornadaRepository.findAll())
                .thenReturn(List.of(jornada, otraJornada));

        List<Jornada> resultado =
                jornadaService.listarPorTecnico(tecnicoId);

        assertEquals(1, resultado.size());
        assertEquals(tecnicoId, resultado.get(0).getTecnicoId());
        assertEquals(jornadaId, resultado.get(0).getId());

        verify(jornadaRepository).findAll();
    }

    @Test
    void listarTecnicoSinJornadas() {

        when(jornadaRepository.findAll())
                .thenReturn(List.of(jornada));

        List<Jornada> resultado =
                jornadaService.listarPorTecnico(otroTecnicoId);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(jornadaRepository).findAll();
    }

    @Test
    void buscarJornadaExistente() {

        when(jornadaRepository.findById(jornadaId))
                .thenReturn(Optional.of(jornada));

        Jornada resultado =
                jornadaService.buscarPorId(jornadaId);

        assertNotNull(resultado);
        assertEquals(jornadaId, resultado.getId());
        assertEquals(tecnicoId, resultado.getTecnicoId());
        assertEquals("EN_CURSO", resultado.getEstado());
        assertEquals(fechaInicio, resultado.getFechaHoraInicio());

        verify(jornadaRepository).findById(jornadaId);
    }

    @Test
    void buscarJornadaInexistente() {

        when(jornadaRepository.findById(jornadaId))
                .thenReturn(Optional.empty());

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> jornadaService.buscarPorId(jornadaId)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                excepcion.getStatusCode()
        );

        assertEquals(
                "Jornada no encontrada",
                excepcion.getReason()
        );

        verify(jornadaRepository).findById(jornadaId);
    }

    private void simularGuardado() {

        when(jornadaRepository.save(any(Jornada.class)))
                .thenAnswer(invocation -> {

                    Jornada guardada =
                            invocation.getArgument(0);

                    assertNull(guardada.getId());

                    guardada.setId(jornadaId);

                    return guardada;
                });
    }

    private void verificarErrorUbicacion(
            ResponseStatusException excepcion
    ) {

        assertEquals(
                HttpStatus.BAD_REQUEST,
                excepcion.getStatusCode()
        );

        assertEquals(
                "Ubicación GPS inválida",
                excepcion.getReason()
        );
    }
}