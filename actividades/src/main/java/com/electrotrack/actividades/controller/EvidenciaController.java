
package com.electrotrack.actividades.controller;

import com.electrotrack.actividades.model.Evidencia;
import com.electrotrack.actividades.service.EvidenciaService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/evidencias")
public class EvidenciaController {

    private final EvidenciaService evidenciaService;

    public EvidenciaController(EvidenciaService evidenciaService) {
        this.evidenciaService = evidenciaService;
    }

    @PostMapping(
        value = "/actividad/{actividadId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Evidencia> subirFotografia(
            @PathVariable UUID actividadId,
            @RequestParam("archivo") MultipartFile archivo) {

        Evidencia evidencia = evidenciaService.guardarEvidencia(
                actividadId, archivo
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(evidencia);
    }

    @GetMapping("/actividad/{actividadId}")
    public List<Evidencia> listarPorActividad(
            @PathVariable UUID actividadId) {

        return evidenciaService.buscarPorActividad(actividadId);
    }

    @GetMapping("/{id}")
    public Evidencia buscarPorId(@PathVariable UUID id) {
        return evidenciaService.buscarPorId(id);
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<byte[]> visualizarFotografia(
            @PathVariable UUID id) {

        Evidencia evidencia = evidenciaService.buscarPorId(id);
        byte[] fotografia = evidenciaService.obtenerFotografia(id);

        MediaType tipo = MediaType.parseMediaType(
                evidencia.getTipoArchivo()
        );

        return ResponseEntity.ok()
                .contentType(tipo)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                        evidencia.getNombreArchivo() + "\""
                )
                .body(fotografia);
    }
}
