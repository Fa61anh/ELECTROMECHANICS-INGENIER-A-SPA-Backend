
package com.electrotrack.jornada.service;

import com.electrotrack.jornada.model.Jornada;
import com.electrotrack.jornada.repository.JornadaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class JornadaServiceImpl implements JornadaService {

    private final JornadaRepository jornadaRepository;

    public JornadaServiceImpl(JornadaRepository jornadaRepository) {
        this.jornadaRepository = jornadaRepository;
    }

    @Override
    public Jornada iniciarJornada(UUID tecnicoId, Double latitud, Double longitud) {

        if (tecnicoId == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El ID del técnico es obligatorio"
            );
        }

        validarUbicacion(latitud, longitud);

        boolean jornadaActiva = jornadaRepository.findAll().stream()
            .anyMatch(j -> tecnicoId.equals(j.getTecnicoId())
                && "EN_CURSO".equals(j.getEstado()));

        if (jornadaActiva) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "El técnico ya tiene una jornada activa"
            );
        }

        Jornada jornada = new Jornada();

        jornada.setId(null);
        jornada.setTecnicoId(tecnicoId);
        jornada.setFechaHoraInicio(LocalDateTime.now());
        jornada.setLatitudInicio(latitud);
        jornada.setLongitudInicio(longitud);
        jornada.setEstado("EN_CURSO");

        return jornadaRepository.save(jornada);
    }

    @Override
    public Jornada finalizarJornada(UUID jornadaId, Double latitud, Double longitud) {

        validarUbicacion(latitud, longitud);

        Jornada jornada = buscarPorId(jornadaId);

        if (!"EN_CURSO".equals(jornada.getEstado())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "La jornada ya está finalizada"
            );
        }

        jornada.setFechaHoraFin(LocalDateTime.now());
        jornada.setLatitudFin(latitud);
        jornada.setLongitudFin(longitud);
        jornada.setEstado("FINALIZADA");

        return jornadaRepository.save(jornada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jornada> listarJornadas() {
        return jornadaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jornada> listarPorTecnico(UUID tecnicoId) {
        return jornadaRepository.findAll().stream()
            .filter(j -> tecnicoId.equals(j.getTecnicoId()))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Jornada buscarPorId(UUID id) {
        return jornadaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Jornada no encontrada"
            ));
    }

    private void validarUbicacion(Double latitud, Double longitud) {

        if (latitud == null || longitud == null
                || !Double.isFinite(latitud)
                || !Double.isFinite(longitud)
                || latitud < -90 || latitud > 90
                || longitud < -180 || longitud > 180) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Ubicación GPS inválida"
            );
        }
    }
}
