package com.kim21.alertas.controller;

import com.kim21.alertas.model.SingularidadesVisibleFieldFilterModel;
import com.kim21.alertas.service.VisibleFieldsFilterConfigSingularidadesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/singularidades/visible-fields-filter/config")
@RequiredArgsConstructor
public class VisibleFieldsFilterConfigSingularidadesController
{
    private final VisibleFieldsFilterConfigSingularidadesService service;

    @GetMapping
    public ResponseEntity<?> getAll()
    {
        return service.getAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SingularidadesVisibleFieldFilterModel body)
    {
        return service.create(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SingularidadesVisibleFieldFilterModel body)
    {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id)
    {
        return service.delete(id);
    }

    
}
