package com.electrotrack.servicios.service;

import com.electrotrack.servicios.model.Servicio;
import com.electrotrack.servicios.repository.ServicioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioServiceImpl(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Servicio> listarServicios() {
        return servicioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Servicio buscarPorId(UUID id) {
        return servicioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Servicio no encontrado"
            ));
    }

    @Override
    public Servicio crearServicio(Servicio servicio) {

        validarDatos(servicio);

        servicio.setId(null);
        servicio.setTitulo(servicio.getTitulo().trim());
        servicio.setCliente(servicio.getCliente().trim());
        servicio.setDescripcion(servicio.getDescripcion().trim());
        servicio.setDireccion(servicio.getDireccion().trim());
        servicio.setEstado("PENDIENTE");

        return servicioRepository.save(servicio);
    }

    @Override
    public Servicio asignarTecnico(UUID id, UUID tecnicoId) {

        Servicio servicio = buscarPorId(id);

        if (tecnicoId == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El ID del tecnico es obligatorio"
            );
        }

        servicio.setTecnicoId(tecnicoId);

        return servicioRepository.save(servicio);
    }

    @Override
    public Servicio actualizarEstado(UUID id, String estado) {

        Servicio servicio = buscarPorId(id);

        if (estado == null ||
            (!estado.equals("PENDIENTE") &&
             !estado.equals("EN_PROCESO") &&
             !estado.equals("FINALIZADO"))) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Estado no valido"
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

        return servicioRepository.save(servicio);
    }

    @Override
    public Servicio actualizarServicio(UUID id, Servicio datos) {

        Servicio servicio = buscarPorId(id);

        validarDatos(datos);

        servicio.setTitulo(datos.getTitulo().trim());
        servicio.setCliente(datos.getCliente().trim());
        servicio.setDescripcion(datos.getDescripcion().trim());
        servicio.setDireccion(datos.getDireccion().trim());
        servicio.setFechaProgramada(datos.getFechaProgramada());

        return servicioRepository.save(servicio);
    }

    @Override
    public void eliminarServicio(UUID id) {

        Servicio servicio = buscarPorId(id);

        servicioRepository.delete(servicio);
    }

    private void validarDatos(Servicio servicio) {

        if (servicio == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Los datos del servicio son obligatorios"
            );
        }

        if (servicio.getTitulo() == null ||
            servicio.getTitulo().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El titulo es obligatorio"
            );
        }

        if (servicio.getTitulo().trim().length() > 150) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El titulo no puede superar 150 caracteres"
            );
        }

        if (servicio.getCliente() == null ||
            servicio.getCliente().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El nombre del cliente es obligatorio"
            );
        }

        if (servicio.getCliente().trim().length() > 150) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El nombre del cliente no puede superar 150 caracteres"
            );
        }

        if (servicio.getDescripcion() == null ||
            servicio.getDescripcion().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La descripcion es obligatoria"
            );
        }

        if (servicio.getDireccion() == null ||
            servicio.getDireccion().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La direccion es obligatoria"
            );
        }

        if (servicio.getDireccion().trim().length() > 255) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La direccion no puede superar 255 caracteres"
            );
        }

        if (servicio.getFechaProgramada() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La fecha y hora programada son obligatorias"
            );
        }
    }
}