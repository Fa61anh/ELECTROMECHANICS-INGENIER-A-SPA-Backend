
package com.electrotrack.jornada.controller;

import com.electrotrack.jornada.model.Jornada;
import com.electrotrack.jornada.service.JornadaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jornadas")
public class JornadaController {

    private final JornadaService jornadaService;

    public JornadaController(JornadaService jornadaService) {
        this.jornadaService = jornadaService;
    }

    @PostMapping("/iniciar")
    @ResponseStatus(HttpStatus.CREATED)
    public Jornada iniciarJornada(@RequestBody IniciarJornadaRequest request) {
        return jornadaService.iniciarJornada(
            request.tecnicoId(),
            request.latitud(),
            request.longitud()
        );
    }

    @PutMapping("/{id}/finalizar")
    public Jornada finalizarJornada(
            @PathVariable UUID id,
            @RequestBody FinalizarJornadaRequest request) {

        return jornadaService.finalizarJornada(
            id,
            request.latitud(),
            request.longitud()
        );
    }

    @GetMapping
    public List<Jornada> listarJornadas() {
        return jornadaService.listarJornadas();
    }

    @GetMapping("/tecnico/{tecnicoId}")
    public List<Jornada> listarPorTecnico(@PathVariable UUID tecnicoId) {
        return jornadaService.listarPorTecnico(tecnicoId);
    }

    @GetMapping("/{id}")
    public Jornada buscarPorId(@PathVariable UUID id) {
        return jornadaService.buscarPorId(id);
    }

    public record IniciarJornadaRequest(
        UUID tecnicoId,
        Double latitud,
        Double longitud
    ) {}

    public record FinalizarJornadaRequest(
        Double latitud,
        Double longitud
    ) {}
}
