package com.electrotrack.actividades.service;

import com.electrotrack.actividades.model.Evidencia;
import com.electrotrack.actividades.repository.ActividadesRepository;
import com.electrotrack.actividades.repository.EvidenciaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvidenciaServiceImplTest {

    @Mock
    private EvidenciaRepository evidenciaRepository;

    @Mock
    private ActividadesRepository actividadesRepository;

    @TempDir
    Path carpetaTemporal;

    private EvidenciaServiceImpl evidenciaService;
    private Evidencia evidencia;
    private UUID evidenciaId;
    private UUID actividadId;
    private LocalDateTime fechaRegistro;

    private final byte[] contenidoJpg = {
            (byte) 0xFF,
            (byte) 0xD8,
            (byte) 0xFF,
            (byte) 0xD9
    };

    private final byte[] contenidoPng = {
            (byte) 137,
            80,
            78,
            71,
            13,
            10,
            26,
            10
    };

    @BeforeEach
    void prepararDatos() {

        evidenciaId = UUID.randomUUID();
        actividadId = UUID.randomUUID();

        fechaRegistro = LocalDateTime.of(
                2026, 11, 21, 10, 0
        );

        evidenciaService = new EvidenciaServiceImpl(
                evidenciaRepository,
                actividadesRepository,
                carpetaTemporal.toString()
        );

        evidencia = new Evidencia(
                evidenciaId,
                actividadId,
                "evidencia.jpg",
                "image/jpeg",
                carpetaTemporal.resolve("evidencia.jpg").toString(),
                fechaRegistro
        );
    }

    @Test
    void guardarFotografiaJpgCorrectamente() throws IOException {

        MockMultipartFile archivo = crearArchivoJpg(
                "evidencia.jpg"
        );

        simularActividadExistente();
        simularGuardado();

        Evidencia resultado =
                evidenciaService.guardarEvidencia(
                        actividadId,
                        archivo
                );

        assertNotNull(resultado);
        assertEquals(evidenciaId, resultado.getId());
        assertEquals(actividadId, resultado.getActividadId());
        assertEquals("evidencia.jpg", resultado.getNombreArchivo());
        assertEquals("image/jpeg", resultado.getTipoArchivo());
        assertNotNull(resultado.getFechaRegistro());

        Path ruta = Path.of(resultado.getRutaArchivo());

        assertTrue(Files.exists(ruta));
        assertTrue(ruta.startsWith(carpetaTemporal));
        assertTrue(ruta.toString().endsWith(".jpg"));

        assertArrayEquals(
                contenidoJpg,
                Files.readAllBytes(ruta)
        );

        verify(evidenciaRepository).save(any(Evidencia.class));
    }

    @Test
    void guardarFotografiaPngCorrectamente() throws IOException {

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "evidencia.png",
                "image/png",
                contenidoPng
        );

        simularActividadExistente();
        simularGuardado();

        Evidencia resultado =
                evidenciaService.guardarEvidencia(
                        actividadId,
                        archivo
                );

        assertEquals("evidencia.png", resultado.getNombreArchivo());
        assertEquals("image/png", resultado.getTipoArchivo());

        Path ruta = Path.of(resultado.getRutaArchivo());

        assertTrue(Files.exists(ruta));
        assertTrue(ruta.toString().endsWith(".png"));

        assertArrayEquals(
                contenidoPng,
                Files.readAllBytes(ruta)
        );

        verify(evidenciaRepository).save(any(Evidencia.class));
    }

    @Test
    void guardarFotografiaSinNombre() throws IOException {

        MultipartFile archivo = mock(MultipartFile.class);

        when(archivo.isEmpty()).thenReturn(false);
        when(archivo.getSize()).thenReturn(
                (long) contenidoJpg.length
        );
        when(archivo.getContentType()).thenReturn("image/jpeg");
        when(archivo.getBytes()).thenReturn(contenidoJpg);
        when(archivo.getOriginalFilename()).thenReturn(null);

        simularActividadExistente();
        simularGuardado();

        Evidencia resultado =
                evidenciaService.guardarEvidencia(
                        actividadId,
                        archivo
                );

        assertNotNull(resultado.getNombreArchivo());
        assertFalse(resultado.getNombreArchivo().isBlank());

        assertEquals(
                Path.of(resultado.getRutaArchivo())
                        .getFileName().toString(),
                resultado.getNombreArchivo()
        );
    }

    @Test
    void guardarFotografiaConNombreEnBlanco() {

        MockMultipartFile archivo = crearArchivoJpg("   ");

        simularActividadExistente();
        simularGuardado();

        Evidencia resultado =
                evidenciaService.guardarEvidencia(
                        actividadId,
                        archivo
                );

        assertFalse(resultado.getNombreArchivo().isBlank());

        assertEquals(
                Path.of(resultado.getRutaArchivo())
                        .getFileName().toString(),
                resultado.getNombreArchivo()
        );
    }

    @Test
    void guardarEvidenciaConActividadIdNulo() {

        verificarRegistroInvalido(
                null,
                crearArchivoJpg("evidencia.jpg"),
                HttpStatus.NOT_FOUND,
                "Actividad no encontrada"
        );

        verifyNoInteractions(actividadesRepository);
    }

    @Test
    void guardarEvidenciaConActividadInexistente() {

        when(actividadesRepository.existsById(actividadId))
                .thenReturn(false);

        verificarRegistroInvalido(
                actividadId,
                crearArchivoJpg("evidencia.jpg"),
                HttpStatus.NOT_FOUND,
                "Actividad no encontrada"
        );
    }

    @Test
    void guardarFotografiaNula() {

        simularActividadExistente();

        verificarRegistroInvalido(
                actividadId,
                null,
                HttpStatus.BAD_REQUEST,
                "La fotografia es obligatoria"
        );
    }

    @Test
    void guardarFotografiaVacia() {

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "evidencia.jpg",
                "image/jpeg",
                new byte[0]
        );

        simularActividadExistente();

        verificarRegistroInvalido(
                actividadId,
                archivo,
                HttpStatus.BAD_REQUEST,
                "La fotografia es obligatoria"
        );
    }

    @Test
    void guardarFotografiaSuperiorA10MB() {

        MultipartFile archivo = mock(MultipartFile.class);

        when(archivo.isEmpty()).thenReturn(false);

        when(archivo.getSize()).thenReturn(
                10L * 1024 * 1024 + 1
        );

        simularActividadExistente();

        verificarRegistroInvalido(
                actividadId,
                archivo,
                HttpStatus.PAYLOAD_TOO_LARGE,
                "La fotografia supera los 10 MB"
        );
    }

    @Test
    void guardarFotografiaDeExactamente10MB() throws IOException {

        byte[] contenido = new byte[10 * 1024 * 1024];

        contenido[0] = (byte) 0xFF;
        contenido[1] = (byte) 0xD8;
        contenido[2] = (byte) 0xFF;

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "evidencia.jpg",
                "image/jpeg",
                contenido
        );

        simularActividadExistente();
        simularGuardado();

        Evidencia resultado =
                evidenciaService.guardarEvidencia(
                        actividadId,
                        archivo
                );

        assertNotNull(resultado);

        assertEquals(
                10L * 1024 * 1024,
                Files.size(Path.of(resultado.getRutaArchivo()))
        );
    }

    @Test
    void guardarFormatoNoPermitido() {

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "documento.pdf",
                "application/pdf",
                new byte[]{1, 2, 3}
        );

        simularActividadExistente();

        verificarRegistroInvalido(
                actividadId,
                archivo,
                HttpStatus.BAD_REQUEST,
                "Solo se permiten imagenes JPG o PNG"
        );
    }

    @Test
    void guardarFotografiaSinTipoDeContenido() {

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "evidencia.jpg",
                null,
                contenidoJpg
        );

        simularActividadExistente();

        verificarRegistroInvalido(
                actividadId,
                archivo,
                HttpStatus.BAD_REQUEST,
                "Solo se permiten imagenes JPG o PNG"
        );
    }

    @Test
    void guardarJpgConContenidoInvalido() {

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "evidencia.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3, 4}
        );

        simularActividadExistente();

        verificarRegistroInvalido(
                actividadId,
                archivo,
                HttpStatus.BAD_REQUEST,
                "El archivo no es una imagen valida"
        );
    }

    @Test
    void guardarPngConContenidoInvalido() {

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "evidencia.png",
                "image/png",
                new byte[]{1, 2, 3, 4, 5, 6, 7, 8}
        );

        simularActividadExistente();

        verificarRegistroInvalido(
                actividadId,
                archivo,
                HttpStatus.BAD_REQUEST,
                "El archivo no es una imagen valida"
        );
    }

    @Test
    void guardarFotografiaConErrorDeLectura() throws IOException {

        MultipartFile archivo = mock(MultipartFile.class);

        when(archivo.isEmpty()).thenReturn(false);
        when(archivo.getSize()).thenReturn(100L);
        when(archivo.getContentType()).thenReturn("image/jpeg");

        when(archivo.getBytes()).thenThrow(
                new IOException("Error de lectura")
        );

        simularActividadExistente();

        verificarRegistroInvalido(
                actividadId,
                archivo,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No se pudo guardar la fotografia"
        );
    }

    @Test
    void eliminarArchivoCuandoFallaElRepositorio() throws IOException {

        simularActividadExistente();

        when(evidenciaRepository.save(any(Evidencia.class)))
                .thenAnswer(invocation -> {

                    Evidencia datos = invocation.getArgument(0);

                    assertTrue(
                            Files.exists(
                                    Path.of(datos.getRutaArchivo())
                            )
                    );

                    throw new RuntimeException(
                            "Error de base de datos"
                    );
                });

        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> evidenciaService.guardarEvidencia(
                        actividadId,
                        crearArchivoJpg("evidencia.jpg")
                )
        );

        assertEquals(
                "Error de base de datos",
                excepcion.getMessage()
        );

        try (Stream<Path> archivos = Files.list(carpetaTemporal)) {

            assertEquals(0L, archivos.count());
        }

        verify(evidenciaRepository).save(any(Evidencia.class));
    }

    @Test
    void buscarEvidenciasPorActividad() {

        when(evidenciaRepository.findByActividadId(actividadId))
                .thenReturn(List.of(evidencia));

        List<Evidencia> resultado =
                evidenciaService.buscarPorActividad(actividadId);

        assertEquals(1, resultado.size());

        assertEquals(
                actividadId,
                resultado.get(0).getActividadId()
        );

        verify(evidenciaRepository)
                .findByActividadId(actividadId);
    }

    @Test
    void buscarActividadSinEvidencias() {

        when(evidenciaRepository.findByActividadId(actividadId))
                .thenReturn(List.of());

        List<Evidencia> resultado =
                evidenciaService.buscarPorActividad(actividadId);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(evidenciaRepository)
                .findByActividadId(actividadId);
    }

    @Test
    void buscarEvidenciaExistente() {

        when(evidenciaRepository.findById(evidenciaId))
                .thenReturn(Optional.of(evidencia));

        Evidencia resultado =
                evidenciaService.buscarPorId(evidenciaId);

        assertEquals(evidenciaId, resultado.getId());
        assertEquals(actividadId, resultado.getActividadId());
        assertEquals("evidencia.jpg", resultado.getNombreArchivo());

        verify(evidenciaRepository).findById(evidenciaId);
    }

    @Test
    void buscarEvidenciaInexistente() {

        when(evidenciaRepository.findById(evidenciaId))
                .thenReturn(Optional.empty());

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> evidenciaService.buscarPorId(evidenciaId)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                excepcion.getStatusCode()
        );

        assertEquals(
                "Evidencia no encontrada",
                excepcion.getReason()
        );
    }

    @Test
    void obtenerFotografiaCorrectamente() throws IOException {

        Files.write(
                Path.of(evidencia.getRutaArchivo()),
                contenidoJpg
        );

        when(evidenciaRepository.findById(evidenciaId))
                .thenReturn(Optional.of(evidencia));

        byte[] resultado =
                evidenciaService.obtenerFotografia(evidenciaId);

        assertArrayEquals(contenidoJpg, resultado);

        verify(evidenciaRepository).findById(evidenciaId);
    }

    @Test
    void obtenerFotografiaNoDisponible() {

        when(evidenciaRepository.findById(evidenciaId))
                .thenReturn(Optional.of(evidencia));

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> evidenciaService.obtenerFotografia(
                        evidenciaId
                )
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                excepcion.getStatusCode()
        );

        assertEquals(
                "Fotografia no disponible",
                excepcion.getReason()
        );
    }

    @Test
    void generarFechaRegistroAutomaticamente() {

        evidencia.setFechaRegistro(null);

        LocalDateTime antes = LocalDateTime.now();

        evidencia.antesDeGuardar();

        LocalDateTime despues = LocalDateTime.now();

        assertNotNull(evidencia.getFechaRegistro());

        assertFalse(
                evidencia.getFechaRegistro().isBefore(antes)
        );

        assertFalse(
                evidencia.getFechaRegistro().isAfter(despues)
        );
    }

    @Test
    void conservarFechaRegistroExistente() {

        evidencia.setFechaRegistro(fechaRegistro);

        evidencia.antesDeGuardar();

        assertEquals(
                fechaRegistro,
                evidencia.getFechaRegistro()
        );
    }

    private MockMultipartFile crearArchivoJpg(String nombre) {

        return new MockMultipartFile(
                "archivo",
                nombre,
                "image/jpeg",
                contenidoJpg
        );
    }

    private void simularActividadExistente() {

        when(actividadesRepository.existsById(actividadId))
                .thenReturn(true);
    }

    private void simularGuardado() {

        when(evidenciaRepository.save(any(Evidencia.class)))
                .thenAnswer(invocation -> {

                    Evidencia guardada =
                            invocation.getArgument(0);

                    assertNull(guardada.getId());

                    guardada.setId(evidenciaId);

                    return guardada;
                });
    }

    private void verificarRegistroInvalido(
            UUID idActividad,
            MultipartFile archivo,
            HttpStatus estadoEsperado,
            String mensajeEsperado
    ) {

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> evidenciaService.guardarEvidencia(
                        idActividad,
                        archivo
                )
        );

        assertEquals(
                estadoEsperado,
                excepcion.getStatusCode()
        );

        assertEquals(
                mensajeEsperado,
                excepcion.getReason()
        );

        verify(evidenciaRepository, never())
                .save(any(Evidencia.class));

        assertDoesNotThrow(() -> {

            try (Stream<Path> archivos = Files.list(carpetaTemporal)) {

                assertEquals(0L, archivos.count());
            }
        });
    }
}