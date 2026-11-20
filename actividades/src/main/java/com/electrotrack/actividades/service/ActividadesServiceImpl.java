
package com.electrotrack.actividades.service;

import com.electrotrack.actividades.model.Actividades;
import com.electrotrack.actividades.repository.ActividadesRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActividadesServiceImpl implements ActividadesService {

    private final ActividadesRepository actividadesRepository;

    public ActividadesServiceImpl(ActividadesRepository actividadesRepository) {
        this.actividadesRepository = actividadesRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Actividades> listarActividades() {
        return actividadesRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Actividades buscarPorId(UUID id) {
        return actividadesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Actividad no encontrada"
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Actividades> buscarPorServicio(UUID servicioId) {
        return actividadesRepository.findByServicioId(servicioId);
    }

    @Override
    public Actividades registrarActividad(Actividades actividad) {

        if (actividad == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos son obligatorios"
            );
        }

        if (actividad.getServicioId() == null ||
            actividad.getTecnicoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El servicio y el tecnico son obligatorios"
            );
        }

        if (actividad.getDescripcion() == null ||
            actividad.getDescripcion().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripcion es obligatoria"
            );
        }

        if (actividad.getPorcentajeAvance() == null ||
            actividad.getPorcentajeAvance() < 0 ||
            actividad.getPorcentajeAvance() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El porcentaje debe estar entre 0 y 100"
            );
        }

        actividad.setId(null);
        actividad.setFechaRegistro(null);

        return actividadesRepository.save(actividad);
    }
}
