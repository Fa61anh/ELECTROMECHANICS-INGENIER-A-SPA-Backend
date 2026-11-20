
package com.electrotrack.actividades.service;

import com.electrotrack.actividades.model.Evidencia;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EvidenciaService {

    Evidencia guardarEvidencia(UUID actividadId, MultipartFile archivo);

    List<Evidencia> buscarPorActividad(UUID actividadId);

    Evidencia buscarPorId(UUID id);

    byte[] obtenerFotografia(UUID id);
}
