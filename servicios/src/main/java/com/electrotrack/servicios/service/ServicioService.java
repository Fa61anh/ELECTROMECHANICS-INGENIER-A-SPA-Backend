package com.electrotrack.servicios.service;

import com.electrotrack.servicios.model.Servicio;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ServicioService {

    private final List<Servicio> servicios = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public synchronized List<Servicio> listarServicios() {
        return new ArrayList<>(servicios);
    }

    public synchronized Servicio buscarPorId(Long id) {

        for (Servicio servicio : servicios) {
            if (servicio.getId().equals(id)) {
                return servicio;
            }
        }

        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Servicio no encontrado"
        );
    }

    public synchronized Servicio crearServicio(Servicio servicio) {

        if (servicio == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Los datos del servicio son obligatorios"
            );
        }

        if (servicio.getTitulo() == null ||
            servicio.getTitulo().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El titulo es obligatorio"
            );
        }

        if (servicio.getDescripcion() == null ||
            servicio.getDescripcion().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "La descripcion es obligatoria"
            );
        }

        if (servicio.getDireccion() == null ||
            servicio.getDireccion().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "La direccion es obligatoria"
            );
        }

        servicio.setId(contador.getAndIncrement());
        servicio.setEstado("PENDIENTE");

        servicios.add(servicio);

        return servicio;
    }

    public synchronized Servicio asignarTecnico(Long id, Long tecnicoId) {

        Servicio servicio = buscarPorId(id);

        if (tecnicoId == null || tecnicoId <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El ID del tecnico no es valido"
            );
        }

        servicio.setTecnicoId(tecnicoId);

        return servicio;
    }
    
    public synchronized Servicio actualizarEstado(Long id, String estado) {

        Servicio servicio = buscarPorId(id);

        if (estado == null ||
            (!estado.equals("PENDIENTE") &&
            !estado.equals("EN_PROCESO") &&
            !estado.equals("FINALIZADO"))) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Estado no valido"
            );
        }

        String estadoActual = servicio.getEstado();

        if (estadoActual.equals("FINALIZADO")) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "No se puede modificar un servicio finalizado"
            );
        }

        if (estadoActual.equals("PENDIENTE") &&
            !estado.equals("EN_PROCESO")) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Un servicio pendiente solo puede pasar a EN_PROCESO"
            );
        }

        if (estadoActual.equals("EN_PROCESO") &&
            !estado.equals("FINALIZADO")) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Un servicio en proceso solo puede pasar a FINALIZADO"
            );
        }

        if (estado.equals("EN_PROCESO") &&
            servicio.getTecnicoId() == null) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Debe asignar un tecnico antes de iniciar el servicio"
            );
        }

        servicio.setEstado(estado);

        return servicio;
    }

    public synchronized Servicio actualizarServicio(Long id, Servicio datos) {

        Servicio servicio = buscarPorId(id);

        if (datos == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Los datos son obligatorios"
            );
        }

        if (datos.getTitulo() == null || datos.getTitulo().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El titulo es obligatorio"
            );
        }

        if (datos.getDescripcion() == null || datos.getDescripcion().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "La descripcion es obligatoria"
            );
        }

        if (datos.getDireccion() == null || datos.getDireccion().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "La direccion es obligatoria"
            );
        }

        servicio.setTitulo(datos.getTitulo());
        servicio.setDescripcion(datos.getDescripcion());
        servicio.setDireccion(datos.getDireccion());

        return servicio;
    }
    
    public synchronized void eliminarServicio(Long id) {

        Servicio servicio = buscarPorId(id);

        servicios.remove(servicio);
    }
}
