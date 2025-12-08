package com.kim21.alertas.controller;

import com.kim21.alertas.model.SingularidadEstadisticasVisibleFieldModel;
import com.kim21.alertas.service.VisibleFieldsConfigSingularidadesEstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/singularidades/estadisticas-visible-fields/config")
@RequiredArgsConstructor
public class VisibleFieldsConfigSingularidadesEstadisticasController
{
    private final VisibleFieldsConfigSingularidadesEstadisticasService service;

    @GetMapping
    public ResponseEntity<?> getAll()
    {
        return service.getAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SingularidadEstadisticasVisibleFieldModel body)
    {
        return service.create(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SingularidadEstadisticasVisibleFieldModel body)
    {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id)
    {
        return service.delete(id);
    }
}
