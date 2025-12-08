package com.kim21.alertas.controller;

import com.kim21.alertas.model.SingularidadesEstadisticasModel;
import com.kim21.alertas.service.SingularidadEstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/singularidades/estadisticas")
@RequiredArgsConstructor
public class SingularidadEstadisticasController
{
    private final SingularidadEstadisticasService service;

    @GetMapping
    public ResponseEntity<?> getAll()
    {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id)
    {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SingularidadesEstadisticasModel body)
    {
        return service.create(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody SingularidadesEstadisticasModel body)
    {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id)
    {
        return service.delete(id);
    }
}
