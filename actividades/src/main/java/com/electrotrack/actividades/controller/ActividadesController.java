
package com.electrotrack.actividades.controller;

import com.electrotrack.actividades.model.Actividades;
import com.electrotrack.actividades.service.ActividadesService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/actividades")
public class ActividadesController {

    private final ActividadesService actividadService;

    public ActividadesController(ActividadesService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping
    public List<Actividades> listarActividades() {
        return actividadService.listarActividades();
    }

    @GetMapping("/{id}")
    public Actividades buscarPorId(@PathVariable UUID id) {
        return actividadService.buscarPorId(id);
    }

    @GetMapping("/servicio/{servicioId}")
    public List<Actividades> buscarPorServicio(
            @PathVariable UUID servicioId) {

        return actividadService.buscarPorServicio(servicioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Actividades registrarActividad(@RequestBody Actividades actividad) {
        return actividadService.registrarActividad(actividad);
    }
}
