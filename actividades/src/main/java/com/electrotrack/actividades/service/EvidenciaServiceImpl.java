
package com.electrotrack.actividades.service;

import com.electrotrack.actividades.model.Evidencia;
import com.electrotrack.actividades.repository.EvidenciaRepository;
import com.electrotrack.actividades.repository.ActividadesRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EvidenciaServiceImpl implements EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;
    private final ActividadesRepository actividadesRepository;
    private final Path carpetaEvidencias;

    public EvidenciaServiceImpl(
            EvidenciaRepository evidenciaRepository,
            ActividadesRepository actividadesRepository,
            @Value("${electrotrack.evidencias.directorio:uploads/evidencias}")
            String directorio) {

        this.evidenciaRepository = evidenciaRepository;
        this.actividadesRepository = actividadesRepository;

        this.carpetaEvidencias = Paths.get(directorio)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(carpetaEvidencias);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo crear la carpeta de evidencias", e
            );
        }
    }

    @Override
    public Evidencia guardarEvidencia(
            UUID actividadId, MultipartFile archivo) {

        if (actividadId == null ||
                !actividadesRepository.existsById(actividadId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Actividad no encontrada"
            );
        }

        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fotografia es obligatoria"
            );
        }

        if (archivo.getSize() > 10 * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "La fotografia supera los 10 MB"
            );
        }

        String tipo = archivo.getContentType();
        String extension;

        if ("image/jpeg".equals(tipo)) {
            extension = ".jpg";
        } else if ("image/png".equals(tipo)) {
            extension = ".png";
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se permiten imagenes JPG o PNG"
            );
        }

        String nombreGuardado = UUID.randomUUID() + extension;
        Path destino = carpetaEvidencias.resolve(nombreGuardado);

        try {
            byte[] contenido = archivo.getBytes();

            boolean imagenValida =
                    extension.equals(".jpg")
                    && contenido.length >= 3
                    && (contenido[0] & 0xFF) == 0xFF
                    && (contenido[1] & 0xFF) == 0xD8
                    && (contenido[2] & 0xFF) == 0xFF;

            imagenValida = imagenValida ||
                    (extension.equals(".png")
                    && contenido.length >= 8
                    && (contenido[0] & 0xFF) == 137
                    && (contenido[1] & 0xFF) == 80
                    && (contenido[2] & 0xFF) == 78
                    && (contenido[3] & 0xFF) == 71
                    && (contenido[4] & 0xFF) == 13
                    && (contenido[5] & 0xFF) == 10
                    && (contenido[6] & 0xFF) == 26
                    && (contenido[7] & 0xFF) == 10);

            if (!imagenValida) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El archivo no es una imagen valida"
                );
            }

            Files.write(
                    destino,
                    contenido,
                    StandardOpenOption.CREATE_NEW
            );

            String nombreOriginal = archivo.getOriginalFilename();

            if (nombreOriginal == null || nombreOriginal.isBlank()) {
                nombreOriginal = nombreGuardado;
            }

            Evidencia evidencia = new Evidencia(
                    null,
                    actividadId,
                    nombreOriginal,
                    tipo,
                    destino.toString(),
                    LocalDateTime.now()
            );

            try {
                return evidenciaRepository.save(evidencia);
            } catch (RuntimeException e) {
                Files.deleteIfExists(destino);
                throw e;
            }

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo guardar la fotografia"
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evidencia> buscarPorActividad(UUID actividadId) {
        return evidenciaRepository.findByActividadId(actividadId);
    }

    @Override
    @Transactional(readOnly = true)
    public Evidencia buscarPorId(UUID id) {
        return evidenciaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evidencia no encontrada"
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] obtenerFotografia(UUID id) {

        Evidencia evidencia = buscarPorId(id);

        try {
            return Files.readAllBytes(
                    Paths.get(evidencia.getRutaArchivo())
            );
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Fotografia no disponible"
            );
        }
    }
}
