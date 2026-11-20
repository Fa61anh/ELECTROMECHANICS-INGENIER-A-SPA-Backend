package com.electrotrack.servicios.controller;

import com.electrotrack.servicios.model.Servicio;
import com.electrotrack.servicios.service.ServicioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public List<Servicio> listarServicios() {
        return servicioService.listarServicios();
    }

    @GetMapping("/{id}")
    public Servicio buscarPorId(@PathVariable UUID id) {
        return servicioService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Servicio crearServicio(@RequestBody Servicio servicio) {
        return servicioService.crearServicio(servicio);
    }

    @PutMapping("/{id}/asignar")
    public Servicio asignarTecnico(
            @PathVariable UUID id,
            @RequestBody Map<String, UUID> datos) {

        return servicioService.asignarTecnico(
            id, datos.get("tecnicoId")
        );
    }

    @PatchMapping("/{id}/estado")
    public Servicio actualizarEstado(
            @PathVariable UUID id,
            @RequestBody Map<String, String> datos) {

        return servicioService.actualizarEstado(
            id, datos.get("estado")
        );
    }

    @PutMapping("/{id}")
    public Servicio actualizarServicio(
            @PathVariable UUID id,
            @RequestBody Servicio servicio) {

        return servicioService.actualizarServicio(id, servicio);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarServicio(@PathVariable UUID id) {
        servicioService.eliminarServicio(id);
    }
}
