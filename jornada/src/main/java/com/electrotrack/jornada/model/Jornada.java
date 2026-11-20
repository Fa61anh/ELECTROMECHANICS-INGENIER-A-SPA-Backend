package com.electrotrack.jornada.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jornadas")
public class Jornada {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID tecnicoId;

    @Column(nullable = false)
    private LocalDateTime fechaHoraInicio;

    private LocalDateTime fechaHoraFin;

    private Double latitudInicio;

    private Double longitudInicio;

    private Double latitudFin;

    private Double longitudFin;

    @Column(nullable = false, length = 20)
    private String estado;

    public Jornada() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(UUID tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public Double getLatitudInicio() {
        return latitudInicio;
    }

    public void setLatitudInicio(Double latitudInicio) {
        this.latitudInicio = latitudInicio;
    }

    public Double getLongitudInicio() {
        return longitudInicio;
    }

    public void setLongitudInicio(Double longitudInicio) {
        this.longitudInicio = longitudInicio;
    }

    public Double getLatitudFin() {
        return latitudFin;
    }

    public void setLatitudFin(Double latitudFin) {
        this.latitudFin = latitudFin;
    }

    public Double getLongitudFin() {
        return longitudFin;
    }

    public void setLongitudFin(Double longitudFin) {
        this.longitudFin = longitudFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
