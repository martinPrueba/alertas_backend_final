package com.kim21.alertas.controller;

import com.kim21.alertas.dto.SingularidadMarcarLeidaDTO;
import com.kim21.alertas.dto.SingularidadReportDTO;
import com.kim21.alertas.model.SingularidadEstadisticasVisibleFieldModel;
import com.kim21.alertas.model.SingularidadesEstadisticasModel;
import com.kim21.alertas.model.SingularidadesVisibleFieldFilterModel;
import com.kim21.alertas.service.SingularidadesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/singularidades")
@RequiredArgsConstructor
public class SingularidadesController
{

    private final SingularidadesService singularidadesService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllSingularidades()
    {
        return singularidadesService.findAllSingularidades();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSingularidadById(@PathVariable Integer id)
    {
        return singularidadesService.findSingularidadById(id);
    }


    @PostMapping("/marcar-leida")
    public ResponseEntity<?> marcarSingularidadComoLeida(@RequestBody SingularidadMarcarLeidaDTO dto)
    {
        return singularidadesService.marcarSingularidadComoLeida(dto);
    }

    @GetMapping("/get-procesos")
    public ResponseEntity<?> getProcesos()
    {
        return singularidadesService.getProcesos();
    }

    @GetMapping("/get-activos")
    public ResponseEntity<?> getActivos()
    {
        return singularidadesService.getActivos();
    }

    @PostMapping("/filter-dynamic")
    public ResponseEntity<?> filtrarSingularidades(@RequestBody Map<String, Object> filtros)
    {
        return singularidadesService.filtrarDinamico(filtros);
    }

    // Visible fields para estadisticas de singularidades
    @GetMapping("/estadisticas-visible-fields")
    public ResponseEntity<?> getAllEstadisticasVisibleFields()
    {
        return singularidadesService.getAllSingularidadEstadisticasVisibleFields();
    }

    @PostMapping("/estadisticas-visible-fields")
    public ResponseEntity<?> createEstadisticasVisibleField(@RequestBody SingularidadEstadisticasVisibleFieldModel body)
    {
        return singularidadesService.createSingularidadEstadisticasVisibleField(body);
    }

    @PutMapping("/estadisticas-visible-fields/{id}")
    public ResponseEntity<?> updateEstadisticasVisibleField(@PathVariable Long id, @RequestBody SingularidadEstadisticasVisibleFieldModel body)
    {
        return singularidadesService.updateSingularidadEstadisticasVisibleField(id, body);
    }

    @DeleteMapping("/estadisticas-visible-fields/{id}")
    public ResponseEntity<?> deleteEstadisticasVisibleField(@PathVariable Long id)
    {
        return singularidadesService.deleteSingularidadEstadisticasVisibleField(id);
    }

    // Visible fields filter config
    @GetMapping("/visible-fields-filter")
    public ResponseEntity<?> getAllVisibleFieldFilters()
    {
        return singularidadesService.getAllSingularidadesVisibleFieldFilters();
    }

    @PostMapping("/visible-fields-filter")
    public ResponseEntity<?> createVisibleFieldFilter(@RequestBody SingularidadesVisibleFieldFilterModel body)
    {
        return singularidadesService.createSingularidadesVisibleFieldFilter(body);
    }

    @PutMapping("/visible-fields-filter/{id}")
    public ResponseEntity<?> updateVisibleFieldFilter(@PathVariable Long id, @RequestBody SingularidadesVisibleFieldFilterModel body)
    {
        return singularidadesService.updateSingularidadesVisibleFieldFilter(id, body);
    }

    @DeleteMapping("/visible-fields-filter/{id}")
    public ResponseEntity<?> deleteVisibleFieldFilter(@PathVariable Long id)
    {
        return singularidadesService.deleteSingularidadesVisibleFieldFilter(id);
    }

    // CRUD singularidadesestadisticas
    @GetMapping("/estadisticas")
    public ResponseEntity<?> getAllEstadisticas()
    {
        return singularidadesService.getAllSingularidadesEstadisticas();
    }

    @GetMapping("/estadisticas/{id}")
    public ResponseEntity<?> getEstadisticasById(@PathVariable Integer id)
    {
        return singularidadesService.getSingularidadesEstadisticasById(id);
    }

    @PostMapping("/estadisticas")
    public ResponseEntity<?> createEstadisticas(@RequestBody SingularidadesEstadisticasModel body)
    {
        return singularidadesService.createSingularidadesEstadisticas(body);
    }

    @PutMapping("/estadisticas/{id}")
    public ResponseEntity<?> updateEstadisticas(@PathVariable Integer id, @RequestBody SingularidadesEstadisticasModel body)
    {
        return singularidadesService.updateSingularidadesEstadisticas(id, body);
    }

    @DeleteMapping("/estadisticas/{id}")
    public ResponseEntity<?> deleteEstadisticas(@PathVariable Integer id)
    {
        return singularidadesService.deleteSingularidadesEstadisticas(id);
    }

    @GetMapping("/get-tipos")
    public ResponseEntity<?> getTiposSingularidades()
    {
        return singularidadesService.getTiposSingularidades();
    }

    @PostMapping("/reporte-singularidades-dynamic")
    public ResponseEntity<?> reportSingularidadesDynamic(@RequestBody SingularidadReportDTO dto)
    {
        return singularidadesService.reportSingularidadesDynamic(dto);
    }
}
