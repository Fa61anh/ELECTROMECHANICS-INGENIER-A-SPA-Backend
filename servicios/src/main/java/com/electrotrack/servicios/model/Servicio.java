package com.electrotrack.servicios.model;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "servicios", schema = "servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 150)
    private String cliente;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(name = "tecnico_id")
    private UUID tecnicoId;

    @Column(nullable = false, length = 30)
    private String estado;

    public Servicio() {
    }

    public Servicio(UUID id, String titulo, String descripcion,
                    String direccion, UUID tecnicoId, String estado) {

        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.tecnicoId = tecnicoId;
        this.estado = estado;
    }

    public Servicio(UUID id, String titulo, String cliente,
                    String descripcion, String direccion,
                    LocalDateTime fechaProgramada,
                    UUID tecnicoId, String estado) {

        this.id = id;
        this.titulo = titulo;
        this.cliente = cliente;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.fechaProgramada = fechaProgramada;
        this.tecnicoId = tecnicoId;
        this.estado = estado;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public UUID getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(UUID tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}