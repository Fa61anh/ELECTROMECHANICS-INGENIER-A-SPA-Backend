package com.electrotrack.servicios.service;

import com.electrotrack.servicios.model.Servicio;
import java.util.List;
import java.util.UUID;

public interface ServicioService {

    List<Servicio> listarServicios();

    Servicio buscarPorId(UUID id);

    Servicio crearServicio(Servicio servicio);

    Servicio asignarTecnico(UUID id, UUID tecnicoId);

    Servicio actualizarEstado(UUID id, String estado);

    Servicio actualizarServicio(UUID id, Servicio datos);

    void eliminarServicio(UUID id);
}
