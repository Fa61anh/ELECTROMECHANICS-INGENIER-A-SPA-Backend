
package com.electrotrack.actividades.service;

import com.electrotrack.actividades.model.Actividades;
import java.util.List;
import java.util.UUID;

public interface ActividadesService {

    List<Actividades> listarActividades();

    Actividades buscarPorId(UUID id);

    List<Actividades> buscarPorServicio(UUID servicioId);

    Actividades registrarActividad(Actividades actividad);
}
