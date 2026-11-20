
package com.electrotrack.actividades.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "actividades")
public class Actividades {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private UUID servicioId;

    @NotNull
    @Column(nullable = false)
    private UUID tecnicoId;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String descripcion;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(nullable = false)
    private Integer porcentajeAvance;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    public Actividades() {
    }

    public Actividades(UUID id, UUID servicioId, UUID tecnicoId,
            String descripcion, Integer porcentajeAvance,
            LocalDateTime fechaRegistro) {
        this.id = id;
        this.servicioId = servicioId;
        this.tecnicoId = tecnicoId;
        this.descripcion = descripcion;
        this.porcentajeAvance = porcentajeAvance;
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

    public UUID getServicioId() {
        return servicioId;
    }

    public void setServicioId(UUID servicioId) {
        this.servicioId = servicioId;
    }

    public UUID getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(UUID tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getPorcentajeAvance() {
        return porcentajeAvance;
    }

    public void setPorcentajeAvance(Integer porcentajeAvance) {
        this.porcentajeAvance = porcentajeAvance;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
