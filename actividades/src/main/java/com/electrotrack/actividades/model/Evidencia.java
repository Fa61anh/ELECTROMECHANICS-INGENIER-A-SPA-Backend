
package com.electrotrack.actividades.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "evidencias")
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private UUID actividadId;

    @NotBlank
    @Column(nullable = false)
    private String nombreArchivo;

    @NotBlank
    @Column(nullable = false)
    private String tipoArchivo;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String rutaArchivo;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    public Evidencia() {
    }

    public Evidencia(UUID id, UUID actividadId, String nombreArchivo,
            String tipoArchivo, String rutaArchivo,
            LocalDateTime fechaRegistro) {
        this.id = id;
        this.actividadId = actividadId;
        this.nombreArchivo = nombreArchivo;
        this.tipoArchivo = tipoArchivo;
        this.rutaArchivo = rutaArchivo;
        this.fechaRegistro = fechaRegistro;
    }

    @PrePersist
    public void antesDeGuardar() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getActividadId() {
        return actividadId;
    }

    public void setActividadId(UUID actividadId) {
        this.actividadId = actividadId;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
