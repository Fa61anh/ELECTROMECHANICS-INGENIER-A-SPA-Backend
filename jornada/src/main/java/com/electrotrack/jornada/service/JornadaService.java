package com.electrotrack.jornada.service;

import com.electrotrack.jornada.model.Jornada;
import java.util.List;
import java.util.UUID;

public interface JornadaService {

    Jornada iniciarJornada(UUID tecnicoId, Double latitud, Double longitud);

    Jornada finalizarJornada(UUID jornadaId, Double latitud, Double longitud);

    List<Jornada> listarJornadas();

    List<Jornada> listarPorTecnico(UUID tecnicoId);

    Jornada buscarPorId(UUID id);
}
